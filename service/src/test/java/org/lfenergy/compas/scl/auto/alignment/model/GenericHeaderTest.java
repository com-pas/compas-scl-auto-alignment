// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.auto.alignment.common.ElementUtil;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericSCLTest.BASIC_SCD_FILENAME;

class GenericHeaderTest {
    private static final String USERNAME = "Test User";
    private static final String MESSAGE = "Some message";

    private GenericHeader header;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement(BASIC_SCD_FILENAME));
        header = scl.getOrCreateHeader();
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(header.getElement());
    }

    @Test
    void getFullName_WhenCalled_ThenForSCLEmptyStringReturned() {
        var result = header.getFullName();
        assertEquals("", result);
    }

    @Test
    void getVersion_WhenCalled_ThenTypeReturned() {
        assertEquals("1", header.getVersion());
    }

    @Test
    void addHistoryItem_WhenCalled_ThenAllElementsAreCreated() {
        header.addHistoryItem(USERNAME, MESSAGE);

        var history = ElementUtil.getElementsStream(header.getElement(), "History").findFirst().orElse(null);
        assertNotNull(history);
        var historyItems = ElementUtil.getElementsStream(header.getElement(), "Hitem").toList();
        var historyItem = historyItems.get(historyItems.size() - 1);
        assertNotNull(historyItem);
        assertNotNull(historyItem.getAttribute("revision"));
        assertEquals(USERNAME, historyItem.getAttribute("who"));
        assertEquals(MESSAGE, historyItem.getAttribute("what"));
    }

    @Test
    void addHistoryItem_WhenCalledNotContainingHistory_ThenAllElementsAreCreated() {
        var xml = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" version=\"2007\" revision=\"B\"></SCL>";
        var converter = new ElementConverter();
        var sclElement = converter.convertToElement(xml, SCL_ELEMENT_NAME, SCL_NS_URI);
        var scl = new GenericSCL(sclElement);
        header = scl.getOrCreateHeader();

        header.addHistoryItem(USERNAME, MESSAGE);

        var history = ElementUtil.getElementsStream(header.getElement(), "History").findFirst().orElse(null);
        assertNotNull(history);
        var historyItem = ElementUtil.getElementsStream(header.getElement(), "Hitem").findFirst().orElse(null);
        assertNotNull(historyItem);
    }
}