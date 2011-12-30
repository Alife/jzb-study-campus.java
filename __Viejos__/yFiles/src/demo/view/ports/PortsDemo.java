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
package demo.view.ports;

import demo.view.DemoBase;
import demo.view.advanced.DragAndDropDemo;
import y.base.Edge;
import y.base.YList;
import y.io.GMLIOHandler;
import y.io.YGFIOHandler;
import y.util.D;
import y.view.CreateEdgeMode;
import y.view.EdgeRealizer;
import y.view.EditMode;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.ImageNodeRealizer;
import y.view.NodeRealizer;
import y.view.Port;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Demonstrates how CreateEdgeMode and ImageNodeRealizer can be customized 
 *  in order to create an application that makes use of a set of 
 *  fixed port coordinates for each node.
 *  Additionally this application demonstrates the customization of the GML 
 *  parser and encoder. For this scenario, the GML format is customized in order
 *  to serialize and deserialize possible port coordinate for each node.
 */
public class PortsDemo extends DemoBase{
  private ImageNodeRealizer[] images;

  /**
   * This method is called before the view modes and actions are registered and the menu and toolbar is build.
   */
  protected void initialize() {
    GMLIOHandler.setEncoderFactory(new EncoderFactory());
    GMLIOHandler.setParserFactory(new ParserFactory());

    List imageNRList = new ArrayList();

    FixedPortsNodeRealizer nr = new FixedPortsNodeRealizer();
    nr.setPaintingPorts(false);
    nr.setImageURL(this.getClass().getResource("resource/4pkt.gif"));
    nr.setToImageSize();
    imageNRList.add(nr);
    YList ports = nr.getPortCandidates();
    ports.clear();
    double w = nr.getWidth();
    double h = nr.getHeight();
    double r = 6;
    ports.add(new Port(-w/2 + r, -h/2 + r));
    ports.add(new Port(-w/2 + r, 0));
    ports.add(new Port(w/2 - r, 0));
    ports.add(new Port(-w/2+ r, h/2 -r));

    nr = new FixedPortsNodeRealizer();
    nr.setPaintingPorts(false);
    nr.setImageURL(this.getClass().getResource("resource/4pktleft.gif"));
    nr.setToImageSize();
    imageNRList.add(nr);
    ports = nr.getPortCandidates();
    ports.clear();
    w = nr.getWidth();
    h = nr.getHeight();
    r = 6;
    ports.add(new Port(w/2 - r, -h/2 + r));
    ports.add(new Port(w/2 - r, 0));
    ports.add(new Port(-w/2 + r, 0));
    ports.add(new Port(w/2 - r, h/2 - r));

    nr = new FixedPortsNodeRealizer();
    nr.setPaintingPorts(false);
    nr.setImageURL(this.getClass().getResource("resource/2pkt.gif"));
    imageNRList.add(nr);
    nr.setToImageSize();
    ports = nr.getPortCandidates();
    ports.clear();
    w = nr.getWidth();
    h = nr.getHeight();
    r = 6;
    ports.add(new Port(w/2 - r, 0));
    ports.add(new Port(-w/2+ r, 0));

    final Graph2D graph = this.view.getGraph2D();

    images = new ImageNodeRealizer[imageNRList.size()];
    images = (ImageNodeRealizer[]) imageNRList.toArray(images);

    graph.setDefaultNodeRealizer(images[0]);

    //Scroll
    DragAndDropDemo.DragAndDropSupport dragAndDropSupport = new MyDragAndDropSupport( images );

    final JList list = dragAndDropSupport.getList();
    list.addListSelectionListener( new ListSelectionListener() {
      public void valueChanged( ListSelectionEvent e ) {
        graph.setDefaultNodeRealizer( images[ list.getSelectedIndex() ] );
      }
    } );
    list.setSelectedIndex( 0 );
    JScrollPane scrollPane = new JScrollPane( list );

    list.setBackground( Color.LIGHT_GRAY );
    list.setCellRenderer( new Renderer() );

    contentPane.add( scrollPane, BorderLayout.WEST );

    loadGraph(  "resource/viewport.gml" );

    view.setGridMode( true );            //snap to grid
    view.setGridResolution( 29 );        //grid distance (should correspond to actual port candidates)
    view.setGridType( Graph2DView.GRID_CROSS ); //grid type
    view.setGridVisible( true );         //show grid
  }

  protected void registerViewModes() {
    EditMode editMode = new EditMode( );
    editMode.setCreateEdgeMode(new PortCreateEdgeMode());
    view.addViewMode( editMode );
  }

  public void dispose() {
    super.dispose();
    // reset to old state
    GMLIOHandler.setEncoderFactory(null);
    GMLIOHandler.setParserFactory(null);
  }

  /**
   * modified version of CreateEdgeMode, that assigns port coordinates
   * according to the ones offered by FixedPortsNodeRealizer's candidates
   */
  private static final class PortCreateEdgeMode extends CreateEdgeMode {
    private Edge edge; // need this for the hook

