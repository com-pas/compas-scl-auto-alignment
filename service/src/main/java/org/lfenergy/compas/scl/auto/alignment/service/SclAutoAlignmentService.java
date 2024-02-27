// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.layout.HorizontalSubstationLayoutFactory;
import com.powsybl.sld.layout.LayoutParameters;
import com.powsybl.sld.layout.PositionVoltageLevelLayoutFactory;
import com.powsybl.sld.library.ConvergenceComponentLibrary;
import com.powsybl.sld.model.graphs.SubstationGraph;
import com.powsybl.sld.svg.SvgParameters;
import com.powsybl.sld.svg.styles.BasicStyleProvider;
import com.powsybl.sld.svg.DefaultSVGWriter;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.auto.alignment.builder.SubstationGraphBuilder;
import org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentException;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSCL;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.NO_SCL_ELEMENT_FOUND_ERROR_CODE;
import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.SUBSTATION_NOT_FOUND_ERROR_CODE;

@ApplicationScoped
public class SclAutoAlignmentService {
    private final ElementConverter converter;

    @Inject
    public SclAutoAlignmentService(ElementConverter converter) {
        this.converter = converter;
    }

    public String updateSCL(String sclData, List<String> substationNames, String who) {
        var scl = readSCL(sclData);

        substationNames.forEach(substationName -> {
            var substationBuilder = createSubstationGraphBuilder(scl, substationName);

            // Create the JSON With all X/Y Coordinate information.
            var jsonGraphInfo = createJson(substationBuilder);
            // Use that JSON to enrich the passed SCL XML with X/Y Coordinates.
            var enricher = new SclAutoAlignmentEnricher(scl, jsonGraphInfo);
            enricher.enrich();

            // Add an extra History Element to show there was a change.
            scl.getOrCreateHeader().addHistoryItem(who, "Add or replaced the X/Y Coordinates for Substation '" +
                    substationName + "' in the SCL File.");
        });

        return converter.convertToString(scl.getElement());
    }

    public String getSVG(String sclData, String substationName) {
        var scl = readSCL(sclData);
        var substationBuilder = createSubstationGraphBuilder(scl, substationName);

        return createSVG(substationBuilder);
    }

    GenericSCL readSCL(String sclData) {
        // Next convert the String to W3C Document/Element
        var sclElement = converter.convertToElement(new BufferedInputStream(
                new ByteArrayInputStream(sclData.getBytes(StandardCharsets.UTF_8))), SCL_ELEMENT_NAME, SCL_NS_URI);
        if (sclElement == null) {
            throw new SclAutoAlignmentException(NO_SCL_ELEMENT_FOUND_ERROR_CODE, "No valid SCL found in the passed SCL Data.");
        }

        return new GenericSCL(sclElement);
    }

    SubstationGraphBuilder createSubstationGraphBuilder(GenericSCL scl,
                                                        String substationName) {
        return scl.getSubstation(substationName)
                .map(SubstationGraphBuilder::new)
                .orElseThrow(() -> {
                    throw new SclAutoAlignmentException(SUBSTATION_NOT_FOUND_ERROR_CODE,
                            "No substation found with name '" + substationName + "'.");
                });
    }

    String createJson(SubstationGraphBuilder substationGraphBuilder) {
        var graph = substationGraphBuilder.getGraph();
        var layoutParameters = getLayoutParameters();
        configureLayout(graph, layoutParameters);

        var writer = new StringWriter();
        graph.writeJson(writer);
        return writer.toString();
    }


    String createSVG(SubstationGraphBuilder substationGraphBuilder) {
        var graph = substationGraphBuilder.getGraph();
        var layoutParameters = getLayoutParameters();
        configureLayout(graph, layoutParameters);

        var writer = new StringWriter();
        var svgWriter = new DefaultSVGWriter(new ConvergenceComponentLibrary(), layoutParameters, new SvgParameters());
        svgWriter.write(graph, new SclAutoAlignmentDiagramLabelProvider(graph), new BasicStyleProvider(), writer);
        return writer.toString();
    }

    private LayoutParameters getLayoutParameters() {
        return new LayoutParameters()
                .setAdaptCellHeightToContent(true);
    }

    private void configureLayout(SubstationGraph graph, LayoutParameters layoutParameters) {
        new HorizontalSubstationLayoutFactory().create(graph,
                        new PositionVoltageLevelLayoutFactory()
                                .setFeederStacked(false)
                                .setHandleShunts(true))
                .run(layoutParameters);
    }
}
