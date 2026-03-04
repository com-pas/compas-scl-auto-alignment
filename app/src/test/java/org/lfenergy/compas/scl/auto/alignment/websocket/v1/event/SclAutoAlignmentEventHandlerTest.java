// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignResponse;
import org.lfenergy.compas.scl.auto.alignment.service.SclAutoAlignmentService;
import org.lfenergy.compas.scl.auto.alignment.websocket.v1.event.model.SclAutoAlignmentEventRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SclAutoAlignmentEventHandlerTest {

	@Mock
	private SclAutoAlignmentService sclAutoAlignmentService;

	@InjectMocks
	private SclAutoAlignmentEventHandler handler;

	@Test
	void sclAutoAlignWebsocketsEvent_WhenCalled_ThenSclAutoAlignResponseReturned() {
		String who = "test-user";
		SclAutoAlignRequest alignRequest = mock(SclAutoAlignRequest.class);
		String sclData = "<SCL>...</SCL>";
		var substationNames = java.util.Collections.singletonList("substation");

		when(alignRequest.getSclData()).thenReturn(sclData);
		when(alignRequest.getSubstationNames()).thenReturn(substationNames);
		when(sclAutoAlignmentService.updateSCL(sclData, substationNames, who)).thenReturn("<SCL>updated</SCL>");

		Session session = mockSession();
		SclAutoAlignmentEventRequest eventRequest = new SclAutoAlignmentEventRequest(session, alignRequest, who);

		handler.sclAutoAlignWebsocketsEvent(eventRequest);

		SclAutoAlignResponse response = verifyResponse(session, SclAutoAlignResponse.class);
		assertEquals("<SCL>updated</SCL>", response.getSclData());
		verify(sclAutoAlignmentService).updateSCL(sclData, substationNames, who);
	}

	private Session mockSession() {
		Session session = mock(Session.class);
		RemoteEndpoint.Async async = mock(RemoteEndpoint.Async.class);
		when(session.getAsyncRemote()).thenReturn(async);
		return session;
	}

	private <T> T verifyResponse(Session session, Class<T> responseClass) {
		verify(session).getAsyncRemote();
		ArgumentCaptor<T> captor = ArgumentCaptor.forClass(responseClass);
		verify(session.getAsyncRemote()).sendObject(captor.capture());
		return captor.getValue();
	}
}
