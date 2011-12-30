/****************************************************************************
 **
 ** This file is part of yFiles-2.5. 
 ** 
 ** yWorks proprietary/confidential. Use is subject to license terms.
 **
 ** Redistribution of this file or of an unauthorized byte-code version
 ** of this file is strictly forbidden.
 **
 ** Copyright (c) 2000-2007 by yWorks GmbH, Vor dem Kreuzberg 28, 
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ***************************************************************************/
package demo.view.layout.hierarchic;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;

import y.base.DataMap;
import y.base.EdgeCursor;
import y.base.Node;
import y.base.NodeCursor;
import y.base.NodeList;
import y.layout.BufferedLayouter;
import y.layout.GraphLayout;
import y.layout.hierarchic.IncrementalHierarchicLayouter;
import y.layout.hierarchic.incremental.IncrementalHintsFactory;
import y.option.ConstraintManager;
import y.option.OptionHandler;
import y.option.OptionItem;
import y.util.Maps;
import y.view.Graph2D;
import y.view.LayoutMorpher;
import y.view.hierarchy.DefaultHierarchyGraphFactory;
import y.view.hierarchy.GroupLayoutConfigurator;
import y.view.hierarchy.ProxyAutoBoundsNodeRealizer;
import demo.view.hierarchy.HierarchyDemo;


/**
 * This demo showcases how IncrementalHierarchicLayouter can be used to fully or incrementally
 * layout hierarchically nested graphs. The demo supports automatic relayout after expanding folder nodes,
 * collapsing group nodes. Furthermore it provides toolbar buttons that
 * trigger full layout and incremental relayout. A settings dialog for group layout options is provided as well.
 * In incremental layout mode all selected elementa are added incrementally to the existing layout.
 */
public class IncrementalHierarchicGroupDemo extends HierarchyDemo {

  IncrementalHierarchicLayouter layouter;
  OptionHandler groupLayoutOptions;

