// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.model.graphs.SubstationGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SclAutoAlignmentDiagramLabelProviderTest {
    private SclAutoAlignmentDiagramLabelProvider provider;

    @BeforeEach
    void setup() {
        provider = new SclAutoAlignmentDiagramLabelProvider(SubstationGraph.create("Dummy"));
    }

    @Test
    void getNodeDecorators_whenCalled_ThenAlwaysReturnEmptyList() {
        var result = provider.getNodeDecorators(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTooltip_whenCalled_ThenAlwaysReturnNull() {
        var result = provider.getTooltip(null);

        assertNull(result);
    }

    @Test
    void getElectricalNodesInfos_whenCalled_ThenAlwaysReturnEmptyList() {
        var result = provider.getElectricalNodesInfos(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBusInfo_whenCalled_ThenAlwaysReturnEmptyOptional() {
        var result = provider.getBusInfo(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBusInfoSides_whenCalled_ThenAlwaysReturnEmptyMap() {
        var result = provider.getBusInfoSides(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}