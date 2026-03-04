// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1.decoder;

import org.lfenergy.compas.core.websocket.AbstractDecoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;

public class CreateWsRequestDecoder extends AbstractDecoder<SclAutoAlignRequest> {
    @Override
    public boolean willDecode(String message) {
        return message != null;
    }

    @Override
    public SclAutoAlignRequest decode(String message) {
        return WebsocketSupport.decode(message, SclAutoAlignRequest.class);
    }
}