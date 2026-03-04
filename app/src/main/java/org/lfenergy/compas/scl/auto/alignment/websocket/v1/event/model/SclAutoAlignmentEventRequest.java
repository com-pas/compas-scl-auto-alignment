// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.websocket.v1.event.model;

import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;

import jakarta.websocket.Session;
import java.io.Serializable;

public class SclAutoAlignmentEventRequest implements Serializable {
    private final Session session;
    private final SclAutoAlignRequest request;
    private final String who;

    public SclAutoAlignmentEventRequest(Session session, SclAutoAlignRequest request, String who) {
        this.session = session;
        this.request = request;
        this.who = who;
    }

    public Session getSession() {
        return session;
    }

    public SclAutoAlignRequest getRequest() {
        return request;
    }

    public String getWho() {
        return who;
    }
}
