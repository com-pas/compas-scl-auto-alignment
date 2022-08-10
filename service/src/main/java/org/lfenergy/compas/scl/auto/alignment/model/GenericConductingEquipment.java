// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

import java.util.List;

public class GenericConductingEquipment extends AbstractGenericNameEntity<GenericBay> {
    private List<GenericTerminal> terminals;

    public GenericConductingEquipment(GenericBay parent, Element element) {
        super(parent, element);
    }

    public String getType() {
        return getAttribute("type");
    }

    public List<GenericTerminal> getTerminals() {
        if (terminals == null) {
            terminals = getElementsStream("Terminal")
                    .map(element -> new GenericTerminal(this, element))
                    .toList();
        }
        return terminals;
    }
}
