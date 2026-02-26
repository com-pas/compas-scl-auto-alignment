package org.lfenergy.compas.scl.auto.alignment.websocket.v1.event.model;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;

import jakarta.websocket.Session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SclAutoAlignmentEventRequestTest {

	@Test
	void constructorAndGetters_ShouldReturnValues() {
		Session session = mock(Session.class);
		SclAutoAlignRequest request = mock(SclAutoAlignRequest.class);
		String who = "test-user";

		SclAutoAlignmentEventRequest eventRequest = new SclAutoAlignmentEventRequest(session, request, who);

		assertSame(session, eventRequest.getSession(), "Session should match");
		assertSame(request, eventRequest.getRequest(), "Request should match");
		assertEquals(who, eventRequest.getWho(), "Who should match");
	}

	@Test
	void constructor_AllowsNullArguments() {
		SclAutoAlignmentEventRequest eventRequest = new SclAutoAlignmentEventRequest(null, null, null);
		assertNull(eventRequest.getSession(), "Session should be null");
		assertNull(eventRequest.getRequest(), "Request should be null");
		assertNull(eventRequest.getWho(), "Who should be null");
	}
}
