
// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1;

import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.auto.alignment.rest.UserInfoProperties;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;
import  org.lfenergy.compas.scl.auto.alignment.websocket.v1.event.model.SclAutoAlignmentEventRequest;

import jakarta.websocket.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SclAutoAlignmentWebsocketTest {
	private EventBus eventBus;
	private JsonWebToken jsonWebToken;
	private UserInfoProperties userInfoProperties;
	private SclAutoAlignmentWebsocket webSocket;

	@BeforeEach
	void setUp() {
		eventBus = mock(EventBus.class);
		jsonWebToken = mock(JsonWebToken.class);
		userInfoProperties = mock(UserInfoProperties.class);
		webSocket = new SclAutoAlignmentWebsocket(eventBus, jsonWebToken, userInfoProperties);
	}

	@Test
	void onAlignMessage_ShouldSendEvent() {
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("session-id");
		SclAutoAlignRequest request = mock(SclAutoAlignRequest.class);
		when(jsonWebToken.getClaim(anyString())).thenReturn("test-user");
		when(userInfoProperties.who()).thenReturn("who");

		webSocket.onAutoAlignMessage(session, request);
		verify(eventBus).send(eq("auto-align-ws"), any(SclAutoAlignmentEventRequest.class));
	}

    @Test
    void onError_ShouldHandleException() {
        Session session = mock(Session.class);
        Throwable throwable = new RuntimeException("error");
        when(session.getId()).thenReturn("session-id");
        jakarta.websocket.RemoteEndpoint.Async async = mock(jakarta.websocket.RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        webSocket.onError(session, throwable);
    }

	@Test
	void onClose_ShouldLogDebug() {
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("session-id");
		webSocket.onClose(session);
	}
}
