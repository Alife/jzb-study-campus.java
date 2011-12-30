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

import java.io.IOException;

import y.base.YCursor;
import y.io.gml.DefaultEncoderFactory;
import y.io.gml.GMLEncoder;
import y.io.gml.NodeObjectEncoder;
import y.io.gml.NodeRealizerObjectEncoder;
import y.io.gml.ObjectEncoder;
import y.view.Port;


/**
 * This class implements the y.io.gml.EncoderFactory interface by creating
 * a GML Encoder, that is capable of encoding Graphs with FixedPortsNodeRealizer
 * nodes.
 */
public class EncoderFactory extends DefaultEncoderFactory
{
  public ObjectEncoder createNodeEncoder(ObjectEncoder graphEncoder)
  {
    ObjectEncoder nre =
      new FixedPortsNodeRealizerEncoder(new NodeRealizerObjectEncoder());
    ObjectEncoder nodeEncoder = new NodeObjectEncoder(nre, null);
    return nodeEncoder;
  }
  
  private static final class FixedPortsNodeRealizerEncoder implements ObjectEncoder{
    
    private ObjectEncoder delegate;
    
    FixedPortsNodeRealizerEncoder(ObjectEncoder delegate){
      this.delegate = delegate;
    }
    
    public void encode(Object item, GMLEncoder encoder) throws IOException
    {
      if (delegate != null){
        delegate.encode(item, encoder);
      }
      if (item instanceof FixedPortsNodeRealizer){
        FixedPortsNodeRealizer fpnr = (FixedPortsNodeRealizer) item;
        encoder.addAttribute("paintingPorts", fpnr.isPaintingPorts());
        encoder.beginSection("PortCandidates");
        for (YCursor c = fpnr.getPortCandidates().cursor(); c.ok(); c.next()){
          Port p = (Port) c.current();
          encoder.beginSection("point");
          encoder.addAttribute("x",(float)p.getOffsetX());
          encoder.addAttribute("y",(float)p.getOffsetY());
          encoder.endSection(); //point
        }
        encoder.endSection();
      }
    }
    
  }
}

