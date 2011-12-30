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
package demo.view.layout.labeling;

import demo.view.DemoBase;
import y.base.Edge;
import y.base.EdgeCursor;
import y.base.Node;
import y.base.NodeCursor;
import y.layout.labeling.GreedyMISLabeling;
import y.option.MappedListCellRenderer;
import y.option.OptionHandler;
import y.view.DefaultBackgroundRenderer;
import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.EditMode;
import y.view.Graph2D;
import y.view.NodeLabel;
import y.view.NodeRealizer;
import y.view.PopupMode;
import y.view.ShapeNodeRealizer;
import y.view.YLabel;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

/**
 * Demonstrates basic labeling features.
 * <br>
 * It is shown:
 * <ul>
 * <li>how to start a labeling algorithm</li>
 * <li>how to create and manipulate labels</li>
 * <li>how to assign a label model to a label</li>
 * <li>how a labeling position can be changed interactively</li>
 * </ul>
 *
 */
public class LabelingDemo extends DemoBase {
  private byte edgeLabelModel = EdgeLabel.SIX_POS;
  private byte nodeLabelModel = NodeLabel.SANDWICH;

  public LabelingDemo() {
    DefaultBackgroundRenderer renderer = new DefaultBackgroundRenderer( view );

    URL bgImage = getClass().getResource( "resource/usamap.gif" );
    renderer.setImageResource( bgImage );
    renderer.setMode( DefaultBackgroundRenderer.DYNAMIC );
    renderer.setColor( Color.white );
    view.setBackgroundRenderer( renderer );
    view.setPreferredSize( new Dimension( 650, 400 ) );
    view.setWorldRect( 0, 0, 650, 400 );

    // use thicker edges 
    // EdgeRealizer er = view.getGraph2D().getDefaultEdgeRealizer();
    // er.setLineType(LineType.LINE_2);

    ShapeNodeRealizer defaultNode =
        new ShapeNodeRealizer( ShapeNodeRealizer.ELLIPSE );
    defaultNode.setSize( 7, 7 );
    defaultNode.setFillColor( Color.black );
    defaultNode.getLabel().setModel( nodeLabelModel );
    view.getGraph2D().setDefaultNodeRealizer( defaultNode );
    createExample();
  }

  protected void registerViewModes() {
    //add a popup view mode that listens to the right mouse click
    //and displays context sensitive menus
    EditMode mode = new EditMode();
    mode.setPopupMode( new DemoPopupMode() );
    view.addViewMode( mode );
  }

  protected JToolBar createToolBar() {
    JToolBar bar = super.createToolBar();
    bar.addSeparator();
    bar.add( new ChangeLabelModel() );
    return bar;
  }

  class DemoPopupMode extends PopupMode {
    /**
     * Popup menu for a hit node
     */
    public JPopupMenu getNodePopup( Node v ) {
      JPopupMenu pm = new JPopupMenu();
      NodeRealizer r = this.view.getGraph2D().getRealizer( v );
      YLabel label = r.getLabel();
      pm.add( new EditLabel( label ) );
      return pm;
    }

    /**
     * Popup menu for a hit node
     */
    public JPopupMenu getEdgePopup( Edge e ) {
      JPopupMenu pm = new JPopupMenu();
      pm.add( new AddEdgeLabel( e ) );
      return pm;
    }

    public JPopupMenu getEdgeLabelPopup( EdgeLabel el ) {
      JPopupMenu pm = new JPopupMenu();
      pm.add( new EditLabel( el ) );
      return pm;
    }
  }


  /**
   * Action that opens a text editor for the label of a node
   */
  class EditLabel extends AbstractAction {
    YLabel label;

    EditLabel( YLabel l ) {
      super( "Edit Label" );
      label = l;
    }

    public void actionPerformed( ActionEvent e ) {
      view.openLabelEditor( label,
          label.getLocation().getX(),
          label.getLocation().getY() );
    }
  }

  public class AddEdgeLabel extends AbstractAction {
    Edge edge;

    public AddEdgeLabel( Edge e ) {
      super( "Add Label" );
      edge = e;
    }

    public void actionPerformed( ActionEvent e ) {
      if ( edge != null ) {
        Graph2D g = view.getGraph2D();
        EdgeRealizer er = g.getRealizer( edge );
        EdgeLabel label = new EdgeLabel( "Route" );
        er.addLabel( label );
        g.updateViews();
        //label.setModel(edgeLabelModel); // a valid option!
      }
    }
  }

  /**
   * Change label model model and apply new labeling
   */
  public class ChangeLabelModel extends AbstractAction {
    OptionHandler op;

    ChangeLabelModel() {
      super( "Change Label Model" );
    }

