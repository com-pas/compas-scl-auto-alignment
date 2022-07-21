// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.auto.alignment.TestUtil;
import org.lfenergy.compas.scl.auto.alignment.model.GenericSCL;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.*;

class SclAutoAlignmentEnricherTest {
    @Test
    void enrich_WhenPassingCase1_ThenSCDUpdated() throws Exception {
        // The JSON file used here is a copy created by the testcase
        // SclAutoAlignmentServiceTest#createJson_WhenPassingCase1_ThenJsonReturned
        var filename = "scl-1";
        var jsonString = readFile("/json/" + filename + ".json");
        var scl = new GenericSCL(readSCLElement(filename + ".scd"));

        var sclAutoAlignmentEnricher = new SclAutoAlignmentEnricher(scl, jsonString);
        sclAutoAlignmentEnricher.enrich();

        assertNotNull(scl.getElement());
        writeFile(filename + "-updated.scd", scl);

        assertXYCoordinates(scl.getElement(), "//scl:VoltageLevel[@name='J1']", 2, 4);
        assertXYCoordinates(scl.getElement(), "//scl:Bay[@name='BusBar A']", 1, 15);
    }

    @Test
    void enrich_WhenPassingCase2_ThenSCDUpdated() throws IOException {
        // The JSON file used here is a copy created by the testcase
        // SclAutoAlignmentServiceTest#createJson_WhenPassingCase2_ThenJsonReturned
        var filename = "scl-2";
        var jsonString = readFile("/json/" + filename + ".json");
        var scl = new GenericSCL(readSCLElement(filename + ".scd"));

        var sclAutoAlignmentEnricher = new SclAutoAlignmentEnricher(scl, jsonString);
        sclAutoAlignmentEnricher.enrich();

        assertNotNull(scl.getElement());
        writeFile(filename + "-updated.scd", scl);
    }

    private Element readSCLElement(String filename) throws IOException {
        var sclData = readSCL(filename);

        ElementConverter converter = new ElementConverter();
        return converter.convertToElement(new BufferedInputStream(
                new ByteArrayInputStream(sclData.getBytes(StandardCharsets.UTF_8))), SCL_ELEMENT_NAME, SCL_NS_URI);
    }

    private void writeFile(String filename, GenericSCL scl) {
        ElementConverter converter = new ElementConverter();
        var sclData = converter.convertToString(scl.getElement());

        TestUtil.writeFile(filename, sclData);
    }
}