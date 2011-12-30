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

package demo.layout;

import y.base.Edge;
import y.base.Node;
import y.base.NodeMap;
import y.layout.BufferedLayouter;
import y.layout.CopiedLayoutGraph;
import y.layout.DefaultLayoutGraph;
import y.layout.hierarchic.IncrementalHierarchicLayouter;
import y.layout.hierarchic.incremental.SwimLaneDescriptor;
import y.util.D;

/**
 * This demo shows how to use the swim lane feature of IncrementalHierarchicLayouter
 * without using classes that are only present in the yFiles Viewer Distribution. 
 * In this demo, nodes will be assigned to certain regions of the diagram,
 * the so-called swim lanes. The diagram will be arranged using hierachical layout
 * style, while nodes remain within the bounds of their lanes.
 * <br>
 * This demo displays the calculated coordinates in a simple graph viewer.
 * Additionally it outputs the calculated coordinates of the graph layout to
 * the console.
 */
public class SwimLaneLayoutWithoutAView
{
  
  /**
   * Launcher
   */
  public static void main(String[] args)
  {
    SwimLaneLayoutWithoutAView lwv = new SwimLaneLayoutWithoutAView();
    lwv.doit();
  }

  /**
   * Creates a small graph and applies a swim lane layout to it.
   */
  public void doit()
  {
    DefaultLayoutGraph graph = new DefaultLayoutGraph();
    
    //construct graph. assign sizes to nodes
    Node v1 = graph.createNode();
    graph.setSize(v1,30,30);
    Node v2 = graph.createNode();
    graph.setSize(v2,30,30);
    Node v3 = graph.createNode();
    graph.setSize(v3,30,30);
    Node v4 = graph.createNode();
    graph.setSize(v4,30,30);

    // create some edges...
    Edge e1 = graph.createEdge(v1,v2);
    Edge e2 = graph.createEdge(v1,v3);
    Edge e3 = graph.createEdge(v2,v4);
    
    // create swim lane descriptors for two lanes. Lane
    // sl1 is the first lane and sl2 is the second lane. 
    SwimLaneDescriptor sl1 = new SwimLaneDescriptor(new Integer(1));
    SwimLaneDescriptor sl2 = new SwimLaneDescriptor(new Integer(2));
    
    // create a map to store the swim lane descriptors
    NodeMap slMap = graph.createNodeMap();

    // assign nodes to lanes 
    slMap.set(v1, sl1);
    slMap.set(v2, sl2);
    slMap.set(v3, sl2);
    slMap.set(v4, sl1);
 
    // register the information
    graph.addDataProvider(IncrementalHierarchicLayouter.SWIMLANE_DESCRIPTOR_DPKEY, slMap);
    
    // create the layout algorithm
    IncrementalHierarchicLayouter layouter = new IncrementalHierarchicLayouter();
    
    // start the layout
    new BufferedLayouter(layouter).doLayout(graph);
    
    //display result
    LayoutPreviewPanel lpp1 = new LayoutPreviewPanel(new CopiedLayoutGraph(graph));
    lpp1.createFrame("Swimlanes").setVisible(true);
    
    D.bug("\n\nGRAPH LAID OUT HIERARCHICALLY IN SWIMLANES");
    D.bug("v1 center position = " + graph.getCenter(v1));
    D.bug("v2 center position = " + graph.getCenter(v2));
    D.bug("v3 center position = " + graph.getCenter(v3));
    D.bug("v4 center position = " + graph.getCenter(v4));
    D.bug("e1 path = " + graph.getPath(e1));
    D.bug("e2 path = " + graph.getPath(e2));
    D.bug("e3 path = " + graph.getPath(e3));
    D.bug("SwimLane 1 index = " + sl1.getComputedLaneIndex());
    D.bug("SwimLane 1 position = " + sl1.getComputedLanePosition());
    D.bug("SwimLane 1 width = " + sl1.getComputedLaneWidth());
    D.bug("SwimLane 2 index = " + sl2.getComputedLaneIndex());
    D.bug("SwimLane 2 position = " + sl2.getComputedLanePosition());
    D.bug("SwimLane 2 width = " + sl2.getComputedLaneWidth());
    
  }
}
