// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.lfenergy.compas.scl.auto.alignment.common.ElementUtil;
import org.w3c.dom.Element;

public abstract class AbstractGenericNameEntity<P extends GenericEntity> extends AbstractGenericEntity<P> {
    protected AbstractGenericNameEntity(P parent, Element element) {
        super(parent, element);
    }

    public String getName() {
        return ElementUtil.getAttributeValue(getElement(), "name");
    }

    @Override
    public String getFullName() {
        return getParent().getFullName() + "/" + getName();
    }
}
