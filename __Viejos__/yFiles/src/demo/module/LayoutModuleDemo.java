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
package demo.module;

import demo.view.DemoBase;
import y.module.YModule;
import y.option.OptionHandler;
import y.view.Arrow;
import y.view.hierarchy.HierarchyManager;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;

/**
 * Demonstrates how layout modules can be added to the GUI of an application.
 * A layout module is a layout algorithm combined
 * with an option dialog, that allows to change the
 * options of a layout algorithm interactively
 * (only available if layout is part of distribution).
 *
 */
public class LayoutModuleDemo extends DemoBase
{

  public LayoutModuleDemo()
  {
    //use a delta arrow to make edge directions clear
    view.getGraph2D().getDefaultEdgeRealizer().setArrow(Arrow.DELTA);

    //to enable loading of hierachically grouped graphs/
    HierarchyManager hierarchy = new HierarchyManager(view.getGraph2D());
    loadGraph( "resource/sample.gml" );
  }

  /**
   * Returns ViewActionDemo toolbar plus actions to trigger some layout algorithms
   */
  protected JToolBar createToolBar()
  {
    JToolBar bar = super.createToolBar();

    bar.addSeparator();

    bar.add(new LaunchModule(new y.module.OrganicLayoutModule(), "Organic"));
    bar.add(new LaunchModule(new y.module.RandomLayoutModule(), "Random" ));
    bar.add(new LaunchModule(new y.module.CircularLayoutModule(), "Circular" ));
    bar.add(new LaunchModule(new y.module.HierarchicLayoutModule(), "Hierarchic" ));
    bar.add(new LaunchModule(new y.module.IncrementalHierarchicLayoutModule(), "Incremental H." ));
    bar.add(new LaunchModule(new y.module.OrthogonalLayoutModule(), "Orthogonal" ));
    bar.add(new LaunchModule(new y.module.TreeLayoutModule(), "Tree" ));
    bar.add(new LaunchModule(new demo.module.DiagonalLayoutModule(), "Diagonal" ));


    return bar;
  }

  /**
   *  Launches a generic YModule. If the modules provides
   *  an option handler display it before the modules gets launched.
   */
  class LaunchModule extends AbstractAction
  {
    YModule module;

    LaunchModule( YModule module, String title )
    {
      super(title);
      this.module = module;
    }

    public void actionPerformed(ActionEvent e)
    {
      OptionHandler op = module.getOptionHandler();
      if( op != null) {
        if( !op.showEditor() )
          return;
      }
      module.start(view.getGraph2D());
    }
  }

  public static void main(String args[])
  {
    initLnF();
    LayoutModuleDemo demo = new LayoutModuleDemo();
    demo.start(demo.getClass().getName());
  }
}


      
