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


import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import y.util.YVersion;
import y.view.EditMode;
import y.view.Graph2DView;
import y.view.NodeRealizer;
import y.view.ShapeNodeRealizer;

import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 * This class represents a custom ShapeNodeRealizer that represents a
 * rounded rectangle with custom rounding radii.
 */
public class RoundRectNodeRealizer extends ShapeNodeRealizer
{
  private RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0,0,10,10,20,30);


  public RoundRectNodeRealizer()
  {
    super();
    this.setShapeType(ROUND_RECT);
  }

  protected RoundRectangle2D.Double getRect(){
      if (rect == null){
          rect = new RoundRectangle2D.Double(0,0, 10,10,20, 30);
      }
      return rect;
  }

  /**
   * overwrite setShapeType to set specialized shape...
   */
  public void setShapeType(byte type){
      super.setShapeType(type);
      RoundRectangle2D.Double rect = getRect();
      if (type == ROUND_RECT){
          rect.x = x;
          rect.y = y;
          rect.width = width;
          rect.height = height;
          shape = rect;
      }
  }

  public RoundRectNodeRealizer(NodeRealizer r)
  {
    super(r);
    setShapeType(ROUND_RECT);
    if (r instanceof RoundRectNodeRealizer){
        setArcWidth(((RoundRectNodeRealizer)r).getArcWidth());
        setArcHeight(((RoundRectNodeRealizer)r).getArcHeight());
    }
  }


  public NodeRealizer createCopy(NodeRealizer r)
  {
    return new RoundRectNodeRealizer(r);
  }

  /**
   * Writes out this realizer in a serialized form. This method 
   * will be used by YGFIOHandler to serialize this NodeRealizer.
   */
  public void write(ObjectOutputStream out) throws IOException
  {
    //write out a version tag. version tags help to provide future
    //serialization compatibility when node realizer features
    //change.    
    out.writeByte(YVersion.VERSION_1);
    out.writeDouble(getArcHeight());
    out.writeDouble(getArcWidth());
    //write out the shape node realizer features
    super.write(out);
  }

  /**
   * Reads in the serialized form of this realizer. The realizer must have been
   * written out before by it's {@link #write(ObjectOutputStream)} method.
   * This method will be used by YGFIOHandler to deserialize this NodeRealizer.
   */
  public void read(ObjectInputStream in) throws IOException,
                                                ClassNotFoundException
  {
    switch(in.readByte()) {
    case YVersion.VERSION_1:
      super.read(in);
      setArcHeight(in.readDouble());
      setArcWidth(in.readDouble());
      break;
    default:
      //trouble
    }
  }

  /** Getter for property arcHeight.
   * @return Value of property arcHeight.
   *
   */
  public double getArcHeight() {
      return getRect().archeight;
  }

  /** Setter for property arcHeight.
   * @param arcHeight New value of property arcHeight.
   *
   */
  public void setArcHeight(double arcHeight) {
      getRect().archeight = arcHeight;
  }

  /** Getter for property arcWidth.
   * @return Value of property arcWidth.
   *
   */
  public double getArcWidth() {
      return getRect().arcwidth;
  }

  /** Setter for property arcWidth.
   * @param arcWidth New value of property arcWidth.
   *
   */
  public void setArcWidth(double arcWidth) {
      getRect().arcwidth = arcWidth;
  }



  public static void addContentTo( final JRootPane rootPane )
  {
    final RoundRectNodeRealizer r = new RoundRectNodeRealizer();
    r.setArcHeight(20);
    r.setArcWidth(40);
    r.setSize(100,40);
    r.setLabelText("Arc=" + r.getArcWidth() + ':' + r.getArcHeight());

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
