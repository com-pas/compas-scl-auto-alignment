// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1.event;

import io.quarkus.vertx.ConsumeEvent;
import org.lfenergy.compas.scl.auto.alignment.service.SclAutoAlignmentService;
import org.lfenergy.compas.scl.auto.alignment.websocket.v1.event.model.SclAutoAlignmentEventRequest;
import org.lfenergy.compas.core.websocket.WebsocketHandler;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SclAutoAlignmentEventHandler {
    private final SclAutoAlignmentService sclAutoAlignmentService;

    @Inject
    public SclAutoAlignmentEventHandler(SclAutoAlignmentService sclAutoAlignmentService) {
        this.sclAutoAlignmentService = sclAutoAlignmentService;
    }

    @ConsumeEvent(value = "auto-align-ws", blocking = true)
    public void mapWebsocketsEvent(SclAutoAlignmentEventRequest request) {
        new WebsocketHandler<SclAutoAlignResponse>().execute(request.getSession(), () -> {
            var response = new SclAutoAlignResponse();
            response.setSclData(sclAutoAlignmentService.updateSCL(
                request.getRequest().getSclData(),
                request.getRequest().getSubstationNames(),
                request.getWho()
            ));
            return response;
        });
    }
}
