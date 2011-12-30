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
package demo.view.layout;

import demo.view.DemoBase;
import y.base.Edge;
import y.base.Node;
import y.layout.BufferedLayouter;
import y.layout.CanonicMultiStageLayouter;
import y.layout.EdgeLabelLayout;
import y.layout.GraphLayout;
import y.layout.Layouter;
import y.layout.OrientationLayouter;
import y.layout.circular.CircularLayouter;
import y.layout.hierarchic.HierarchicLayouter;
import y.layout.organic.SmartOrganicLayouter;
import y.layout.orthogonal.OrthogonalLayouter;
import y.option.OptionHandler;
import y.util.D;
import y.view.Arrow;
import y.view.EdgeLabel;
import y.view.Graph2D;
import y.view.LayoutMorpher;
import y.anim.AnimationFactory;
import y.anim.AnimationPlayer;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.JRootPane;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Demonstrates how layout and labeling algorithms can be applied to a
 * graph being displayed within a viewer component.
 * <br>
 * The view actions provided with ViewActionDemo are accessible as well.
 * <br>
 * The layout options can be either given as command line arguments or
 * within a settings dialog that will automatically pop up if no
 * command line arguments are provided.
 * <br>
 * Command line usage:
 * <pre>
 * java demo.view.layout.LayoutDemo {hierarchic,orthogonal,organic,circular} [anim] [label]
 * </pre>
 * <br>
 * The first command line argument is the name of the layout algorithm
 * to be applied. 
 * If the optional argument "label" is given, node and edge labels will
 * be created and a generic labeling algorithm will be used to place them.
 * If the optional argument "anim" is given, then the
 * layout will be applied to the graph in an animated fashion.
 */
public class LayoutDemo extends DemoBase
{
  private AnimationPlayer animationPlayer = new AnimationPlayer();
  OptionHandler layoutOptions;

  public LayoutDemo()
  {
    this(new String[]{"hierarchic", "anim", "label"});
  }

  public LayoutDemo(String[] args) {
    animationPlayer.addAnimationListener( view );
    initLayoutOptions( args );

    //use an arrow to make edge directions clear
    view.getGraph2D().getDefaultEdgeRealizer().setArrow( Arrow.STANDARD );

    //build sample graph
    buildGraph( view.getGraph2D() );
  }

  /**
   * Initializes layout options from command line arguments.
   * If no command line arguments are given, a settings dialog will
   * automatically be displayed.
   */
  void initLayoutOptions(String[] args) {
    //create layout options
    boolean anim = true;
    boolean label = true;
    String layout = "hierarchic";
    if (args.length > 0) {
      layout = args[0];
      List list = Arrays.asList(args);
      anim = list.contains("anim");
      label = list.contains("label");
    }

    layoutOptions = new OptionHandler("Settings");
    final String[] algoEnum = {"hierarchic", "orthogonal", "organic", "circular" };
    layoutOptions.addEnum("Layout Style", algoEnum, layout, null);
    layoutOptions.addBool("Activate Generic Labeling", label);
    layoutOptions.addBool("Activate Layout Morphing", anim);

    if(args.length == 0) {
      layoutOptions.showEditor();
    }
  }



  /** Creates a small random graph with labelled edges */
  void buildGraph(Graph2D graph)
  {
    graph.clear();
    Node nodes[] = new Node[10];
    for(int i = 0; i < nodes.length; i++)
    {
      nodes[i] = graph.createNode();
      graph.getRealizer(nodes[i]).setLabelText(""+i);
    }

    Random random = new Random(0);
    for ( int i = 0; i < nodes.length; i++ ) {
      for ( int j = i + 1; j < nodes.length; j++ ) {
        if ( random.nextDouble() > 0.75 ) {
          Edge edge = graph.createEdge( nodes[ i ], nodes[ j ] );
          EdgeLabel edgeLabel = new EdgeLabel( i + " -> " + j );
          edgeLabel.setModel( EdgeLabel.SIDE_SLIDER );
          edgeLabel.setPreferredPlacement( EdgeLabelLayout.PLACE_AT_CENTER );
          graph.getRealizer( edge ).addLabel( edgeLabel );
        }
      }
    }
  }

