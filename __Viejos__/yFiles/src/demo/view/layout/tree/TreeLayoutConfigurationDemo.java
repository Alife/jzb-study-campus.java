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
package demo.view.layout.tree;

import demo.view.DemoBase;
import y.layout.tree.GenericTreeLayouter;
import y.base.NodeList;
import y.base.Node;
import y.view.Graph2D;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * This demo shows the usage of the TreeLayoutConfiguration.
 * The TreeLayoutConfiguration offers examples how to configure and run the {@link GenericTreeLayouter}.
 *
 **/
public class TreeLayoutConfigurationDemo extends DemoBase {

  private static Color[] colors = new Color[11];

  static {
    for ( int i = 0; i < 11; i++ ) {
      colors[ i ] = new Color( 255 - i * 22, i * 22, 0 );
    }
  }

  public static void main( String[] args ) {
    TreeLayoutConfigurationDemo demo = new TreeLayoutConfigurationDemo();
    demo.start();
  }

  private void layout( TreeLayoutConfiguration configuration ) {
    GenericTreeLayouter genericTreeLayouter = new GenericTreeLayouter();
    configuration.layout( genericTreeLayouter, view.getGraph2D() );
    view.fitContent();
    view.updateView();
  }

  protected void initialize() {
    loadGraph( "resource/dfb2004.gml"  );
    layout( TreeLayoutConfiguration.PLAYOFFS );
  }

  protected JToolBar createToolBar() {
    JToolBar toolBar = super.createToolBar();
    toolBar.add( new AbstractAction( "Playoffs" ) {
      public void actionPerformed( ActionEvent e ) {
        view.getGraph2D().clear();
        loadGraph( "resource/dfb2004.gml"  );
        layout( TreeLayoutConfiguration.PLAYOFFS );
      }
    } );
    toolBar.add( new AbstractAction( "Playoffs double" ) {
      public void actionPerformed( ActionEvent e ) {
        view.getGraph2D().clear();
        loadGraph( "resource/dfb2004.gml"  );
        layout( TreeLayoutConfiguration.PLAYOFFS_DOUBLE );
      }
    } );
    toolBar.add( new AbstractAction( "Double line" ) {
      public void actionPerformed( ActionEvent e ) {
        view.getGraph2D().clear();
        createTree( view.getGraph2D(), new int[]{ 1, 4, 6, 8 } );
        layout( TreeLayoutConfiguration.DOUBLE_LINE );
      }
    } );
    toolBar.add( new AbstractAction( "Bus" ) {
      public void actionPerformed( ActionEvent e ) {
        view.getGraph2D().clear();
        createTree( view.getGraph2D(), new int[]{ 1, 4, 3, 8 } );
        layout( TreeLayoutConfiguration.BUS );
      }
    } );
    toolBar.add( new AbstractAction( "Layered tree" ) {
      public void actionPerformed( ActionEvent e ) {
        view.getGraph2D().clear();
        createTree( view.getGraph2D(), new int[]{ 1, 4,4,4 } );
        layout( TreeLayoutConfiguration.LAYERED_TREE );
      }
    } );
    toolBar.add( new AbstractAction( "Default delegating" ) {
      public void actionPerformed( ActionEvent e ) {
        view.getGraph2D().clear();
        createTree( view.getGraph2D(), new int[]{ 1, 4, 3, 8 } );
        layout( TreeLayoutConfiguration.DEFAULT_DELEGATING );
      }
    } );
    return toolBar;
  }

  /**
   * Creates a tree with specified number of children per parent and layer.
   */
  public static Graph2D createTree(Graph2D graph, int[] childrenCountPerLayer) {
    if (childrenCountPerLayer.length == 0) return graph;
    if (childrenCountPerLayer[0] != 1) throw new IllegalArgumentException("The first layer must contain 1 node");

    NodeList lastLayerContent = new NodeList();

    //First layer
    Node node = graph.createNode();
    lastLayerContent.add(node);

    for (int i = 1; i < childrenCountPerLayer.length; i++) {
      int childrenCount = childrenCountPerLayer[i];

      NodeList newLayerContent = new NodeList();
      for (int j = 0; j < lastLayerContent.size(); j++) {
        Node parent = (Node) lastLayerContent.get(j);

        for (int k = 0; k < childrenCount; k++) {
          Node child = graph.createNode();
          newLayerContent.add(child);
          graph.setLabelText(child, String.valueOf(graph.N()));
          graph.createEdge(parent, child);
        }
      }

      lastLayerContent = newLayerContent;
    }
    return graph;
  }

}