    public void actionPerformed( ActionEvent e ) {
      if ( op == null ) {
        op = new OptionHandler( "Label Model" );
        Map m1 = NodeLabel.modelToStringMap();
        Map m2 = EdgeLabel.modelToStringMap();
        op.addEnum( "Node Label Model",
            m1.keySet().toArray(), new Byte( nodeLabelModel ),
            new MappedListCellRenderer( m1 ) );
        op.addEnum( "Edge Label Model",
            m2.keySet().toArray(), new Byte( edgeLabelModel ),
            new MappedListCellRenderer( m2 ) );
      }

      if ( op.showEditor() ) {
        byte model1 = ( ( ( Byte ) op.get( "Node Label Model" ) ).byteValue() );
        for ( NodeCursor nc = view.getGraph2D().nodes(); nc.ok(); nc.next() ) {
          view.getGraph2D().getRealizer( nc.node() ).getLabel().setModel( model1 );
        }
        nodeLabelModel = model1;
        byte model2 = ( ( ( Byte ) op.get( "Edge Label Model" ) ).byteValue() );
        for ( EdgeCursor ec = view.getGraph2D().edges(); ec.ok(); ec.next() ) {
          EdgeRealizer er = view.getGraph2D().getRealizer( ec.edge() );
          for ( int i = 0; i < er.labelCount(); i++ ) {
            er.getLabel( i ).setModel( model2 );
          }
        }
        edgeLabelModel = model2;
      }
      //now do actual labeling
      // Here you may choose another labeling algorithm, i.e. SALabling
      GreedyMISLabeling labeling = new GreedyMISLabeling();

      // use:
      // labeling.setPlaceNodeLabels(true);
      // labeling.setPlaceEdgeLabels(false);
      // to label only nodes.
      labeling.label( view.getGraph2D() );
      view.updateView();
    }
  }


  public void createExample() {
    Graph2D graph = view.getGraph2D();
    graph.clear();
    Node n1 = graph.createNode();
    graph.setCenter( n1, 100, 90 );
    graph.setLabelText( n1, "City 1" );
    Node n2 = graph.createNode();
    graph.setCenter( n2, 150, 200 );
    graph.setLabelText( n2, "City 2" );
    Node n3 = graph.createNode();
    graph.setCenter( n3, 250, 260 );
    graph.setLabelText( n3, "City 3" );
    Node n4 = graph.createNode();
    graph.setCenter( n4, 300, 45 );
    graph.setLabelText( n4, "City 4" );
    Node n5 = graph.createNode();
    graph.setCenter( n5, 500, 300 );
    graph.setLabelText( n5, "City 5" );
    Node n6 = graph.createNode();
    graph.setCenter( n6, 50, 150 );
    graph.setLabelText( n6, "City 6" );
    Node n7 = graph.createNode();
    graph.setCenter( n7, 45, 135 );
    graph.setLabelText( n7, "City 7" );
    Node n8 = graph.createNode();
    graph.setCenter( n8, 55, 165 );
    graph.setLabelText( n8, "City 8" );
    Node n9 = graph.createNode();
    graph.setCenter( n9, 620, 50 );
    graph.setLabelText( n9, "City 9" );
    Node n10 = graph.createNode();
    graph.setCenter( n10, 450, 150 );
    graph.setLabelText( n10, "City 10" );
    Node n11 = graph.createNode();
    graph.setCenter( n11, 270, 180 );
    graph.setLabelText( n11, "City 11" );
    Node n12 = graph.createNode();
    graph.setCenter( n12, 580, 100 );
    graph.setLabelText( n12, "City 12" );
    //
    Edge e1 = graph.createEdge( n6, n7 );
    Edge e2 = graph.createEdge( n7, n8 );
    Edge e3 = graph.createEdge( n1, n2 );
    graph.getRealizer( e3 ).addLabel( new EdgeLabel( "Route 1" ) );
    Edge e4 = graph.createEdge( n2, n3 );
    graph.getRealizer( e4 ).addLabel( new EdgeLabel( "Route 2" ) );
    Edge e5 = graph.createEdge( n1, n6 );
    Edge e6 = graph.createEdge( n2, n8 );
    Edge e7 = graph.createEdge( n3, n5 );
    Edge e8 = graph.createEdge( n9, n10 );
    Edge e9 = graph.createEdge( n11, n10 );
    Edge e10 = graph.createEdge( n11, n3 );
    graph.getRealizer( e10 ).addLabel( new EdgeLabel( "Route 3" ) );
    graph.getRealizer( e10 ).addLabel( new EdgeLabel( "Out of order !" ) );
    Edge e11 = graph.createEdge( n1, n4 );
    Edge e12 = graph.createEdge( n11, n4 );
    Edge e13 = graph.createEdge( n10, n4 );
    Edge e14 = graph.createEdge( n10, n5 );
    Edge e15 = graph.createEdge( n12, n5 );
    Edge e16 = graph.createEdge( n9, n12 );
  }


  public static void main( String args[] ) {
    LabelingDemo demo = new LabelingDemo();
    demo.start( "Labeling Demo" );
  }
}


      
