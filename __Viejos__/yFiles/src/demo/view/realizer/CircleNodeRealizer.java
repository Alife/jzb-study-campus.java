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


import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JRootPane;

import y.util.YVersion;
import y.view.EditMode;
import y.view.Graph2DView;
import y.view.NodeRealizer;
import y.view.ShapeNodeRealizer;

/**
 * This class represents a customized ShapeNodeRealizer similar to
 * the ellipse type, but restricts the aspect ratio to be 1, i.e. the
 * ellipse to be a circle at all times.
 */
public class CircleNodeRealizer extends ShapeNodeRealizer
{
  private Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 10,10);

  public CircleNodeRealizer()
  {
    super();
    this.setShapeType(HEXAGON); //anything but ELLIPSE OR RECT, since intersection handling
                                //in superclass handles these cases specially.
    updateShape();
  }

  protected void updateShape(){
      this.shape = circle;
      double radius = Math.min(getWidth(), getHeight());
      circle.width = radius;
      circle.height = radius;
      circle.x = getCenterX() - radius/2;
      circle.y = getCenterY() - radius/2;
  }

  public CircleNodeRealizer(NodeRealizer r)
  {
    super(r);
    setShapeType(HEXAGON);
  }

  /**
   * Paints the node on the given graphics context.
   */
  public void paintNode(Graphics2D gfx)
  {
    updateShape();

    super.paintNode(gfx);
  }

  public void setLocation(double x, double y){
      super.setLocation(x,y);
      updateShape();
  }

  public void setSize(double w, double h){
      super.setSize(w, h);
      updateShape();
  }

  /**
   * Returns the radius of this realizer.
   */
  public double getRadius()
  {
    return Math.min(getWidth(),getHeight())/2.0;
  }

  /**
   * Sets the radius of this node. This will effectively change the
   * size of this node.
   */
  public void setRadius(double r)
  {
    setSize(2*r, 2*r);
  }

  public NodeRealizer createCopy(NodeRealizer r)
  {
    return new CircleNodeRealizer(r);
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
    //write out the shape node realzier features
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
      break;
    default:
      //trouble
    }
  }

  /** Sets the width of this realizer. This method delegates to
   *  {@link #setSize(double, double)}.
   *
   */
  public void setWidth(double width) {
      super.setWidth(width);
      updateShape();
  }

  /** Sets the height of this realizer. This method delegates to
   *  {@link #setSize(double,double)}.
   *
   */
  public void setHeight(double height) {
      super.setHeight(height);
      updateShape();
  }

  /**
   * Set the x coordinate of the center of the node.
   *
   */
  public void setCenterX(double x) {
      super.setCenterX(x);
      updateShape();
  }

  /**
   * Set the coordinates of the center of the node.
   *
   */
  public void setCenter(double x, double y) {
      super.setCenter(x, y);
      updateShape();
  }

  /**
   * Set the y coordinate of the center of the node.
   *
   */
  public void setCenterY(double y) {
      super.setCenterY(y);
      updateShape();
  }

  /**
   * Sets the frame of the realizer, i.e. its size and
   * its location.
   *
   */
  public void setFrame(Rectangle2D frame) {
      super.setFrame(frame);
      updateShape();
  }

  /**
   * Sets the Y-Coordinate of the upper left corner of the node.
   *
   */
  public void setY(double yp) {
      super.setY(yp);
      updateShape();
  }

  /**
   * Sets the frame of the realizer, i.e. its size and
   * its location.
   *
   */
  public void setFrame(double x, double y, double width, double height) {
      super.setFrame(x, y, width, height);
      updateShape();
  }

  /**
   * Sets the X-Coordinate of the upper left corner of the node.
   *
   */
  public void setX(double xp) {
      super.setX(xp);
      updateShape();
  }



  public static void addContentTo( final JRootPane rootPane )
  {
    final CircleNodeRealizer r = new CircleNodeRealizer();
    r.setRadius(50);
    r.setLabelText("Circle");

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
   * this node realizer in action. Try to change the size of this realizer by 
   * dragging the 8 hotspot handles that are visible around the node when selected.  
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

