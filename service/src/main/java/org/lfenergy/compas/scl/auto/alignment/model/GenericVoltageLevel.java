// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentException;
import org.w3c.dom.Element;

import java.util.List;

import static org.lfenergy.compas.scl.auto.alignment.exception.SclAutoAlignmentErrorCode.NO_VOLTAGE_FOUND_ERROR_CODE;

public class GenericVoltageLevel extends AbstractGenericNameEntity<GenericSubstation> {
    private List<GenericBay> bays;

    public GenericVoltageLevel(GenericSubstation parent, Element element) {
        super(parent, element);
    }

    public double getVoltage() {
        return getElementsStream("Voltage")
                .findFirst()
                .map(this::convertStringToDouble)
                .orElseThrow(() -> new SclAutoAlignmentException(NO_VOLTAGE_FOUND_ERROR_CODE,
                        "No Voltage found for VoltageLevel '" + getName() + "'."));
    }

    public List<GenericBay> getBays() {
        if (bays == null) {
            bays = getElementsStream("Bay")
                    .map(element -> new GenericBay(this, element))
                    .toList();
        }
        return bays;
    }
}
