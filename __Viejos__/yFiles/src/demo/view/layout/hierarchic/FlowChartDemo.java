/*****************************************************************************************************************************************************
 * * * This file is part of yFiles-2.5. * * yWorks proprietary/confidential. Use is subject to license terms. * * Redistribution of this file or of an
 * unauthorized byte-code version * of this file is strictly forbidden. * * Copyright (c) 2000-2007 by yWorks GmbH, Vor dem Kreuzberg 28, * 72070
 * Tuebingen, Germany. All rights reserved. *
 ****************************************************************************************************************************************************/
package demo.view.layout.hierarchic;

import demo.view.DemoBase;
import demo.view.advanced.DragAndDropDemo;
import demo.view.realizer.GenericNodeRealizerDemo;
import y.anim.AnimationFactory;
import y.anim.AnimationPlayer;
import y.base.DataMap;
import y.base.DataProvider;
import y.base.Edge;
import y.base.Node;
import y.base.NodeCursor;
import y.base.NodeList;
import y.layout.BufferedLayouter;
import y.layout.GraphLayout;
import y.layout.PortCandidate;
import y.layout.PortCandidateSet;
import y.layout.PortConstraint;
import y.layout.hierarchic.IncrementalHierarchicLayouter;
import y.layout.hierarchic.incremental.IncrementalHintsFactory;
import y.layout.hierarchic.incremental.SimplexNodePlacer;
import y.util.DataProviderAdapter;
import y.util.DataProviders;
import y.util.Maps;
import y.view.Arrow;
import y.view.BridgeCalculator;
import y.view.DefaultGraph2DRenderer;
import y.view.EdgeRealizer;
import y.view.EditMode;
import y.view.GenericNodeRealizer;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.HitInfo;
import y.view.LayoutMorpher;
import y.view.NodeRealizer;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This demo shows a simple FlowChartEditor. It depicts how to use {@link IncrementalHierarchicLayouter} in both normal and incremental mode and the
 * use of {@link PortConstraint}s and {@link PortCandidateSet}s.
 */
public class FlowChartDemo extends DemoBase {

    // the layouter instance to use for the automatic layouts
    private IncrementalHierarchicLayouter layouter;

    // the hintmap used for hinting the layouter if a new node should be added incrementally
    private DataMap hintMap;

