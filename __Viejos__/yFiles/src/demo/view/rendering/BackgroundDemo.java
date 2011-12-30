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
package demo.view.rendering;

import demo.view.DemoBase;
import y.option.EnumOptionItem;
import y.option.MappedListCellRenderer;
import y.option.OptionHandler;
import y.view.DefaultBackgroundRenderer;
import y.view.EdgeRealizer;
import y.view.LineType;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

/**
 *  Demonstrates different modes for drawing images in the
 *  background of Graph2DView.
 */

public class BackgroundDemo extends DemoBase
{
  DefaultBackgroundRenderer renderer;

  final static String bgImages[] = {
    "resource/yWorksBig.png",
    "resource/yWorksSmall.gif",
    "resource/usamap.gif",
    "resource/ySplash.jpg",
    "resource/tile.jpg",
    "<NONE>"
  };


  public BackgroundDemo()
  {
    renderer = new DefaultBackgroundRenderer(view);
    renderer.setImageResource(getClass().getResource(bgImages[0]));
    renderer.setMode(DefaultBackgroundRenderer.CENTERED);
    renderer.setColor(Color.white);
    view.setBackgroundRenderer(renderer);
    view.setPreferredSize(new Dimension(600,400));

    view.setWorldRect(0,0,1000,1000);

    //use thicker edges 
    EdgeRealizer er = view.getGraph2D().getDefaultEdgeRealizer();
    er.setLineType(LineType.LINE_2);
  }

  /**
   * Returns ViewActionDemo toolbar plus a button to change the 
   * background of the view.
   */
  protected JToolBar createToolBar()
  {
    JToolBar bar = super.createToolBar();
    bar.addSeparator();
    bar.add(new ChangeBackground());
    return bar;
  }

  /**
   * An action that displays a dialog that allows to change the background
   * properties of the view.
   */
  class ChangeBackground extends AbstractAction
  {
    /** The powerful yFiles dialog generator */
    OptionHandler op;
    Hashtable  xlate;

    ChangeBackground()
    {
      super("Background");
      xlate = new Hashtable(11);
      xlate.put(new Byte(DefaultBackgroundRenderer.FULLSCREEN),"Fullscreen");
      xlate.put(new Byte(DefaultBackgroundRenderer.TILED),     "Tiled");
      xlate.put(new Byte(DefaultBackgroundRenderer.BRICKED),   "Bricked");
      xlate.put(new Byte(DefaultBackgroundRenderer.CENTERED),  "Centered");
      xlate.put(new Byte(DefaultBackgroundRenderer.PLAIN),     "Plain");
      xlate.put(new Byte(DefaultBackgroundRenderer.DYNAMIC),   "Dynamic");
    }

    public void actionPerformed(ActionEvent e)
    {
      if(op == null)
      {
        op = new OptionHandler("Background");
        op.addEnum("Mode",
                   xlate.keySet().toArray(),
                   new Byte(renderer.getMode()),
                   new MappedListCellRenderer(xlate));
        op.addColor("Color",renderer.getColor());
        op.addEnum("Image",bgImages,0)
          // disable unwanted I18N
          .setAttribute(EnumOptionItem.ATTRIBUTE_RENDERER,
                        new DefaultListCellRenderer());
      }

      String oldImage = op.getString("Image");

      if(op.showEditor())
      {
        renderer.setMode(((Byte)op.get("Mode")).byteValue());
        renderer.setColor((Color)op.get("Color"));
        String imageSrc = op.getString("Image");
        if(imageSrc.equals("<NONE>"))
          renderer.setImage(null);
        else
          renderer.setImageResource(this.getClass().getResource(imageSrc));
        view.updateView();
      }
     }
  }

  public static void main(String args[])
  {
    BackgroundDemo demo = new BackgroundDemo();
    demo.start("Background Demo");
  }
}


      
