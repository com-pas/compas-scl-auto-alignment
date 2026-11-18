// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.exception;

public class SclAutoAlignmentErrorCode {
    SclAutoAlignmentErrorCode() {
        throw new UnsupportedOperationException("SclAutoAlignmentErrorCode class");
    }

    public static final String NO_SCL_ELEMENT_FOUND_ERROR_CODE = "SAA-0001";
    public static final String SUBSTATION_NOT_FOUND_ERROR_CODE = "SAA-0002";
    public static final String NO_VOLTAGE_FOUND_ERROR_CODE = "SAA-0003";
    public static final String VOLTAGELEVEL_NOT_FOUND_ERROR_CODE = "SAA-0004";
    public static final String INVALID_FEEDER_NODE_ERROR_CODE = "SAA-0005";
}
