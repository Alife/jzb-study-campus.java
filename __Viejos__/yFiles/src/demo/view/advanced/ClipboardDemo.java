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
import y.view.Graph2DClipboard;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

/**
 * This class demonstrates how to use the yFiles clipboard
 * functionality to cut, copy and paste parts of a graph.
 */
public class ClipboardDemo extends DemoBase
{
  Action cutAction;
  Action copyAction;
  Action pasteAction;

  public ClipboardDemo()
  {
    view.getCanvasComponent().getActionMap().put("CUT", cutAction);
    view.getCanvasComponent().getInputMap().put(
        KeyStroke.getKeyStroke(KeyEvent.VK_X,  KeyEvent.CTRL_MASK),"CUT");

    view.getCanvasComponent().getActionMap().put("COPY", copyAction);
    view.getCanvasComponent().getInputMap().put(
        KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK), "COPY");

    view.getCanvasComponent().getActionMap().put("PASTE", pasteAction);
    view.getCanvasComponent().getInputMap().put(
        KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK), "PASTE");
  }

  protected void registerViewActions() {
    super.registerViewActions();
    //create new clipboard.
    Graph2DClipboard clipboard = new Graph2DClipboard(view);

    //get Cut action from clipboard
    cutAction = clipboard.getCutAction();
    cutAction.putValue(Action.SMALL_ICON,
        new ImageIcon( DemoBase.class.getResource("resource/Cut16.gif")));
    cutAction.putValue(Action.SHORT_DESCRIPTION, "Cut");

    //get Copy action from clipboard
    copyAction = clipboard.getCopyAction();
    copyAction.putValue(Action.SMALL_ICON,
        new ImageIcon( DemoBase.class.getResource("resource/Copy16.gif")));
    copyAction.putValue(Action.SHORT_DESCRIPTION, "Copy");

    //get Paste action from clipboard
    pasteAction = clipboard.getPasteAction();
    pasteAction.putValue(Action.SMALL_ICON,
        new ImageIcon( DemoBase.class.getResource("resource/Paste16.gif")));
    pasteAction.putValue(Action.SHORT_DESCRIPTION, "Paste");
  }

  protected JToolBar createToolBar() {
    JToolBar bar = super.createToolBar();
    bar.addSeparator();

    bar.add(cutAction);
    bar.add(copyAction);
    bar.add(pasteAction);

    return bar;
  }

  public static void main(String args[])
  {
    initLnF();
    ClipboardDemo demo = new ClipboardDemo();
    demo.start(demo.getClass().getName());
  }

}

    

      
