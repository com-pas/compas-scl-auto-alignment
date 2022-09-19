// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.model;

import com.powsybl.sld.model.nodes.NodeSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.auto.alignment.TestUtil.readSCLElement;
import static org.lfenergy.compas.scl.auto.alignment.model.GenericTransformerWindingTest.PTW_NAME;

class GenericPowerTransformerTest {
    public static final String PT_NAME = "T4";

    private GenericPowerTransformer powerTransformer;

    @BeforeEach
    void setup() throws IOException {
        var scl = new GenericSCL(readSCLElement("scl-2.scd"));
        var substation = scl.getSubstation("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4").get();
        powerTransformer = substation.getPowerTransformers().get(0);
    }

    @Test
    void constructor_WhenCreated_ThenElementSet() {
        assertNotNull(powerTransformer.getElement());
        assertEquals(PT_NAME, powerTransformer.getName());
    }

    @Test
    void getTransformerWindings_WhenCalled_ThenListReturned() {
        var result = powerTransformer.getTransformerWindings();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(PTW_NAME, result.get(0).getName());

        // Second time we should get exactly the same list again (cached)
        assertEquals(result, powerTransformer.getTransformerWindings());
    }

    @Test
    void getTransformerWindingByConnectivityNode_WhenCalledWithKnownConnectivityNode() {
        var result = powerTransformer.getTransformerWindingByConnectivityNode("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 30kV/BAY_T4_0/CONNECTIVITY_NODE78");

        assertNotNull(result);
        assertEquals(PTW_NAME, result.getName());
    }

    @Test
    void getTransformerWindingByConnectivityNode_WhenCalledWithUnknownConnectivityNode() {
        var result = powerTransformer.getTransformerWindingByConnectivityNode("UNKNOWN");

        assertNull(result);
    }

    @Test
    void isFeederXWT_WhenCalledWithFirstPowerTransformer_ThenItWillBeA3WT() {
        assertTrue(powerTransformer.isFeeder3WT());
        assertFalse(powerTransformer.isFeeder2WT());
    }

    @Test
    void isFeederXWT_WhenCalledWithOtherPowerTransformer_ThenItWillBeA2WT() throws IOException {
        // We will switch to another PowerTransformer.
        var scl = new GenericSCL(readSCLElement("scl-2.scd"));
        var substation = scl.getSubstation("_3f64f4e2-adfe-4d12-b082-68e7fe4b11c9").get();
        powerTransformer = substation.getPowerTransformers().get(0);

        assertFalse(powerTransformer.isFeeder3WT());
        assertTrue(powerTransformer.isFeeder2WT());
    }

    @Test
    void getSide_WhenCalledWithDifferentConnectivityNodes_ThenCorrectSidesAreReturned() {
        assertEquals(NodeSide.ONE, powerTransformer.getSide("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 30kV/BAY_T4_0/CONNECTIVITY_NODE78"));
        assertEquals(NodeSide.TWO, powerTransformer.getSide("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 110kV/BAY_T4_1/CONNECTIVITY_NODE81"));
        assertEquals(NodeSide.THREE, powerTransformer.getSide("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE85"));
        assertNull(powerTransformer.getSide("UNKNOWN"));
    }

}