// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.lfenergy.compas.scl.auto.alignment.common.ElementUtil;
import org.w3c.dom.Element;

import java.util.stream.Stream;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_NS_URI;
import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCLXY_PREFIX;

public abstract class AbstractGenericEntity<P extends GenericEntity> implements GenericEntity {
    private final P parent;
    private final Element element;

    protected AbstractGenericEntity(P parent, Element element) {
        this.parent = parent;
        this.element = element;
    }

    protected Stream<Element> getElementsStream(String tagName) {
        return ElementUtil.getElementsStream(element, tagName);
    }

    protected String getAttribute(String attributeName) {
        return ElementUtil.getAttributeValue(element, attributeName);
    }

    protected double convertStringToDouble(Element element) {
        return Double.parseDouble(element.getTextContent());
    }

    @Override
    public Element getElement() {
        return element;
    }

    public P getParent() {
        return parent;
    }

    public long getCoordinate(String fieldName) {
        var value = element.getAttributeNS(SCLXY_NS_URI, fieldName);
        if (!value.isBlank()) {
            return Long.parseLong(value);
        }
        return 0;
    }

    public void setXYCoordinates(long x, long y) {
        element.setAttributeNS(SCLXY_NS_URI, SCLXY_PREFIX + ":x", String.valueOf(x));
        element.setAttributeNS(SCLXY_NS_URI, SCLXY_PREFIX + ":y", String.valueOf(y));
    }
}
