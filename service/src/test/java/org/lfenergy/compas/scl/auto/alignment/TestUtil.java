// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment;

import javanet.staxutils.SimpleNamespaceContext;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.*;

public final class TestUtil {
    private TestUtil() {
        // Only static methods.
    }

    public static Element toElement(String sclData) throws IOException {
        var converter = new ElementConverter();
        try (var inputStream = new BufferedInputStream(
                new ByteArrayInputStream(sclData.getBytes(StandardCharsets.UTF_8)))) {
            return converter.convertToElement(inputStream, SCL_ELEMENT_NAME, SCL_NS_URI);
        }
    }

    public static Element readSCLElement(String filename) throws IOException {
        var sclData = readSCL(filename);
        return toElement(sclData);
    }

    public static String readSCL(String filename) throws IOException {
        return readFile("/scl/" + filename);
    }

    public static String readFile(String filename) throws IOException {
        try (var inputStream = TestUtil.class.getResourceAsStream(filename)) {
            assert inputStream != null;

            return new String(inputStream.readAllBytes());
        }
    }

    public static void writeFile(String fileName, String data) {
        var file = new File("target", fileName);
        try (var fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            fw.write(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void assertXYCoordinates(Element rootElement, String xPathExpression, Integer x, Integer y) throws Exception {
        var xPath = XPathFactory.newInstance().newXPath();

        SimpleNamespaceContext nsCtx = new SimpleNamespaceContext();
        nsCtx.setPrefix("scl", SCL_NS_URI);
        xPath.setNamespaceContext(nsCtx);

        var nodeList = (NodeList) xPath.compile(xPathExpression).evaluate(rootElement.getOwnerDocument(), XPathConstants.NODESET);
        assertEquals(1, nodeList.getLength());

        var element = (Element) nodeList.item(0);
        assertNotNull(element);
        assertEquals(x.toString(), element.getAttributeNS(SCLXY_NS_URI, "x"));
        assertEquals(y.toString(), element.getAttributeNS(SCLXY_NS_URI, "y"));
    }
}
