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

import java.util.Map;

import y.base.YCursor;
import y.base.YList;
import y.geom.YPoint;
import y.io.gml.DefaultParserFactory;
import y.io.gml.GraphParser;
import y.io.gml.ItemParser;
import y.io.gml.LineParser;
import y.io.gml.GMLTokenizer.Callback;
import y.view.Graph2D;
import y.view.ImageNodeRealizer;
import y.view.NodeRealizer;
import y.view.Port;


/** This class is an implementation of the ParserFactory interface.
 * This implementation is capable of parsing the basic gml features.
 * The work is delegated to instances of GraphParser, NodeParser and EdgeParser
 */
public class ParserFactory extends DefaultParserFactory
{
  public Callback createNodeParser(Graph2D graph, Callback graphParser)
  {
    GraphParser gp = (GraphParser) graphParser;
    ItemParser parser = new NodeParser(graph, gp.getId2Node());
    return parser;
  }
  
  private static final class NodeParser extends y.io.gml.NodeParser {
    
    private LineParser lineParser;
    
    NodeParser(Graph2D graph, Map id2NodeMap){
      super(graph, id2NodeMap);
      this.nodeGraphics.addChild("PortCandidates", lineParser = new LineParser());
    }
    
    /** this method is called when the parsing of the node section
     * has ended. This implementation calls
     * <CODE>super.end()</CODE> and then tries to set the additional
     * FixedPortsNodeRealizer attributes: paintingPorts and portCandidates
     */  
    public void end(){
      super.end();
      NodeRealizer item = (NodeRealizer) nodeGraphics.getItem();
      if (item instanceof ImageNodeRealizer){ // maybe fixedportnr
        FixedPortsNodeRealizer fpnr = new FixedPortsNodeRealizer(item);
        fpnr.setPaintingPorts(nodeGraphics.getBoolean("paintingPorts"));
        YList list = fpnr.getPortCandidates();
        list.clear();
        for (YCursor c = lineParser.getPointList().cursor(); c.ok(); c.next()){
          YPoint p = (YPoint) c.current();
          list.add(new Port(p.getX(), p.getY()));
        }
        graph.setRealizer(this.nr.getNode(), fpnr);
      }
      
    }
  }
}

