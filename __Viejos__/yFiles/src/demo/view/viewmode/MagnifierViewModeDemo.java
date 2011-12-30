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
package demo.view.viewmode;

import demo.view.DemoBase;
import y.view.EditMode;
import y.view.MagnifierViewMode;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  Demonstrates how to use a magnifying glass effect to zoom view regions locally.
 *
 *  Usage: to activate the magnifier select the "Use Magnifier" button. Move the mouse over the
 *  view canvas to move the magnifier. Note that you can even edit the graph while the magnifier is active.
 *  Use the mouse wheel to change the zoom factor of the magnifier. To change the radius of the magnifier
 *  with the mouse wheel, additionally keep the CTRL key pressed. To deactivate the magnifier again, deselect
 *  the "Use Magnifier" button.
 */
public class MagnifierViewModeDemo extends DemoBase
{
  MagnifierViewMode magnifierMode;
  JToggleButton magnifierButton;

  protected void initialize() {
    super.initialize();


    magnifierMode = new MagnifierViewMode();
    magnifierMode.setMagnifierRadius(100);
    magnifierMode.setMagnifierZoomFactor(2.0);

    loadGraph(getClass(), "resource/5.gml");

  }

  protected JToolBar createToolBar() {
    JToolBar toolBar = super.createToolBar();
    magnifierButton = new JToggleButton("Use Magnifier");
    magnifierButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(magnifierButton.isSelected()) {
          view.addViewMode(magnifierMode);
        } else {
          view.removeViewMode(magnifierMode);
        }
      }
    });

    toolBar.add(magnifierButton);
    return toolBar;
  }

  protected void registerViewModes() {
    EditMode editMode = new EditMode();
    view.addViewMode(editMode);
  }

  public static void main(String args[])
  {
    initLnF();
    MagnifierViewModeDemo demo = new MagnifierViewModeDemo();
    demo.start();
  }
}
