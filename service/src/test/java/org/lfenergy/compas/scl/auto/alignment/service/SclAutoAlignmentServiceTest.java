// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class SclAutoAlignmentServiceTest {

    private SclAutoAlignmentService sclAutoAlignmentService;

    private final ElementConverter converter = new ElementConverter();

    @BeforeEach
    void beforeEach() {
        sclAutoAlignmentService = new SclAutoAlignmentService(converter);
    }

    @Test
    void updateSCL_WhenPassingCase1_ThenUpdatedSCLReturned() throws Exception {
        var filename = "scl-1";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.updateSCL(sclString, List.of("AA1"), "Mr. Editor");
        assertNotNull(result);
        writeFile(filename + "-updated-service.scd", result);

        var rootElement = toElement(result);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='J1']", 2, 4);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='J1']/scl:Bay[@name='BusBar A']", 1, 14);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='J1']/scl:Bay[@name='Bay 1S']", 2, 12);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='J1']/scl:Bay[@name='Bay 1S']/scl:ConductingEquipment[@name='CBR1']", 1, 1);
    }

    @Test
    void updateSCL_WhenPassingCase2_ThenUpdatedSCLReturned() throws Exception {
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.updateSCL(sclString, List.of("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4"), "Mr. Editor");
        assertNotNull(result);
        writeFile(filename + "-updated-service.scd", result);

        var rootElement = toElement(result);
        assertXYCoordinates(rootElement, "//scl:PowerTransformer[@name='T4']", 27, 3);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='S1 380kV']", 2, 7);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='S1 380kV']/scl:Bay[@name='BUSBAR10']", 1, 10);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='S1 380kV']/scl:Bay[@name='BAY_T4_2']", 1, 4);
        assertXYCoordinates(rootElement, "//scl:VoltageLevel[@name='S1 380kV']/scl:Bay[@name='BAY_T4_2']/scl:ConductingEquipment[@name='BREAKER25']", 1, 2);
    }

    @Test
    void getSVG_WhenPassingCase1_ThenSVGReturned() throws IOException {
        // Nice addition to the updateSCL Test to have a visual output from PowSyBl and check how their drawing looks.
        var filename = "scl-1";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.getSVG(sclString, "AA1");
        assertNotNull(result);
        writeFile(filename + ".svg", result);
    }

    @Test
    void getSVG_WhenPassingCase2_ThenSVGReturned() throws IOException {
        // Nice addition to the updateSCL Test to have a visual output from PowSyBl and check how their drawing looks.
        var filename = "scl-2";
        var sclString = readSCL(filename + ".scd");

        var result = sclAutoAlignmentService.getSVG(sclString, "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4");
        assertNotNull(result);
        writeFile(filename + ".svg", result);
    }

    @Test
    void createJson_WhenPassingCase1_ThenJsonReturned() throws IOException {
        // This just tests if the SCD File can be processed, because most of the work is done in PowSyBl.
        // But this way we know the GraphBuilders are still working and not missing electric components.
        var filename = "scl-1";
        var substationName = "AA1";
        var sclData = readSCL(filename + ".scd");
        var scl = sclAutoAlignmentService.readSCL(sclData);
        var substationBuilder = sclAutoAlignmentService.createSubstationGraphBuilder(scl, substationName);

        var result = sclAutoAlignmentService.createJson(substationBuilder);
        assertNotNull(result);
        writeFile(filename + ".json", result);
    }

    @Test
    void createJson_WhenPassingCase2_ThenJsonReturned() throws IOException {
        // This just tests if the SCD File can be processed, because most of the work is done in PowSyBl.
        // But this way we know the GraphBuilders are still working and not missing electric components.
        var filename = "scl-2";
        var substationName = "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4";
        var sclData = readSCL(filename + ".scd");
        var scl = sclAutoAlignmentService.readSCL(sclData);
        var substationBuilder = sclAutoAlignmentService.createSubstationGraphBuilder(scl, substationName);

        var result = sclAutoAlignmentService.createJson(substationBuilder);
        assertNotNull(result);
        writeFile(filename + ".json", result);
    }
}