    /**
     * If a node was hit at the given coordinates, that node
     * will be used as target node for the newly created edge.
     *
     */
    public void mouseReleasedLeft( double x, double y ) {
      // simulate a pressed shift...
      // this will trigger CreateEdgeMode, to preassign offset
      // to source and target ports
      super.mouseShiftReleasedLeft( x, y );

      if ( edge != null ) { // the edge has just been created
        Graph2D graph = ( Graph2D ) edge.getGraph();
        EdgeRealizer er = graph.getRealizer( edge );

        NodeRealizer nr = graph.getRealizer( edge.target() );
        if ( nr instanceof FixedPortsNodeRealizer ) {
          FixedPortsNodeRealizer fpnr = ( FixedPortsNodeRealizer ) nr;
          Port p = er.getTargetPort();
          p = fpnr.snapCandidate( graph, er, false, p.getOffsetX(), p.getOffsetY() );
          er.setTargetPort( p );
        }

        nr = graph.getRealizer( edge.source() );
        if ( nr instanceof FixedPortsNodeRealizer ) {
          FixedPortsNodeRealizer fpnr = ( FixedPortsNodeRealizer ) nr;
          Port p = er.getSourcePort();
          p = fpnr.snapCandidate( graph, er, true, p.getOffsetX(), p.getOffsetY() );
          er.setSourcePort( p );
        }

        // do some clean up
        edge = null;
        graph.updateViews();
      }

    }

    /**
     * Initiates the creation of an edge.
     * 
     */
    public void mousePressedLeft( double x, double y ) {
      // simulate a pressed shift...
      // this will trigger CreateEdgeMode, to preassign offset
      // to source and target ports
      super.mouseShiftPressedLeft( x, y );
    }


    public void edgeCreated( Edge e ) {
      //remember the edge...
      this.edge = e;
    }
  }

  /*
   * overwritten here to install gml io handling
   */
  protected Action createLoadAction() {
    return new LoadAction();
  }

  /*
   * overwritten here to install gml io handling
   */
  protected Action createSaveAction() {
    return new SaveAction();
  }

  /**
   * Action that saves the current graph to a file in YGF or GML format.
   */
  private final class SaveAction extends AbstractAction {
    SaveAction() {
      super( "Save..." );
    }

    public void actionPerformed( ActionEvent e ) {
      JFileChooser chooser = new JFileChooser();
      if ( chooser.showSaveDialog( contentPane ) == JFileChooser.APPROVE_OPTION ) {
        String name = chooser.getSelectedFile().toString();
        if ( name.endsWith( ".gml" ) ) {
          GMLIOHandler ioh = new GMLIOHandler();
          try {
            ioh.write( view.getGraph2D(), name );
          } catch ( IOException ioe ) {
            D.show( ioe );
          }
        } else {
          if ( !name.endsWith( ".ygf" ) ) name = name + ".ygf";
          YGFIOHandler ioh = new YGFIOHandler();
          try {
            ioh.write( view.getGraph2D(), name );
          } catch ( IOException ioe ) {
            D.show( ioe );
          }
        }
      }
    }
  }

  /**
   * Action that loads the current graph from a file in YGF or GML format.
   */
  private final class LoadAction extends AbstractAction {
    LoadAction() {
      super( "Load..." );
    }

    public void actionPerformed( ActionEvent e ) {
      JFileChooser chooser = new JFileChooser();
      if ( chooser.showOpenDialog( contentPane ) == JFileChooser.APPROVE_OPTION ) {
        String name = chooser.getSelectedFile().toString();
        if ( name.endsWith( ".gml" ) ) {
          GMLIOHandler ioh = new GMLIOHandler();
          try {
            view.getGraph2D().clear();
            ioh.read( view.getGraph2D(), name );
          } catch ( IOException ioe ) {
            D.show( ioe );
          }
        } else {
          if ( !name.endsWith( ".ygf" ) ) name = name + ".ygf";
          YGFIOHandler ioh = new YGFIOHandler();
          try {
            view.getGraph2D().clear();
            ioh.read( view.getGraph2D(), name );
          } catch ( IOException ioe ) {
            D.show( ioe );
          }
        }
        //force redisplay of view contents
        view.updateView();
      }
    }
  }

  /**
   * helper class for displaying ImageNodeRealizers in a JComboBox
   */
  private static final class Renderer extends JComponent implements ListCellRenderer {
    private Image image;
    private int width, height;
    private Color bg;

    public void paintComponent( Graphics gfx ) {
      gfx.setColor( bg );
      gfx.fillRect( 0, 0, getWidth(), getHeight() );
      gfx.drawImage( image, 5, ( getHeight() - height ) / 2, width, height, this );
    }

    public Component getListCellRendererComponent(
      JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
      bg = isSelected ? list.getSelectionBackground() : list.getBackground();
      image = ( ( ImageNodeRealizer ) value ).getImage();
      width = image.getWidth( this );
      height = image.getHeight( this );
      setPreferredSize( new Dimension( width + 10, height + 10 ) );
      return this;
    }
  }

  /**
   * launches the demo
   */
  public static void main( String[] args ) {
    initLnF();
    PortsDemo demo = new PortsDemo();
    demo.start("Port Demo");
  }

  private class MyDragAndDropSupport extends DragAndDropDemo.DragAndDropSupport {
    MyDragAndDropSupport( ImageNodeRealizer[] images ) {
      super( images, view );
    }

    protected String getTextValue( NodeRealizer selected ) {
      return ( ( ImageNodeRealizer ) selected ).getImageURL().getFile();
    }

    protected NodeRealizer createNodeRealizerFromTextValue( String s ) {
      for ( int i = 0; i < images.length; i++ ) {
        ImageNodeRealizer image = images[ i ];
        if ( image.getImageURL().getPath().equals( s ) ) {
          return image;
        }
      }
      return images[ 0 ];
    }
  }
}


      
