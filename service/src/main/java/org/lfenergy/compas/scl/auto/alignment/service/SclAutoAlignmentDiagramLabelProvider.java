// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.auto.alignment.service;

import com.powsybl.sld.library.ComponentTypeName;
import com.powsybl.sld.model.coordinate.Direction;
import com.powsybl.sld.model.coordinate.Side;
import com.powsybl.sld.model.graphs.SubstationGraph;
import com.powsybl.sld.model.graphs.VoltageLevelGraph;
import com.powsybl.sld.model.nodes.BusNode;
import com.powsybl.sld.model.nodes.FeederNode;
import com.powsybl.sld.model.nodes.Node;
import com.powsybl.sld.svg.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SclAutoAlignmentDiagramLabelProvider implements DiagramLabelProvider {
    private final Map<Node, List<NodeLabel>> busLabels = new HashMap<>();

    public SclAutoAlignmentDiagramLabelProvider(SubstationGraph graph) {
        graph.getVoltageLevels().forEach(voltageLevelGraph ->
                voltageLevelGraph.getNodes().forEach(this::addNode)
        );
        graph.getMultiTermNodes().forEach(this::addNode);
    }

    private void addNode(Node node) {
        LabelPosition labelPosition = new LabelPosition("default", 0, -5, true, 0);
        List<NodeLabel> labels = new ArrayList<>();
        labels.add(new NodeLabel(stripLabel(node.getId()), labelPosition, null));
        busLabels.put(node, labels);
    }

    @Override
    public List<FeederInfo> getFeederInfos(FeederNode node) {
        return Arrays.asList(new DirectionalFeederInfo(ComponentTypeName.ARROW_ACTIVE, LabelDirection.OUT, "", ""),
                new DirectionalFeederInfo(ComponentTypeName.ARROW_REACTIVE, LabelDirection.IN, "", ""));
    }

    @Override
    public List<NodeLabel> getNodeLabels(Node node, Direction direction) {
        return busLabels.get(node);
    }

    @Override
    public List<NodeDecorator> getNodeDecorators(Node node, Direction direction) {
        return new ArrayList<>();
    }

    @Override
    public String getTooltip(Node node) {
        return null;
    }

    @Override
    public List<ElectricalNodeInfo> getElectricalNodesInfos(VoltageLevelGraph voltageLevelGraph) {
        return Collections.emptyList();
    }

    @Override
    public Optional<BusInfo> getBusInfo(BusNode busNode) {
        return Optional.empty();
    }

    @Override
    public Map<String, Side> getBusInfoSides(VoltageLevelGraph voltageLevelGraph) {
        return Collections.emptyMap();
    }

    String stripLabel(String id) {
        String label = id;
        if (StringUtils.isNotBlank(label) && label.lastIndexOf("/") >= 0) {
            label = label.substring(label.lastIndexOf("/") + 1);
        }
        return label;
    }
}