// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSCLTest.BASIC_SCD_FILENAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSubstationTest.SUBSTATION_NAME;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericVoltageLevelTest.VOLTAGE_LEVEL_NAME;

class AbstractGenericEntityTest {
    private AbstractGenericEntity<GenericSubstation> entity;

    @BeforeEach
    void setup() throws IOException {
        // Use VoltageLevel as example to test generic functionality.
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        var substation = scl.getSubstation(SUBSTATION_NAME).get();
        entity = substation.getVoltageLevel(VOLTAGE_LEVEL_NAME).get();
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(entity.getElement());
    }

    @Test
    void getElementsStream_WhenCallingForKnownElement_ThenReturnElements() {
        var result = entity.getElementsStream("Bay")
                .toList();

        assertEquals(8, result.size());
    }

    @Test
    void getElementsStream_WhenCallingForUnknownElement_ThenReturnEmptyList() {
        var result = entity.getElementsStream("Unknown")
                .toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAttribute_WhenCalledWithKnownAttribute_ThenAttributeReturned() {
        var result = entity.getAttribute("name");

        assertNotNull(result);
        assertEquals(VOLTAGE_LEVEL_NAME, result);
    }

    @Test
    void getAttribute_WhenCalledWithUnknownAttribute_ThenNullReturned() {
        var result = entity.getAttribute("unknown");

        assertNull(result);
    }

    @Test
    void getAttribute_WhenCalledForAttributeWithBlankValue_ThenNullReturned() {
        entity.getElement().setAttribute("name", "    ");
        var result = entity.getAttribute("name");

        assertNull(result);
    }

    @Test
    void setXYCoordinates_WhenCalledToSetXYCoordinates_ThenElementContainsAttributes() {
        var xCoord = 1;
        var yCoord = 2;

        entity.setXYCoordinates(xCoord, yCoord);

        var element = entity.getElement();
        assertEquals(String.valueOf(xCoord), element.getAttributeNS(SCLXY_NS_URI, "x"));
        assertEquals(String.valueOf(yCoord), element.getAttributeNS(SCLXY_NS_URI, "y"));
    }
}