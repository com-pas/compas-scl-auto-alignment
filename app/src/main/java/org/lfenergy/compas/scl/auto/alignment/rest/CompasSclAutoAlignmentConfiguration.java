// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.core.commons.ElementConverter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
@RegisterForReflection(targets = {com.powsybl.sld.library.Components.class,
        com.powsybl.sld.library.Component.class,
        org.lfenergy.compas.core.commons.model.ErrorResponse.class,
        org.lfenergy.compas.core.commons.model.ErrorMessage.class})
public class CompasSclAutoAlignmentConfiguration {
    @Produces
    @ApplicationScoped
    public ElementConverter createElementConverter() {
        return new ElementConverter();
    }
}
