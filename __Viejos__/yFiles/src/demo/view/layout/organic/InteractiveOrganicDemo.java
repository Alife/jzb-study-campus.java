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
package demo.view.layout.organic;

import demo.view.DemoBase;
import y.layout.CopiedLayoutGraph;
import y.layout.LayoutTool;
import y.layout.organic.InteractiveOrganicLayouter;
import y.view.EditMode;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * This demo shows the very basic useage of the
 *  {@link y.layout.organic.InteractiveOrganicLayouter}.
 * The layouter is started within a thread. A swing timer is used to update the
 * positions of the nodes.
 */
public class InteractiveOrganicDemo extends DemoBase {
  private InteractiveOrganicLayouter layouter;

  protected void initialize() {
    layouter = new InteractiveOrganicLayouter();

    loadGraph( "resource/peopleNav.ygf" );
    //Reset the paths and the locations of the nodes.
    LayoutTool.initDiagram( view.getGraph2D() );

    view.updateWorldRect();
    view.fitContent();
    view.setZoom( 0.3 );
  }

  /**
   * Callback used by {@link #registerViewModes()} to create the default EditMode
   * @return an instance of {@link y.view.EditMode} with showNodeTips enabled
   */
  protected EditMode createEditMode() {
    EditMode editMode = super.createEditMode();
    editMode.allowBendCreation( false );
    editMode.allowNodeCreation( false );
    editMode.allowResizeNodes( false );
    editMode.allowEdgeCreation( false );

    //This view mode offers support for "touching the graph"
    editMode.setMoveSelectionMode( new InteractiveMoveSelectionMode( layouter ) );

    return editMode;
  }

  protected JToolBar createToolBar() {
    JToolBar toolBar = super.createToolBar();
    final JButton button = new JButton( "Layout" );
    toolBar.add( button );

    button.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        //Disable the button
        button.setEnabled( false );

        //Start the layout thread
        new Thread( new Runnable() {
          public void run() {
            //This call will not return until {@link y.layout.organic.InteractiveOrganicLayouter#stop()} is called.
            //The calculated layout is *not* automatically applied on the graph.
            layouter.doLayout( new CopiedLayoutGraph( view.getGraph2D() ) );
          }
        } ).start();

        //Update timer
        Timer timer = new Timer( 21, new ActionListener() {
          //This listener is notified about 24 times a second.
          public void actionPerformed( ActionEvent e ) {
            //Write the calculated positions back to the realizers
            if ( layouter.commitPositionsSmoothly( 50, 0.15 ) > 0 ) {
              //... and update the view, if something has changed
              view.updateView();
            }
          }
        } );
        timer.setInitialDelay( 500 );
        timer.start();
      }
    } );

    return toolBar;
  }


  public static void main( String[] args ) throws IOException {
    InteractiveOrganicDemo demo = new InteractiveOrganicDemo();
    demo.start();
  }
}
