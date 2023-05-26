// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.rest.v1;

import io.quarkus.security.Authenticated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.scl.auto.alignment.rest.UserInfoProperties;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignRequest;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignResponse;
import org.lfenergy.compas.scl.auto.alignment.rest.v1.model.SclAutoAlignSVGRequest;
import org.lfenergy.compas.scl.auto.alignment.service.SclAutoAlignmentService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Authenticated
@RequestScoped
@Path("/auto/alignment/v1")
public class SclAutoAlignmentResource {
    private static final Logger LOGGER = LogManager.getLogger(SclAutoAlignmentResource.class);

    private final SclAutoAlignmentService sclAutoAlignmentService;

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @Inject
    public SclAutoAlignmentResource(SclAutoAlignmentService compasCimMappingService) {
        this.sclAutoAlignmentService = compasCimMappingService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public SclAutoAlignResponse alignment(@Valid SclAutoAlignRequest request) {
        LOGGER.info("Auto align SCL for Substation(s) {}.", request.getSubstationNames());

        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        var response = new SclAutoAlignResponse();
        response.setSclData(sclAutoAlignmentService.updateSCL(request.getSclData(), request.getSubstationNames(), who));
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_SVG_XML)
    @Path("/svg")
    public String svg(@Valid SclAutoAlignSVGRequest request) {
        LOGGER.info("Creating SVG for Substation {}.", request.getSubstationName());
        return sclAutoAlignmentService.getSVG(request.getSclData(), request.getSubstationName());
    }
}