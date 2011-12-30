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
package demo.view.advanced;

import demo.view.DemoBase;
import y.view.Graph2DUndoManager;

import javax.swing.JToolBar;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * This Demo shows how to use Undo/Redo functionality built into yFiles.
 */
public class UndoRedoDemo extends DemoBase
{
  private Graph2DUndoManager undoManager;

  protected void init()
  {

  }

  /**
   * Returns the undo manager for this application. Also, if not already done - it creates 
   * and configures it.
   */
  protected Graph2DUndoManager getUndoManager()
  {
    if(undoManager == null)
    {
      undoManager = new Graph2DUndoManager();
      //make it listen to graph structure changes
      view.getGraph2D().addGraphListener(undoManager);
      //make it handle backup realizer requests. 
      view.getGraph2D().setBackupRealizersHandler(undoManager);
      //assign the graph view as view container so we get view updates
      //after undo/redo actions have been performed. 
      undoManager.setViewContainer(view);
    }
    return undoManager;
  }


  public JToolBar createToolBar()
  {
    JToolBar bar = super.createToolBar();

    bar.addSeparator();
    
    //add undo action to toolbar
    Action action = getUndoManager().getUndoAction();
    action.putValue(Action.SMALL_ICON,
        new ImageIcon(DemoBase.class.getResource("resource/Undo16.gif")));
    action.putValue(Action.SHORT_DESCRIPTION, "Undo");
    bar.add(action);

    //add redo action to toolbar
    action = getUndoManager().getRedoAction();
    action.putValue(Action.SMALL_ICON,
        new ImageIcon(DemoBase.class.getResource("resource/Redo16.gif")));
    action.putValue(Action.SHORT_DESCRIPTION, "Redo");
    bar.add(action);
    return bar;
  }

  public static void main(String args[])
  {
    initLnF();
    UndoRedoDemo demo = new UndoRedoDemo();
    demo.start("Undo/Redo Demo");
  }

}

    

      