    public FlowChartDemo() {
        configureView();

        final List nodeRealizerList = new ArrayList();
        addNodeRealizerTemplates(nodeRealizerList);

        // creat a nice customized DragAndDrop List support
        DragAndDropDemo.DragAndDropSupport dndSupport = new DragAndDropDemo.DragAndDropSupport(nodeRealizerList, view) {

            // use the configuration string from GenericNodeRealizer for the Transferable
            protected String getTextValue(NodeRealizer selected) {
                if (selected instanceof GenericNodeRealizer) {
                    GenericNodeRealizer gnr = (GenericNodeRealizer) selected;
                    return gnr.getConfiguration();
                } else {
                    return null;
                }
            }

            // use the configuration string from GenericNodeRealizer for the Transferable
            protected NodeRealizer createNodeRealizerFromTextValue(String s) {
                try {
                    for (Iterator iterator = nodeRealizerList.iterator(); iterator.hasNext();) {
                        GenericNodeRealizer genericNodeRealizer = (GenericNodeRealizer) iterator.next();
                        if (genericNodeRealizer.getConfiguration().equals(s)) {
                            return genericNodeRealizer;
                        }
                    }
                } catch (IllegalArgumentException iae) {
                }
                return null;
            }

            // customize the drop operation to invoke a layout algorithm and auto-create an edge
            protected boolean dropRealizer(Graph2DView view, NodeRealizer r, double worldCoordX, double worldCoordY) {
                final Graph2D graph = view.getGraph2D();
                final HitInfo hitInfo = graph.getHitInfo(worldCoordX, worldCoordY, true);
                r = r.createCopy();
                r.setCenter(worldCoordX, worldCoordY);
                final Node node = graph.createNode(r);
                if (hitInfo.getHitNode() != null) {
                    graph.createEdge(hitInfo.getHitNode(), node);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            runIncrementalLayout(new NodeList(node).nodes());
                        }
                    });
                } else {
                    view.updateView();
                }
                return true;
            }
        };

        // add the list to the UI
        contentPane.add(new JScrollPane(dndSupport.getList()), BorderLayout.WEST);
        layouter = createLayouter();
    }

    /**
     * Overwritten to disable node label setting and disallow resizing.
     */
    protected EditMode createEditMode() {
        final EditMode editMode = super.createEditMode();
        editMode.assignNodeLabel(false);
        editMode.allowResizeNodes(false);
        return editMode;
    }

    /**
     * Add a layout button to the ToolBar
     */
    protected JToolBar createToolBar() {
        final JToolBar toolBar = super.createToolBar();
        toolBar.add(new AbstractAction("Layout") {
            public void actionPerformed(ActionEvent e) {
                runLayout();
            }
        });
        return toolBar;
    }

    /**
     * Configures the view.
     */
    private void configureView() {
        final EdgeRealizer defaultEdgeRealizer = view.getGraph2D().getDefaultEdgeRealizer();
        defaultEdgeRealizer.setTargetArrow(Arrow.STANDARD);
        ((DefaultGraph2DRenderer) view.getGraph2DRenderer()).setBridgeCalculator(new BridgeCalculator());
    }

    /**
     * Creates the Layouter instance.
     */
    private IncrementalHierarchicLayouter createLayouter() {
        final IncrementalHierarchicLayouter layouter = new IncrementalHierarchicLayouter();
        ((SimplexNodePlacer) layouter.getNodePlacer()).setBaryCenterModeEnabled(true);
        layouter.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_FROM_SCRATCH);
        layouter.getEdgeLayoutDescriptor().setOrthogonallyRouted(true);
        final Graph2D graph = view.getGraph2D();

        // create the map for the Incremental Hints
        hintMap = Maps.createDataMap(new HashMap());
        graph.addDataProvider(IncrementalHierarchicLayouter.INCREMENTAL_HINTS_DPKEY, hintMap);

        // create an adapter that returns the userdata of the GenericNodeRealizers as the PortCandidateSet
        graph.addDataProvider(PortCandidateSet.NODE_DP_KEY, new DataProviderAdapter() {
            public Object get(Object dataHolder) {
                final Node node = (Node) dataHolder;
                final NodeRealizer realizer = graph.getRealizer(node);
                if (realizer instanceof GenericNodeRealizer) {
                    return (PortCandidateSet) ((GenericNodeRealizer) realizer).getUserData();
                } else {
                    return null;
                }
            }
        });

        // create automatic bus structures for outgoing edges of "start" nodes
        graph.addDataProvider(PortConstraint.SOURCE_GROUPID_KEY, new DataProviderAdapter() {
            public Object get(Object dataHolder) {
                Edge edge = (Edge) dataHolder;
                Node source = edge.source();
                GenericNodeRealizer gnr = (GenericNodeRealizer) graph.getRealizer(source);
                String sourceConfiguration = gnr.getConfiguration();
                if (sourceConfiguration.equals("start")) {
                    return source;
                }
                return null;
            }
        });

        // ... and bus structures for incoming edges at "switch" and "branch" nodes
        graph.addDataProvider(PortConstraint.TARGET_GROUPID_KEY, new DataProviderAdapter() {
            public Object get(Object dataHolder) {
                Edge edge = (Edge) dataHolder;
                Node target = edge.target();
                GenericNodeRealizer gnr = (GenericNodeRealizer) graph.getRealizer(target);
                String targetConfiguration = gnr.getConfiguration();
                if (targetConfiguration.equals("switch") || targetConfiguration.equals("branch")) {
                    return target;
                }
                return null;
            }
        });

        // make sure that edges enter at the north side and exit at the south side of the nodes
        final DataProvider southDP = DataProviders.createConstantDataProvider(PortConstraint.create(PortConstraint.SOUTH));
        graph.addDataProvider(PortConstraint.SOURCE_PORT_CONSTRAINT_KEY, southDP);
        final DataProvider northDP = DataProviders.createConstantDataProvider(PortConstraint.create(PortConstraint.NORTH));
        graph.addDataProvider(PortConstraint.TARGET_PORT_CONSTRAINT_KEY, northDP);
        return layouter;
    }

    /**
     * Run the layout in normal mode.
     */
    private void runLayout() {
        Cursor oldCursor = view.getCanvasComponent().getCursor();
        try {
            view.getCanvasComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            GraphLayout result = new BufferedLayouter(layouter).calcLayout(view.getGraph2D());
            LayoutMorpher morpher = new LayoutMorpher(view, result);
            morpher.setSmoothViewTransform(true);
            morpher.setPreferredDuration(300);
            final AnimationPlayer player = new AnimationPlayer();
            player.addAnimationListener(view);
            player.setFps(120);
            player.animate(AnimationFactory.createEasedAnimation(morpher));
        } finally {
            view.getCanvasComponent().setCursor(oldCursor);
        }
    }

    /**
     * Run the layout in incremental mode.
     */
    private void runIncrementalLayout(NodeCursor incrementalNodes) {
        Cursor oldCursor = view.getCanvasComponent().getCursor();
        try {
            view.getCanvasComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            layouter.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_INCREMENTAL);
            final IncrementalHintsFactory factory = layouter.createIncrementalHintsFactory();
            for (incrementalNodes.toFirst(); incrementalNodes.ok(); incrementalNodes.next()) {
                hintMap.set(incrementalNodes.node(), factory.createLayerIncrementallyHint(incrementalNodes.node()));
            }
            GraphLayout result = new BufferedLayouter(layouter).calcLayout(view.getGraph2D());
            LayoutMorpher morpher = new LayoutMorpher(view, result);
            morpher.setSmoothViewTransform(true);
            morpher.setPreferredDuration(300);
            final AnimationPlayer player = new AnimationPlayer();
            player.addAnimationListener(view);
            player.setFps(120);
            player.animate(AnimationFactory.createEasedAnimation(morpher));
        } finally {
            layouter.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_FROM_SCRATCH);
            for (incrementalNodes.toFirst(); incrementalNodes.ok(); incrementalNodes.next()) {
                hintMap.set(incrementalNodes.node(), null);
            }
            view.getCanvasComponent().setCursor(oldCursor);
        }
    }

    /**
     * This method adds the possible NodeRealizer's for this application to the given list.
     */
    private void addNodeRealizerTemplates(List nodeRealizerList) {
        // obtain the factory to register the configurations
        final GenericNodeRealizer.Factory factory = GenericNodeRealizer.getFactory();
        {
            // create a start node configuration
            final Map map = GenericNodeRealizer.getFactory().createDefaultConfigurationMap();
            Ellipse2D.Double outer = new Ellipse2D.Double(0, 0, 1, 1);
            Ellipse2D.Double inner = new Ellipse2D.Double(0.1, 0.1, 0.8, 0.8);
            Ellipse2D.Double inner2 = new Ellipse2D.Double(0.2, 0.2, 0.6, 0.6);
            GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
            gp.append(outer, true);
            gp.append(inner, false);
            gp.append(inner2, false);
            final GenericNodeRealizerDemo.GeneralPathPainter painter = new GenericNodeRealizerDemo.GeneralPathPainter(gp);
            final GenericNodeRealizerDemo.RectangularShapePainter containsTest = new GenericNodeRealizerDemo.RectangularShapePainter(outer);
            map.put(GenericNodeRealizer.Painter.class, painter);
            map.put(GenericNodeRealizer.ContainsTest.class, containsTest);
            factory.addConfiguration("start", map);

            // create an configure an instance
            final GenericNodeRealizer startRealizer = new GenericNodeRealizer("start");
            startRealizer.setFillColor(Color.black);

            // configure the PortCandidateSet as the userdata
            // sophisticated Painter implementations could visualize these ports.
            PortCandidateSet candidateSet = new PortCandidateSet();
            candidateSet.add(PortCandidate.createCandidate(0.0d, 0.0d, PortCandidate.ANY, 0), Integer.MAX_VALUE);
            startRealizer.setUserData(candidateSet);

            // add it to the list
            nodeRealizerList.add(startRealizer);
        }
        {
            final Map map = GenericNodeRealizer.getFactory().createDefaultConfigurationMap();
            RoundRectangle2D.Double rr = new RoundRectangle2D.Double(0, 0, 100, 100, 20, 20);
            final GenericNodeRealizerDemo.RectangularShapePainter painter = new GenericNodeRealizerDemo.RectangularShapePainter(rr);
            map.put(GenericNodeRealizer.Painter.class, painter);
            map.put(GenericNodeRealizer.ContainsTest.class, painter);
            factory.addConfiguration("state", map);

            // create an configure an instance
            final GenericNodeRealizer stateRealizer = new GenericNodeRealizer("state");
            stateRealizer.setFillColor(Color.yellow);

            // configure the PortCandidateSet as the userdata
            PortCandidateSet candidateSet = new PortCandidateSet();
            candidateSet.add(PortCandidate.createCandidate(PortCandidate.NORTH, 0), Integer.MAX_VALUE);
            candidateSet.add(PortCandidate.createCandidate(PortCandidate.SOUTH, 0), Integer.MAX_VALUE);
            stateRealizer.setUserData(candidateSet);

            // add it to the list
            nodeRealizerList.add(stateRealizer);
        }
        {
            final Map map = GenericNodeRealizer.getFactory().createDefaultConfigurationMap();
            GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
            gp.moveTo(0.5f, 0f);
            gp.lineTo(1.0f, 0.5f);
            gp.lineTo(0.5f, 1.0f);
            gp.lineTo(0, 0.5f);
            gp.closePath();
            final GenericNodeRealizerDemo.GeneralPathPainter painter = new GenericNodeRealizerDemo.GeneralPathPainter(gp);
            map.put(GenericNodeRealizer.Painter.class, painter);
            map.put(GenericNodeRealizer.ContainsTest.class, painter);
            factory.addConfiguration("switch", map);

            // create an configure an instance
            final GenericNodeRealizer switchRealizer = new GenericNodeRealizer("switch");

            // configure the PortCandidateSet as the userdata
            PortCandidateSet candidateSet = new PortCandidateSet();
            candidateSet.add(PortCandidate.createCandidate(0, -15, PortCandidate.NORTH, 0), 1);
            candidateSet.add(PortCandidate.createCandidate(0, 15, PortCandidate.SOUTH, 0), 1);
            candidateSet.add(PortCandidate.createCandidate(15, 0, PortCandidate.EAST, 0), 1);
            candidateSet.add(PortCandidate.createCandidate(-15, 0, PortCandidate.WEST, 0), 1);
            candidateSet.add(PortCandidate.createCandidate(0, -15, PortCandidate.NORTH, 1), Integer.MAX_VALUE);
            candidateSet.add(PortCandidate.createCandidate(0, 15, PortCandidate.SOUTH, 1), Integer.MAX_VALUE);
            switchRealizer.setUserData(candidateSet);

            // add it to the list
            nodeRealizerList.add(switchRealizer);
        }
        {
            final Map map = GenericNodeRealizer.getFactory().createDefaultConfigurationMap();
            GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
            gp.moveTo(0.5f, 0.3f);
            gp.lineTo(1, 1);
            gp.lineTo(0, 1);
            gp.closePath();
            final GenericNodeRealizerDemo.GeneralPathPainter painter = new GenericNodeRealizerDemo.GeneralPathPainter(gp);
            map.put(GenericNodeRealizer.Painter.class, painter);
            map.put(GenericNodeRealizer.ContainsTest.class, painter);
            factory.addConfiguration("branch", map);

            // create an configure an instance
            final GenericNodeRealizer branch = new GenericNodeRealizer("branch");

            // configure the PortCandidateSet as the userdata
            PortCandidateSet candidateSet = new PortCandidateSet();
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.NORTH, 0), Integer.MAX_VALUE);
            candidateSet.add(PortCandidate.createCandidate(PortCandidate.SOUTH, 0), 3);
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.EAST, 1), Integer.MAX_VALUE);
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.WEST, 1), Integer.MAX_VALUE);
            branch.setUserData(candidateSet);

            // add it to the list
            nodeRealizerList.add(branch);
        }
        {
            final Map map = GenericNodeRealizer.getFactory().createDefaultConfigurationMap();
            Ellipse2D.Double outer = new Ellipse2D.Double(0.2, 0.2, 0.6, 0.6);
            GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
            gp.append(outer, true);

            final GenericNodeRealizerDemo.GeneralPathPainter painter = new GenericNodeRealizerDemo.GeneralPathPainter(gp);
            map.put(GenericNodeRealizer.Painter.class, painter);
            map.put(GenericNodeRealizer.ContainsTest.class, painter);
            factory.addConfiguration("end", map);

            // create and configure an instance
            final GenericNodeRealizer endRealizer = new GenericNodeRealizer("end");
            endRealizer.setFillColor(Color.black);

            // configure the PortCandidateSet as the userdata
            PortCandidateSet candidateSet = new PortCandidateSet();
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.NORTH, 0), 1);
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.EAST, 1), 1);
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.WEST, 1), 1);
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.EAST, 2), Integer.MAX_VALUE);
            candidateSet.add(PortCandidate.createCandidate(0, 0, PortCandidate.WEST, 2), Integer.MAX_VALUE);
            endRealizer.setUserData(candidateSet);

            // add it to the list
            nodeRealizerList.add(endRealizer);
        }
    }

    public static void main(String[] args) {
        initLnF();
        new FlowChartDemo().start("Flow Chart Demo");
    }
}
