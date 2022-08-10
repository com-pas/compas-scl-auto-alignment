// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.common;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.stream.IntStream;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_NS_URI;

/**
 * Some common methods used in the Auto Alignment Service.
 */
public class CommonUtil {
    CommonUtil() {
        throw new UnsupportedOperationException("CommonUtil class");
    }

    /**
     * Remove attributes from the element related to the namespace SCLXY_NS_URI
     * {@link org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants}.
     * It will also remove the attributes from all child elements.
     *
     * @param element The Element to start from removing the attributes.
     */
    public static void cleanSXYDeclarationAndAttributes(Element element) {
        // First collect the attributes to be removed.
        var attributes = element.getAttributes();
        var attributesToRemove = IntStream.range(0, attributes.getLength())
                .mapToObj(attributes::item)
                .filter(Attr.class::isInstance)
                .map(Attr.class::cast)
                .filter(attr -> SCLXY_NS_URI.equals(attr.getNamespaceURI()))
                .toList();
        // Remove the attribute from the element.
        attributesToRemove.forEach(element::removeAttributeNode);

        // Check if there is a declaration that can be removed
        IntStream.range(0, attributes.getLength())
                .mapToObj(attributes::item)
                .filter(Attr.class::isInstance)
                .map(Attr.class::cast)
                .filter(attr -> SCLXY_NS_URI.equals(attr.getValue()))
                .forEach(element::removeAttributeNode);

        // Next cleanup all the child elements in the same way.
        var nodes = element.getChildNodes();
        IntStream.range(0, nodes.getLength())
                .mapToObj(nodes::item)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .forEach(CommonUtil::cleanSXYDeclarationAndAttributes);
    }
}
