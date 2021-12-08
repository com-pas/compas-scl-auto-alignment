// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import com.powsybl.sld.model.FeederWithSideNode;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GenericPowerTransformer extends AbstractGenericNameEntity<GenericSubstation> {
    private List<GenericTransformerWinding> transformerWindings;

    public GenericPowerTransformer(GenericSubstation parent, Element element) {
        super(parent, element);
    }

    public List<GenericTransformerWinding> getTransformerWindings() {
        if (transformerWindings == null) {
            transformerWindings = getElementsStream("TransformerWinding")
                    .map(element -> new GenericTransformerWinding(this, element))
                    .collect(Collectors.toList());
        }
        return transformerWindings;
    }

    public GenericTransformerWinding getTransformerWindingByConnectivityNode(String connectivityNode) {
        return getTransformerWindings()
                .stream()
                .map(GenericTransformerWinding::getTerminals)
                .flatMap(Collection::stream)
                .filter(terminal -> connectivityNode.equals(terminal.getConnectivityNode()))
                .findFirst()
                .map(terminal -> ((GenericTransformerWinding) terminal.getParent()))
                .orElse(null);
    }

    public boolean isFeeder2WT() {
        return getTransformerWindings().size() == 2;
    }

    public boolean isFeeder3WT() {
        return getTransformerWindings().size() == 3;
    }

    public FeederWithSideNode.Side getSide(String connectivityNode) {
        var ptw = getTransformerWindingByConnectivityNode(connectivityNode);
        var index = getTransformerWindings().indexOf(ptw);

        switch (index) {
            case 0:
                return FeederWithSideNode.Side.ONE;
            case 1:
                return FeederWithSideNode.Side.TWO;
            case 2:
                return FeederWithSideNode.Side.THREE;
            default:
                return null;
        }
    }
}
