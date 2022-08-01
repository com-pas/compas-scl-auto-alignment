// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.builder;

import com.powsybl.sld.library.ComponentTypeName;
import com.powsybl.sld.model.coordinate.Direction;
import com.powsybl.sld.model.graphs.BaseGraph;
import com.powsybl.sld.model.graphs.NodeFactory;
import com.powsybl.sld.model.graphs.VoltageLevelGraph;
import com.powsybl.sld.model.graphs.VoltageLevelInfos;
import com.powsybl.sld.model.nodes.*;
import org.lfenergy.compas.scl.auto.alignment.model.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class VoltageLevelGraphBuilder extends AbstractGraphBuilder<VoltageLevelGraph> {
    private final GenericVoltageLevel voltageLevel;
    private final GenericSubstation substation;

    public VoltageLevelGraphBuilder(GenericVoltageLevel voltageLevel,
                                    GenericSubstation substation,
                                    Map<String, Node> path2Node,
                                    BaseGraph parentGraph) {
        super(path2Node);
        this.voltageLevel = voltageLevel;
        this.substation = substation;

        var voltageLevelInfos = new VoltageLevelInfos(voltageLevel.getFullName(),
                voltageLevel.getFullName(),
                voltageLevel.getVoltage());
        setGraph(new VoltageLevelGraph(voltageLevelInfos, parentGraph));

        createVoltageLevel();
    }

    private void createVoltageLevel() {
        // First process the Busbars.
        var busbarIndex = new AtomicInteger(1);
        voltageLevel.getBays().stream()
                .filter(GenericBay::isBusbar)
                .forEach(busbar -> processBusbarNode(busbar, busbarIndex.getAndIncrement()));

        // Next process the other bays.
        var bayIndex = new AtomicInteger(1);
        voltageLevel.getBays().stream()
                .filter(bay -> !bay.isBusbar())
                .forEach(bay -> processBayNode(bay, bayIndex.getAndIncrement()));
    }

    private void processBusbarNode(GenericBay busbar,
                                   int busbarIndex) {
        busbar.getConnectivityNodes()
                .stream().findFirst()
                .ifPresent(connectivityNode ->
                        addNode(connectivityNode.getPathName(),
                                createBusbarNode(busbar.getFullName(), busbarIndex, 1)));
    }

    public BusNode createBusbarNode(String id, int busbarIndex, int sectionIndex) {
        BusNode busNode = NodeFactory.createBusNode(getGraph(), id, id);
        busNode.setBusBarIndexSectionIndex(busbarIndex, sectionIndex);
        return busNode;
    }

    private void processBayNode(GenericBay bay, int bayIndex) {
        bay.getConnectivityNodes().forEach(this::createConnectivityNode);
        bay.getConductingEquipments().forEach(conductingEquipment ->
                processConductingEquipment(conductingEquipment, bayIndex));
    }

    private void createConnectivityNode(GenericConnectivityNode connectivityNode) {
        getPowerTransformer(connectivityNode.getPathName())
                .ifPresentOrElse(powerTransformer -> {
                            if (powerTransformer.isFeeder2WT()) {
                                addNode(connectivityNode.getPathName(),
                                        createFeeder2WTLegNode(connectivityNode.getPathName(),
                                                powerTransformer.getSide(connectivityNode.getPathName()), 0, null));
                            } else if (powerTransformer.isFeeder3WT()) {
                                addNode(connectivityNode.getPathName(),
                                        createFeeder3WTLegNode(connectivityNode.getPathName(),
                                                powerTransformer.getSide(connectivityNode.getPathName()), 0, null));
                            }
                        }, () ->
                                addNode(connectivityNode.getPathName(),
                                        createFictitiousNode(connectivityNode.getPathName()))
                );
    }

    private Optional<GenericPowerTransformer> getPowerTransformer(String pathName) {
        if (substation != null) {
            return substation.getPowerTransformerByConnectivityNode(pathName);
        }
        return Optional.empty();
    }

    private void processConductingEquipment(GenericConductingEquipment conductingEquipment, int order) {
        var terminals = conductingEquipment.getTerminals();
        var fullName = conductingEquipment.getFullName();
        var node = createSwitchNode(fullName, order);

        if (!terminals.isEmpty()) {
            var node1 = terminalToNode(terminals.get(0), order);
            connectNode(node, node1);

            if (terminals.size() == 2) {
                var node2 = terminalToNode(terminals.get(1), order);
                connectNode(node, node2);
            } else {
                var node2 = createLoad(fullName + "/Grounded", order);
                connectNode(node, node2);
            }
        }
    }

    private Node terminalToNode(GenericTerminal terminal, int order) {
        var pathName = terminal.getConnectivityNode();
        if (pathName != null) {
            return getNodeByPath(pathName);
        }
        return createLoad(terminal.getCNodeName(), order);
    }

    private SwitchNode createSwitchNode(String id, int order) {
        var switchNode = NodeFactory.createSwitchNode(getGraph(), id, id, SwitchNode.SwitchKind.BREAKER.name(),
                false, SwitchNode.SwitchKind.BREAKER, false);
        switchNode.setOrder(order);
        switchNode.setDirection(Direction.TOP);
        return switchNode;
    }

    private void connectNode(Node node1, Node node2) {
        getGraph().addEdge(node1, node2);
    }

    private FictitiousNode createFictitiousNode(String id) {
        return NodeFactory.createFictitiousNode(getGraph(), id, id, id, ComponentTypeName.LINE);
    }

    private FeederNode createLoad(String id, int order) {
        FeederNode fn = NodeFactory.createLoad(getGraph(), id, id);
        commonFeederSetting(fn, id, order, Direction.TOP);
        return fn;
    }

    public Feeder2WTLegNode createFeeder2WTLegNode(String id, FeederWithSideNode.Side side, int order,
                                                   Direction direction) {
        Feeder2WTLegNode f2WTe = NodeFactory.createFeeder2WTLegNode(getGraph(), id + "_" + side, id, id, side);
        commonFeederSetting(f2WTe, id, order, direction);
        return f2WTe;
    }

    public Feeder3WTLegNode createFeeder3WTLegNode(String id, FeederWithSideNode.Side side, int order,
                                                   Direction direction) {
        Feeder3WTLegNode f3WTe = NodeFactory.createFeeder3WTLegNodeForSubstationDiagram(getGraph(), id + "_" + side, id, id, side);
        commonFeederSetting(f3WTe, id + side.getIntValue(), order, direction);
        return f3WTe;
    }

    private void commonFeederSetting(FeederNode node, String id, int order, Direction direction) {
        node.setLabel(id);

        if (direction != null) {
            node.setOrder(order);
            node.setDirection(direction);
        }
    }
}
