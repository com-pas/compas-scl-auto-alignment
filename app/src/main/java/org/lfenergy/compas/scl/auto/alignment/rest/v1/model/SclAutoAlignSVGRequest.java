// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.auto.alignment.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.auto.alignment.SclAutoAlignmentConstants.SCL_AUTO_ALIGNMENT_SERVICE_V1_NS_URI;

@Schema(description = "")
@XmlRootElement(name = "SclAutoAlignRequest", namespace = SCL_AUTO_ALIGNMENT_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class SclAutoAlignSVGRequest {
    @Schema(description = "")
    @NotBlank
    @XmlElement(name = "SubstationName", namespace = SCL_AUTO_ALIGNMENT_SERVICE_V1_NS_URI)
    protected String substationName;

    @Schema(description = "")
    @NotBlank
    @XmlElement(name = "SclData", namespace = SCL_AUTO_ALIGNMENT_SERVICE_V1_NS_URI)
    protected String sclData;

    public String getSubstationName() {
        return substationName;
    }

    public void setSubstationName(String substationName) {
        this.substationName = substationName;
    }

    public String getSclData() {
        return sclData;
    }

    public void setSclData(String sclData) {
        this.sclData = sclData;
    }
}
