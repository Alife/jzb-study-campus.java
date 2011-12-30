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
package demo.view.ports;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import y.base.ListCell;
import y.base.YCursor;
import y.base.YList;
import y.geom.YPoint;

import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.ImageNodeRealizer;
import y.view.NodeRealizer;
import y.view.Port;

/**
 * Modified version of ImageNodeRealizer, that maintains a list of possible
 * port coordinates, suppresses resizing of the node and modifies the behavior
 * 
 */
public class FixedPortsNodeRealizer extends ImageNodeRealizer
{
  /** Holds value of property paintingPorts. */
  private boolean paintingPorts = true;
  
  /** Holds value of property portCandidates. */
  private YList portCandidates;
  
  /** Creates a new instance of FixedPortsNodeRealizer */
  public FixedPortsNodeRealizer()
  {
    init();
  }

  /** Creates a new instance of FixedPortsNodeRealizer */
  public FixedPortsNodeRealizer(NodeRealizer arg)
  {
    super(arg);
    if (arg instanceof FixedPortsNodeRealizer){
      FixedPortsNodeRealizer fpnr = (FixedPortsNodeRealizer)arg;
      this.portCandidates = 
        new YList(fpnr.portCandidates);
      this.paintingPorts = fpnr.paintingPorts;
    } 
    init();
  }
  
  /**
   * assures the list of port candidates is non null and not empty
   */
  private void init(){
    if (portCandidates == null){
      portCandidates = new YList();
      portCandidates.add(new Port(0,0));
    }
  }
  
  /** Getter for property portCandidates.
   * @return Value of property portCandidates.
   *
   */
  public YList getPortCandidates()
  {
    return this.portCandidates;
  }
  
  /**
   * overwritten in order to make MovePortMode use the port candidates
   */
  public YList getPortCandidates(double grid){
    YList candidates = new YList();
    for (YCursor c = getPortCandidates().cursor(); c.ok(); c.next()){
      Port p = (Port) c.current();
      candidates.add(new YPoint(p.getX(this), p.getY(this)));
    }
    return candidates;
  }
  
  /**
   * this method can be modified in order to present a subset of port candidates
   * depending on the edge to be created.
   * @param graph the graph that contains everything
   * @param edge the edge which has been created
   * @param source a flag indicating whether to create a list of possible source ports
   * respectively target ports
   */
  public YList createCandidateList(Graph2D graph, EdgeRealizer edge, boolean source){
    return getPortCandidates();
  }
  
  /**
   * This method chooses from a list of given ports for an edge
   * a suitable port, given an initial placement.
   * @param edge the affected edge
   * @param source whether we look at the source node
   * @param x the initial x offset
   * @param y the initial y offset
   * @param ports a list of Port objects (candidates)
   */  
  public Port snapCandidate(Graph2D graph, EdgeRealizer edge, boolean source, double x, double y){
    
    YList ports = createCandidateList(graph, edge, source);
    
    if (ports == null || ports.size() < 1) return null; // do nothing

    // find the closest port with regards to the getDistance function
    Port closest = (Port) ports.first();
    double dist = getDistance(x,y,closest);
    
    for (YCursor cursor = ports.cursor(); cursor.ok(); cursor.next()){
      Port p = (Port) cursor.current();
      double d2 = getDistance(x,y, p);
      if (d2 < dist){
        dist = d2;
        closest = p;
      }
    }
    return new Port(closest);
  }
  
  /**
   * This method calculates a metric for ports and points
   * @param x the initial x offset
   * @param y the initial y offset
   * @param port the port
   * @return the distance between the point (x,y) and the port
   */  
  public static double getDistance(double x, double y, Port port){
    return Math.sqrt((x-port.getOffsetX())*(x-port.getOffsetX()) 
      + (y-port.getOffsetY()) * (y-port.getOffsetY()));
  }
  
  /**
   * important deep copy method
   */
  public NodeRealizer createCopy(NodeRealizer arg){
    return new FixedPortsNodeRealizer(arg);
  }
  
  /**
   * suppress resizing of node by not offering any hotspots (and hits)
   */
  public byte hotSpotHit(double x, double y){
    return HOTSPOT_NONE;
  }
  
  private static final Ellipse2D ellipse = new Ellipse2D.Double();
  
  public void paintNode(Graphics2D g2d){
    super.paintNode(g2d);
    if (isSelected()){
      g2d.setColor(getHotSpotColor());
      g2d.draw(getBoundingBox());
    }
    if (paintingPorts){
      g2d.setColor(getHotSpotColor());
      for (ListCell cell = getPortCandidates().firstCell(); cell != null; cell = cell.succ()){
        Port p = (Port) cell.getInfo();
        ellipse.setFrame(p.getX(this)-3.0,p.getY(this)-3.0,6.0,6.0);
        g2d.setColor(Color.red);
        g2d.fill(ellipse);
        g2d.setColor(Color.black);
        g2d.draw(ellipse);
      }
    }
  }
  
  /** 
   * overwritten to do nothing, since this realizer does not support resizing
   */
  public void paintHotSpots(Graphics2D g2d){
    return;
  }
  
  /** Getter for property paintingPorts.
   * @return Value of property paintingPorts.
   *
   */
  public boolean isPaintingPorts()
  {
    return this.paintingPorts;
  }
  
  /** Setter for property paintingPorts.
   * @param paintingPorts New value of property paintingPorts.
   *
   */
  public void setPaintingPorts(boolean paintingPorts)
  {
    this.paintingPorts = paintingPorts;
  }
  
  /**
   * overwritten to suppress the clipping of edges on the boundary of the node
   */
  public boolean findIntersection(double ix, double iy, double ox, double oy, Point2D res){
    res.setLocation(ix, iy);
    return true;
  }

  /**
   * serializes this node to an ObjectOutputStream by appending the port 
   * candidates and additional properties of this realizer to the serialization
   * data of the super class.
   */
  public void write(ObjectOutputStream oos) throws IOException{
    super.write(oos);
    YList ports = getPortCandidates();
    oos.writeInt(ports.size());
    for (YCursor yc = ports.cursor(); yc.ok(); yc.next()){
      Port p = (Port)yc.current();
      p.write(oos);
    }
    oos.writeBoolean(isPaintingPorts());
  }

  /**
   * deserializes this node from an ObjectInputStream by reading the port
   * candidates and additional properties of this realizer
   */
  public void read(ObjectInputStream ois) throws IOException, ClassNotFoundException{
    super.read(ois);
    YList ports = getPortCandidates();
    ports.clear();
    int count = ois.readInt();
    while (count-- > 0){
      Port p = new Port();
      p.read(ois);
      ports.add(p);
    }
    setPaintingPorts(ois.readBoolean());
  }
}
