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
package demo.io;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

import y.io.GMLIOHandler;
import y.util.D;
import y.view.EditMode;
import y.view.Graph2DView;

/** 
 *  Demonstrates simple usage of the Graph2DView.
 *  The initial graph will be read from a GML file,
 *  that can be specified on the command line.
 *
 *  The edit mode won't allow node/edge creation
 *  
 */
public class SimpleGMLDemo extends JPanel 
{
  Graph2DView view;
  
  public SimpleGMLDemo()
  {
    setLayout(new BorderLayout());  
    view = new Graph2DView();
    EditMode mode = new EditMode();
    
    //editing not allowed anymore
    mode.allowNodeCreation(false);
    mode.allowEdgeCreation(false);
    mode.allowBendCreation(false);
    
    view.addViewMode(mode);
    add(view);
  }
  
  /** read in a GML file and display it's contents.
   *  the view mode will not allow node and edge creation 
   *  anymore.
   */
  void loadGML(URL url)
  {
    GMLIOHandler ioh = new GMLIOHandler();
    //read file into view
    try
    {
      ioh.read(view.getGraph2D(),url);
      //fit the content nicely into the view
      view.fitContent();
      //update the view
      view.updateView();
    } catch (IOException ioe){
      D.show(ioe);
    }
  }
  
  public static void main(String args[])
  {
    JFrame frame = new JFrame("GML Demo");
    
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
    
    SimpleGMLDemo demo = new SimpleGMLDemo();
    frame.setContentPane(demo);
    frame.pack();
    
    if(args.length == 0)
    {
      demo.loadGML(demo.getClass().getResource("resource/graph1.gml"));
    }
    else
    {
      File file = new File(args[0]);
      if(file.canRead())
        try {
          System.out.println("file >>> " + file.toURL());
          demo.loadGML(file.toURL());
        }catch(MalformedURLException mex) {}
    }
    
    frame.setVisible(true);
  }
}


