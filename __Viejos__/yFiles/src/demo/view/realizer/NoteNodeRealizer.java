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
package demo.view.realizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import y.view.EditMode;
import y.view.Graph2DView;
import y.view.NodeRealizer;



/**
 * NodeRealizer implementation that draws a node as
 * a dog-eared note as thez are used in UML diagrams
 *
 * Executing this class will display a sample instance of this realizer.
 */
public class NoteNodeRealizer extends NodeRealizer
{
  /**
   * Obligatory default constructor.
   */
  public NoteNodeRealizer()
  {
    setSize(100,30);
    setFillColor(Color.lightGray);
  }
  
  /**
   * Obligatory copy constructor
   */
  public NoteNodeRealizer(NodeRealizer r)
  {
    super(r);
  }
  
  /**
   * Obligatory polymorphic copy constructor
   */
  public NodeRealizer createCopy(NodeRealizer r)
  {
    return new NoteNodeRealizer(r);
  }
  
  /**
   * Paints the node as a dog-eared rectangle.
   */
  public void paintNode(Graphics2D gfx)
  {
    if(isSelected())
    {
      gfx.setColor(getFillColor().darker());
      paintHotSpots(gfx);
    }
    else
      gfx.setColor(getFillColor());
    
    double cornerW = width > 20.0 ? 15.0 : 0.0;
    double cornerH = height > 20.0 ? 15.0 : 0.0;
    
    Polygon corner = new Polygon();
    corner.addPoint((int)(x+width-cornerW),(int)y);
    corner.addPoint((int)(x+width-cornerW),(int)(y+cornerH));
    corner.addPoint((int)(x+width),(int)(y+cornerH));
    
    gfx.fillRect((int)x,(int)y,(int)(width-cornerW),(int)cornerH);
    gfx.fillRect((int)x,(int)(y+cornerH),(int)(width),(int)(height-cornerH));
    gfx.setColor(Color.white);
    gfx.fillPolygon(corner);
    gfx.setColor(Color.black);
    gfx.drawPolygon(corner);
    gfx.drawLine((int)x,(int)y,(int)(x+width-cornerW),(int)(y)); //top
    gfx.drawLine((int)x,(int)(y+height),(int)(x+width),(int)(y+height)); //bottom
    gfx.drawLine((int)x,(int)y,(int)(x),(int)(y+height)); //left
    gfx.drawLine((int)(x+width),(int)(y+cornerH),  //right
                 (int)(x+width),(int)(y+height));
  
    paintText(gfx);
  }



  public static void addContentTo( final JRootPane rootPane )
  {
    final NoteNodeRealizer r = new NoteNodeRealizer();
    r.setLabelText("This is\na note");

    final Graph2DView view = new Graph2DView();
    view.getGraph2D().setDefaultNodeRealizer(r.createCopy());
    view.setAntialiasedPainting(true);
    view.getGraph2D().createNode();
    view.addViewMode(new EditMode());
    view.fitContent();

    rootPane.setContentPane(view);
  }

  /**
   * Launcher method. Execute this class to see a sample instantiation of
   * this node realizer in action.
   */
  public static void main(String[] args)
  {
    final JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    addContentTo(frame.getRootPane());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}


