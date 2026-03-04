// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1;

import io.quarkus.security.Authenticated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.lfenergy.compas.scl.auto.alignment.rest.UserInfoProperties;
import org.lfenergy.compas.scl.auto.alignment.websocket.v1.event.model.SclAutoAlignmentEventRequest;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import static org.lfenergy.compas.core.websocket.WebsocketSupport.handleException;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/auto-ws/alignment/v1",
    decoders = {org.lfenergy.compas.scl.auto.alignment.websocket.v1.decoder.CreateWsRequestDecoder.class},
    encoders = {org.lfenergy.compas.scl.auto.alignment.websocket.v1.encoder.CreateWsResponseEncoder.class})
public class SclAutoAlignmentWebsocket {
    private static final Logger LOGGER = LogManager.getLogger(SclAutoAlignmentWebsocket.class);
    private final EventBus eventBus;
    private final JsonWebToken jsonWebToken;
    private final UserInfoProperties userInfoProperties;
   
    @Inject
    public SclAutoAlignmentWebsocket(EventBus eventBus,
                                     JsonWebToken jsonWebToken,
                                     UserInfoProperties userInfoProperties) {
        this.eventBus = eventBus;
        this.jsonWebToken = jsonWebToken;
        this.userInfoProperties = userInfoProperties;
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("WebSocket opened: {}", session.getId());
    }

    @OnMessage
    public void onAutoAlignMessage(Session session, SclAutoAlignRequest request) {
        LOGGER.info("Received WebSocket message (SclAutoAlignRequest) from session {}.", session.getId());
        String who = jsonWebToken.getClaim(userInfoProperties.who());
        eventBus.send("auto-align-ws", new SclAutoAlignmentEventRequest(session, request, who));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.warn("WebSocket error in session {}", session.getId(), throwable);
        handleException(session, throwable);
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.debug("WebSocket closed: {}", session.getId());
    }
}