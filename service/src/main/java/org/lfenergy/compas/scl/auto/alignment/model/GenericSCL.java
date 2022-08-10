// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.apache.commons.lang3.StringUtils;
import org.lfenergy.compas.scl.auto.alignment.common.ElementUtil;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Optional;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.*;

public class GenericSCL implements GenericEntity {
    private final Element element;
    private List<GenericSubstation> substations;

    public GenericSCL(Element element) {
        this.element = element;

        if (!element.hasAttribute("xmlns:" + SCLXY_PREFIX)) {
            element.setAttribute("xmlns:" + SCLXY_PREFIX, SCLXY_NS_URI);
        }
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public String getFullName() {
        return "";
    }

    public List<GenericSubstation> getSubstations() {
        if (substations == null) {
            substations = ElementUtil.getElementsStream(element, "Substation")
                    .map(substationElement -> new GenericSubstation(this, substationElement))
                    .toList();
        }
        return substations;
    }

    public Optional<GenericSubstation> getSubstation(String substationName) {
        if (StringUtils.isNotBlank(substationName)) {
            return getSubstations().stream()
                    .filter(substation -> substationName.equals(substation.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    public GenericHeader getOrCreateHeader() {
        var document = getElement().getOwnerDocument();
        return new GenericHeader(this,
                ElementUtil.getElementsStream(element, "Header")
                        .findFirst()
                        .orElseGet(() -> {
                            Element newHeader = document.createElementNS(SCL_NS_URI, "Header");
                            element.appendChild(newHeader);
                            return newHeader;
                        }));
    }
}
