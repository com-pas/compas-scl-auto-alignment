// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.IntStream;

public class GenericBay extends AbstractGenericNameEntity<GenericVoltageLevel> {
    private List<GenericConnectivityNode> connectivityNodes;
    private List<GenericConductingEquipment> conductingEquipments;

    public GenericBay(GenericVoltageLevel parent, Element element) {
        super(parent, element);
    }

    public boolean isBusbar() {
        return getConnectivityNodes().size() == 1 && getNrOfChilderen() == 1;
    }

    private long getNrOfChilderen() {
        var nodeList = getElement().getChildNodes();
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .filter(Element.class::isInstance)
                .count();
    }

    public List<GenericConnectivityNode> getConnectivityNodes() {
        if (connectivityNodes == null) {
            connectivityNodes = getElementsStream("ConnectivityNode")
                    .map(element -> new GenericConnectivityNode(this, element))
                    .toList();
        }
        return connectivityNodes;
    }

    public List<GenericConductingEquipment> getConductingEquipments() {
        if (conductingEquipments == null) {
            conductingEquipments = getElementsStream("ConductingEquipment")
                    .map(element -> new GenericConductingEquipment(this, element))
                    .toList();
        }
        return conductingEquipments;
    }
}
