// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1.decoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.exception.CompasException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.core.commons.exception.CompasErrorCode.WEBSOCKET_DECODER_ERROR_CODE;

class CreateWsRequestDecoderTest {
    private CreateWsRequestDecoder decoder;

    @BeforeEach
    void init() {
        decoder = new CreateWsRequestDecoder();
        decoder.init(null);
    }


    @Test
    void willDecode_WhenCalledWithString_ThenTrueReturned() {
        assertTrue(decoder.willDecode(""));
        assertTrue(decoder.willDecode("Some text"));
    }


    @Test
    void willDecode_WhenCalledWithNull_ThenFalseReturned() {
        assertFalse(decoder.willDecode(null));
    }


    @Test
    void decode_WhenCalledWithCorrectRequestXML_ThenStringConvertedToObject() {
        // Arrange
        final String substationName = "test";
        final String sclData = "<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\" version=\"2007\" revision=\"B\" release=\"4\" xmlns:compas=\"https://www.lfenergy.org/compas/extension/v1\"><Private type=\"compas_scl\"/>\n  <Header id=\"project\"/>\n<Substation name=\"test\" desc=\"\"/></SCL>";
        final String message =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<saa:SclAutoAlignRequest xmlns:saa=\"https://www.lfenergy.org/compas/SclAutoAlignmentService/v1\">" +
                "  <saa:SubstationName>" + substationName + "</saa:SubstationName>" +
                "  <saa:SclData><![CDATA[" + sclData + "]]></saa:SclData>" +
                "</saa:SclAutoAlignRequest>";

        var result = decoder.decode(message);

        assertNotNull(result, "Result should not be null");
        assertEquals(substationName, result.getSubstationNames().get(0), "SubstationName should match");
        assertEquals(sclData, result.getSclData(), "SclData should match");
    }


    @Test
    void decode_WhenCalledWithWrongXMLType_ThenExceptionThrown() {
        String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<InvalidRequest>"
                + "  <SomeField>value</SomeField>"
                + "</InvalidRequest>";

        var exception = assertThrows(CompasException.class, () -> decoder.decode(message));
        assertEquals(WEBSOCKET_DECODER_ERROR_CODE, exception.getErrorCode());
        assertNotNull(exception.getCause());
    }

    @AfterEach
    void destroy() {
        decoder.destroy();
    }
}
