
// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1.encoder;

import org.lfenergy.compas.core.websocket.AbstractEncoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignResponse;

public class CreateWsResponseEncoder extends AbstractEncoder<SclAutoAlignResponse> {
    public String encode(SclAutoAlignResponse jaxbObject) {
    return WebsocketSupport.encode(jaxbObject, SclAutoAlignResponse.class);
    }
}