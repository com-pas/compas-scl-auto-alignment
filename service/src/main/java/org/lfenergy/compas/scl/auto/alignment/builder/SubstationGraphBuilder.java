// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.builder;

import com.powsybl.sld.model.graphs.NodeFactory;
import com.powsybl.sld.model.graphs.SubstationGraph;
import com.powsybl.sld.model.nodes.FeederNode;
import com.powsybl.sld.model.nodes.Node;
import org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentException;
import org.lfenergy.compas.scl.auto.alignment.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.VOLTAGELEVEL_NOT_FOUND_ERROR_CODE;

public class SubstationGraphBuilder extends AbstractGraphBuilder<SubstationGraph> {
    private final GenericSubstation substation;

    private final Map<String, VoltageLevelGraphBuilder> voltageLevelGraphBuilderMap = new HashMap<>();

    public SubstationGraphBuilder(GenericSubstation substation) {
        this(substation, new HashMap<>());
    }

    public SubstationGraphBuilder(GenericSubstation substation,
                                  Map<String, Node> path2Node) {
        super(path2Node);
        this.substation = substation;

        setGraph(SubstationGraph.create(substation.getFullName()));

        createSubstation();
    }

    private void createSubstation() {
        substation.getVoltageLevels().forEach(this::createVoltageLevelGraph);
        substation.getPowerTransformers().forEach(this::createPowerTransformer);
    }

    private void createVoltageLevelGraph(GenericVoltageLevel voltageLevel) {
        var voltageLevelBuilder = new VoltageLevelGraphBuilder(voltageLevel, substation, getPath2Node(), getGraph());
        getGraph().addVoltageLevel(voltageLevelBuilder.getGraph());
        voltageLevelGraphBuilderMap.put(voltageLevel.getFullName(), voltageLevelBuilder);
    }

    private VoltageLevelGraphBuilder getVoltageLevelBuilder(GenericTransformerWinding transformerWinding) {
        var connectivityNode = transformerWinding.getTerminals().get(0).getConnectivityNode();
        return substation.getVoltageLevels().stream()
                .map(GenericVoltageLevel::getBays)
                .flatMap(List::stream)
                .map(GenericBay::getConductingEquipments)
                .flatMap(List::stream)
                .map(GenericConductingEquipment::getTerminals)
                .flatMap(List::stream)
                .filter(terminal -> connectivityNode.equals(terminal.getConnectivityNode()))
                .findFirst()
                .map(terminal -> ((GenericConductingEquipment) terminal.getParent()).getParent().getParent())
                .map(genericVoltageLevel -> voltageLevelGraphBuilderMap.get(genericVoltageLevel.getFullName()))
                .orElseThrow(() -> new SclAutoAlignmentException(VOLTAGELEVEL_NOT_FOUND_ERROR_CODE, "No voltage level found."));
    }

    private void createPowerTransformer(GenericPowerTransformer powerTransformer) {
        if (powerTransformer.isFeeder2WT()) {
            var tws = powerTransformer.getTransformerWindings();
            getGraph().addMultiTermNode(
                    NodeFactory.createMiddle2WTNode(getGraph(),
                            powerTransformer.getFullName(),
                            powerTransformer.getFullName(),
                            getFeederNode(tws.get(0)),
                            getFeederNode(tws.get(1)),
                            getVoltageLevelBuilder(tws.get(0)).getGraph().getVoltageLevelInfos(),
                            getVoltageLevelBuilder(tws.get(1)).getGraph().getVoltageLevelInfos(),
                            false));
        } else if (powerTransformer.isFeeder3WT()) {
            var tws = powerTransformer.getTransformerWindings();
            getGraph().addMultiTermNode(
                    NodeFactory.createMiddle3WTNode(getGraph(),
                            powerTransformer.getFullName(),
                            powerTransformer.getFullName(),
                            getFeederNode(tws.get(0)),
                            getFeederNode(tws.get(1)),
                            getFeederNode(tws.get(2))));
        }
    }

    private FeederNode getFeederNode(GenericTransformerWinding transformerWinding) {
        var connectivityNode = transformerWinding.getTerminals().get(0).getConnectivityNode();
        return (FeederNode) getNodeByPath(connectivityNode);
    }
}