  /**
   * Adds an extra layout action to the toolbar
   */
  protected JToolBar createToolBar() {
    JToolBar bar = super.createToolBar();
    bar.addSeparator();
    bar.add(new LayoutAction());
    return bar;
  }

  /**
   * Layout action that configures and launches a layout algorithm.
   */
  class LayoutAction extends AbstractAction {
    LayoutAction() {
      super("Auto-Layout Graph");
    }

    public void actionPerformed(ActionEvent e) {
      if(layoutOptions.showEditor()) {
        applyLayout();
      }
    }
  }

  /**
   * Configures and invokes a layout algorthm
   */
  void applyLayout() {
    Layouter layouter = createLayouter(layoutOptions);
    applyLayout(layouter, layoutOptions.getBool("Activate Layout Morphing"));
  }

  /**
   * Creates and returns a Layouter instance according to the given layout options.
   */
  Layouter createLayouter(OptionHandler layoutOptions) {

    String layout = layoutOptions.getString("Layout Style");
    boolean label = layoutOptions.getBool("Activate Generic Labeling");

    CanonicMultiStageLayouter layouter = null;

    if (layout.equals("circular")) {
      CircularLayouter cl = new CircularLayouter();
      cl.getSingleCycleLayouter().setMinimalNodeDistance(100);
      layouter = cl;
    } else if (layout.equals("hierarchic")) {
      HierarchicLayouter hl = new HierarchicLayouter();
      //set some options
      hl.setMinimalLayerDistance(60);
      hl.setMinimalNodeDistance(20);

      //use left-to-right layout orientation
      OrientationLayouter ol = new OrientationLayouter();
      ol.setOrientation(OrientationLayouter.LEFT_TO_RIGHT);
      hl.setOrientationLayouter(ol);

      layouter = hl;
    } else if (layout.equals("organic")) {
      SmartOrganicLayouter ol = new SmartOrganicLayouter();
      //set some options
      ol.setPreferredEdgeLength(80);
      ol.setQualityTimeRatio(1.0);
      ol.setNodeOverlapsAllowed(false);
      layouter = ol;
    } else if (layout.equals("orthogonal")) {
      OrthogonalLayouter ol = new OrthogonalLayouter();
      //set some options
      layouter = ol;
    }

    if (layouter == null) usage();

    if (label) {
      // enable the generic labeling feature
      layouter.setLabelLayouterEnabled(true);
    }

    return layouter;
  }

  /**
   * Applies the given layout algorithm to the graph
   * residing in the view. depending on the parameter
   * the layout will be applied in an animated fashion
   * to the graph or not.
   */
  void applyLayout(Layouter layouter, boolean animated) {
    if (animated) {
      GraphLayout gl = new BufferedLayouter(layouter).calcLayout(view.getGraph2D());

      LayoutMorpher morpher = new LayoutMorpher( view, gl );
      morpher.setPreferredDuration( 800 );
      animationPlayer.animate( AnimationFactory.createEasedAnimation( morpher ) );
    } else {
      layouter.doLayout(view.getGraph2D());

      //adjusts the zoom and origin of the view to make the
      //whole graph visible
      view.fitContent();

      //an ALTERNATIVE to fitContent is updateWorldRect
      //adjusts view scrollbars, so that the whole graph is visible
      //does not change zoom on the view

      //view.updateWorldRect();

      view.updateView();
    }
  }


  void usage() {
    D.bug("USAGE: java demo.view.layout.LayoutDemoTmp " +
        "{organic,circular,random,hierarchic,orthogonal} [label] [anim]");
    System.exit(0);
  }

  public void addContentTo( final JRootPane rootPane ) {
    super.addContentTo( rootPane );
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        applyLayout();
      }
    });
  }

  public static void main(String[] args)
  {
    initLnF();
    final LayoutDemo demo = new LayoutDemo(args);

    demo.start(demo.getClass().getName());

    ////////////////// IMPORTANT ////////////////////
    // Graph2DView must be visible before
    // animated layout morphing can be performed
    /////////////////////////////////////////////////

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        demo.applyLayout();
      }
    });
  }
}