  public IncrementalHierarchicGroupDemo() {

    view.setPreferredSize(new Dimension(1000,800));

    //configure layout algorithm
    layouter = new IncrementalHierarchicLayouter();
    layouter.getEdgeLayoutDescriptor().setOrthogonallyRouted(true);
    layouter.setRecursiveGroupLayeringEnabled(false);

    //prepare option handler for group layout options
    Object[] groupStrategyEnum = {"Global Layering", "Recursive Layering"};
    Object[] groupAlignmentEnum = {"Top", "Center", "Bottom"};
    groupLayoutOptions = new OptionHandler("Grouplayout Options");
    ConstraintManager cm = new ConstraintManager(groupLayoutOptions);
    OptionItem gsi = groupLayoutOptions.addEnum("Group Layering Strategy", groupStrategyEnum, 0);
    OptionItem eci = groupLayoutOptions.addBool("Enable Compact Layering", true);
    OptionItem gai = groupLayoutOptions.addEnum("Group Alignment", groupAlignmentEnum, 0);
    cm.setEnabledOnValueEquals(gsi, "Recursive Layering", eci);
    cm.setEnabledOnValueEquals(gsi, "Recursive Layering", gai);
    cm.setEnabledOnCondition(cm.createConditionValueEquals(gsi, "Recursive Layering").and(cm.createConditionValueEquals(eci, Boolean.TRUE).inverse()), gai);

    //configure default graphics for default node realizers. defaults are adopted
    //from nodes contained in initial graph.
    Graph2D graph = view.getGraph2D();
    for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
      Node n = nc.node();
      if (hierarchy.isNormalNode(n)) {
        graph.setDefaultNodeRealizer(graph.getRealizer(n).createCopy());
        break;
      }
    }
    for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
      Node n = nc.node();
      if (!hierarchy.isNormalNode(n)) {
        DefaultHierarchyGraphFactory hgf = (DefaultHierarchyGraphFactory) hierarchy.getGraphFactory();
        if(graph.getRealizer(n) instanceof ProxyAutoBoundsNodeRealizer) {
          hgf.setProxyNodeRealizerEnabled(true);
          ProxyAutoBoundsNodeRealizer pnr = (ProxyAutoBoundsNodeRealizer) graph.getRealizer(n);
          hgf.setDefaultGroupNodeRealizer(pnr.getRealizer(0).createCopy());
          hgf.setDefaultFolderNodeRealizer(pnr.getRealizer(1).createCopy());
          break;
        }
      }
    }

    view.fitContent();
  }

  /**
   * Loads the initial graph
   */
  protected void loadInitialGraph() {
    loadGraph("resource/grouping.ygf");
  }

  /**
   * Creates the toolbar for the demo.
   */
  protected JToolBar createToolBar() {
    JToolBar toolBar = super.createToolBar();
    toolBar.add(new AbstractAction("Incremental Layout") {
      public void actionPerformed(ActionEvent e) {
        layoutIncrementally();
      }
    });
    toolBar.add(new AbstractAction("New Layout") {
      public void actionPerformed(ActionEvent e) {
        layout();
      }
    });
    toolBar.add(new AbstractAction("Group Layout Options...") {
      public void actionPerformed(ActionEvent e) {
        groupLayoutOptions.showEditor((Frame)view.getTopLevelAncestor());
        configureGroupLayout();
      }
    });
    return toolBar;
  }

  /**
   * Configures the layouter options relevant for grouping.
   */
  private void configureGroupLayout() {
    Object gsi = groupLayoutOptions.get("Group Layering Strategy");
    if ("Recursive Layering".equals(gsi)) {
      layouter.setRecursiveGroupLayeringEnabled(true);
    } else if ("Global Layering".equals(gsi)) {
      layouter.setRecursiveGroupLayeringEnabled(false);
    }

    layouter.setGroupCompactionEnabled(groupLayoutOptions.getBool("Enable Compact Layering"));

    Object gai = groupLayoutOptions.get("Group Alignment");
    if ("Top".equals(gai)) {
      layouter.setGroupAlignmentPolicy(IncrementalHierarchicLayouter.POLICY_ALIGN_GROUPS_TOP);
    } else if ("Center".equals(gai)) {
      layouter.setGroupAlignmentPolicy(IncrementalHierarchicLayouter.POLICY_ALIGN_GROUPS_CENTER);
    }
    if ("Bottom".equals(gai)) {
      layouter.setGroupAlignmentPolicy(IncrementalHierarchicLayouter.POLICY_ALIGN_GROUPS_BOTTOM);
    }
  }

  /**
   * Performs incremental layout. All selected elements will be treated incrementally.
   */
  private void layoutIncrementally() {
    Graph2D graph = view.getGraph2D();

    layouter.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_INCREMENTAL);

    // create storage for both nodes and edges
    DataMap incrementalElements = Maps.createHashedDataMap();
    // configure the mode
    final IncrementalHintsFactory ihf = layouter.createIncrementalHintsFactory();

    for (NodeCursor nc = graph.selectedNodes(); nc.ok(); nc.next()) {
      incrementalElements.set(nc.node(), ihf.createLayerIncrementallyHint(nc.node()));
    }

    for (EdgeCursor ec = graph.selectedEdges(); ec.ok(); ec.next()) {
      incrementalElements.set(ec.edge(), ihf.createSequenceIncrementallyHint(ec.edge()));
    }
    graph.addDataProvider(IncrementalHierarchicLayouter.INCREMENTAL_HINTS_DPKEY, incrementalElements);


    GroupLayoutConfigurator glc = new GroupLayoutConfigurator(graph);
    glc.prepareAll();
    GraphLayout gl = new BufferedLayouter(layouter).calcLayout(graph);
    new LayoutMorpher(view, gl).execute();
    glc.restoreAll();
    
    graph.removeDataProvider(IncrementalHierarchicLayouter.INCREMENTAL_HINTS_DPKEY);
  }

  /**
   * Performs global layout. The new layout can strongly differ from the existing layout.
   */
  private void layout() {
    Graph2D graph = view.getGraph2D();
    layouter.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_FROM_SCRATCH);
    GroupLayoutConfigurator glc = new GroupLayoutConfigurator(graph);
    glc.prepareAll();
    GraphLayout gl = new BufferedLayouter(layouter).calcLayout(graph);
    new LayoutMorpher(view, gl).execute();
    glc.restoreAll();
  }


  /**
   * Expand a folder node. After expanding the folder node, an incremental layout is automatically triggered.
   * For this, the expanded node and all of its decendants will be treated as incremental elements.
   */
  protected void openFolder(Node folderNode) {
    NodeList children = new NodeList(hierarchy.getInnerGraph(folderNode).nodes());
    super.openFolder(folderNode);

    Graph2D graph = view.getGraph2D();

    graph.unselectAll();
    graph.setSelected(folderNode, true);
    for(NodeCursor nc = children.nodes(); nc.ok(); nc.next()) {
      graph.setSelected(nc.node(), true);
    }

    layoutIncrementally();

    graph.unselectAll();
    graph.setSelected(folderNode, true);

    graph.updateViews();
  }

  /**
   * Collape a group node. After collapsing the group node, an incremental layout is automatically triggered.
   * For this, the collapsed node is treated as an incremental element.
   */
  protected void closeGroup(Node groupNode) {
    super.closeGroup(groupNode);


    Graph2D graph = view.getGraph2D();
    graph.unselectAll();
    graph.setSelected(groupNode, true);
    for (EdgeCursor ec = groupNode.edges(); ec.ok(); ec.next()) {
      graph.setSelected(ec.edge(), true);
    }

    layoutIncrementally();
    graph.unselectAll();

    graph.updateViews();
  }

  /**
   * Launches this demo.
   */
  public static void main(String args[]) {
    initLnF();

    IncrementalHierarchicGroupDemo demo = new IncrementalHierarchicGroupDemo();

    demo.start();
  }

}
