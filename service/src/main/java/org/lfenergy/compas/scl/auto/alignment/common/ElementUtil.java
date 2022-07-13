// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.common;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_NS_URI;

public final class ElementUtil {
    ElementUtil() {
        throw new UnsupportedOperationException("ElementUtil class");
    }

    public static Stream<Element> getElementsStream(Element element, String tagName) {
        var nodeList = element.getElementsByTagNameNS(SCL_NS_URI, tagName);
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .filter(Element.class::isInstance)
                .map(Element.class::cast);
    }

    public static String getAttributeValue(Element element, String attributeName) {
        String value = element.getAttribute(attributeName);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return null;
    }
}
