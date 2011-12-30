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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import y.util.D;
import y.io.GMLIOHandler;
import y.io.YGFIOHandler;
import y.io.IOHandler;
import y.io.JPGIOHandler;
import y.io.GIFIOHandler;
import y.io.ImageOutputHandler;
import y.view.Graph2DView;
import y.view.Graph2D;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.io.IOException;
import y.view.hierarchy.HierarchyManager;


/**
 * This class implements a command line driven graph format converter.
 * Possible input formats are GML or YGF.
 * Output formats are GML, YGF, GIF, and JPG.
 * <br>
 * Additionally, it is possible to write to the formats WMF, GraphML,
 * ZIPGraphML, SVG, and SVGZ and
 * to read from the formats GraphML and ZIPGraphML in case the corresponding
 * yFiles extension packages are installed.
 * The size of some output formats can be specified.
 */
public class GraphFormatConverter
{
  private Collection ioHandlers;
  private int outputWidth = -1, outputHeight = -1;
  private String inFile = null, outFile = null;
  
  
  private static void usage(String msg)
  {
    System.err.println(msg + "\n\n" +
    "Usage: java demo.io.GraphFormatConverter -in <infile> -out <outfile> [options]\n" +  
    "Usage: where the format of infile is YGF or GML \n" + 
    "Usage: and the format of outfile in in YGF, GML, JPG or GIF.\n" +
    "Usage: SVG/SVGZ output needs the ySVG extension package.\n" + 
    "Usage: GRAPHML/(ZIP)GRAPHML input and output needs the GraphML extension package.\n" + 
    "Usage: WMF output needs the yWMF extension package.\n" + 
    "Usage: File formats are determined by the file name extensions.\n" + 
    "Usage: Additional options which work for some output formats:\n" + 
    "Usage: -width <w>   the width of the output format\n" + 
    "Usage: -height<h>   the height of the output format\n"+
    "Usage:  If neither option is specified, a value of 1024\n"+
    "Usage:  is used for both dimensions\n");
    System.exit(1);
  }
  
  private static void error(String msg)
  {
    System.err.println(msg);
    System.exit(1);
  }
  
  /** 
   * Creates a new instance of GraphFormatConverter.
   * Adds all known IOHandlers to the conversion engine
   */
  public GraphFormatConverter()
  {
    ioHandlers = new Vector();
    ioHandlers.add(new YGFIOHandler());
    ioHandlers.add(new GMLIOHandler());
    ioHandlers.add(new GIFIOHandler());
    ioHandlers.add(new JPGIOHandler());
    try { //try to support SVG(Z) output format if it is present
      ioHandlers.add((IOHandler)Class.forName("yext.svg.io.SVGIOHandler").newInstance());
      ioHandlers.add((IOHandler)Class.forName("yext.svg.io.SVGZIOHandler").newInstance());
    }
    catch(ClassNotFoundException cnfex)
    {
      //SVG(Z) format disabled. Put ySVG extension package in your classpath
    }
    catch(Exception ex)
    {
      D.trace(ex);
    }

    try { //try to support GraphML input and output formats if they are present
      ioHandlers.add((IOHandler)Class.forName("yext.graphml.graph2D.GraphMLIOHandler").newInstance());
      ioHandlers.add((IOHandler)Class.forName("yext.graphml.graph2D.ZipGraphMLIOHandler").newInstance());
    }
    catch(ClassNotFoundException cnfex)
    {
      //(Zip)GraphML format disabled. Put GraphML extension package in your classpath
    }
    catch(Exception ex)
    {
      D.trace(ex);
    }

    try { //try to support WMF output format if it is present
      ioHandlers.add((IOHandler)Class.forName("yext.wmf.io.WMFIOHandler").newInstance());
    }
    catch(ClassNotFoundException cnfex)
    {
      //WMF format disabled. Put WMF extension package in your classpath
    }
    catch(Exception ex)
    {
      D.trace(ex);
    }

  }
  
  /**
   * does the conversion specified on the command line.
   * @param args the command line arguments.
   */
  public void convert(String[] args)
  {
     parseArgs(args);
     
     Graph2D graph = new Graph2D();
     
     //add HierarchyManager in case the input and output files
     //are able to handle graph hierarchy information
     HierarchyManager hierarchy = new HierarchyManager(graph);
     
     //read in the graph using inpoutHandler
     IOHandler inputHandler = getIOHandler(inFile);
     
     if(inputHandler != null && inputHandler.canRead())
     { 
       try
       {
         inputHandler.read(graph, inFile);
       }
       catch(IOException iex)
       {
         error("Error while decoding file " + inFile + "\n" + iex);
       }
     }
     else
     {
       usage("Can't determine input format");
     }
     
     //write out the graph using outputHandler
     IOHandler outputHandler = getIOHandler(outFile);
     
     if(outputHandler != null && outputHandler.canWrite())
     {
       Graph2DView view = null;
       if(outputHandler instanceof ImageOutputHandler)
       {
         //configure rendering component for image formats
         view = ((ImageOutputHandler)outputHandler).createDefaultGraph2DView(graph);
       }
       else 
       {
         view = new Graph2DView(graph);
       }  
       configureView(view);
       //set the viewport view to the current view of the graph.
       graph.setCurrentView(view);
       try
       {
         outputHandler.write(graph, outFile);
       }
       catch(IOException iex)
       {
         error("Error while encoding file " + outFile + "\n" + iex);
       }
       //deregister the viewport view for the graph again. 
       graph.removeView(view);
     }
     else
     {
        usage("Can't determine output format");
     }
     
  }
  
  /**
   * Configures the view that is used as rendering environment for some
   * output formats.
   */ 
  private void configureView(Graph2DView view)
  {
    Graph2D graph = view.getGraph2D();
    Rectangle box = graph.getBoundingBox();
    Dimension dim = getOutputSize(box);
    view.setSize(dim);
    view.zoomToArea(box.getX()-10,box.getY()-10,box.getWidth()+20,box.getHeight()+20);
    view.setPaintDetailThreshold(0.0); //never switch to less detail mode
  }
    
  /**
   * Parses the command line arguments and set attributes
   */
  public void parseArgs(String[] args)
  {
     for(int i = 0; i < args.length; i++)
     {
       if(args[i].equals("-in") && inFile == null)
       {
          inFile = args[++i];
       }
       else if(args[i].equals("-out") && outFile == null)
       {
         outFile = args[++i];
       }
       else if(args[i].equals("-width"))
       {
         outputWidth = Integer.parseInt(args[++i]);
       }
       else if(args[i].equals("-height"))
       {
         outputHeight = Integer.parseInt(args[++i]);
       }  
     }
     
     if(inFile == null)
     {
       usage("No input file specified");
     }
     
     if(outFile == null)
     {
       usage("No output file specified");
     }
  }
  
  /**
   * Returns the output size of image formats by
   * inspecting the input size of the graph and the output size
   * parameters.
   */
  private Dimension getOutputSize(Rectangle inBox)
  {
    if(outputWidth > 0 && outputHeight > 0)
    {
      //output completely specified. use it
      return new Dimension((int)outputWidth,(int)outputHeight);
    }
    else if(outputWidth > 0)
    {
      //output width specified. determine output height
      return new Dimension(outputWidth, 
                           (int)(outputWidth*(inBox.getHeight()/inBox.getWidth())));
    }
    else if(outputHeight > 0)
    {
      //output height specified. determine output width
      return new Dimension((int)(outputHeight*(inBox.getWidth()/inBox.getHeight())),
                           outputHeight);
    }
    else //no output size specified
    {
      //no output size specified. use input size, but only if smaller than 1024
      double width = inBox.getWidth();
      double height = inBox.getHeight();
      //scale down if necessary, keeping aspect ratio
      if(width > 1024) {
        height *= 1024/width;
        width = 1024;
      }
      if(height > 1024) {
        width *= 1024/height;
        height = 1024;
      }
      return new Dimension((int)width,(int)height);
    }
  }
    
  /**
   * returns the IOHandler that is responsible for files with the
   * given name.
   */
  private IOHandler getIOHandler(String fileName)
  {
    for(Iterator iter = ioHandlers.iterator(); iter.hasNext();)
    {
      IOHandler ioh = (IOHandler)iter.next();
      if(fileName.endsWith(ioh.getFileNameExtension()))
        return ioh;
    }
    return null;
  }
  
 //////////////////////////////////////////////////////////////////////////////
 // STATIC LAUNCHER SECTION ///////////////////////////////////////////////////
 //////////////////////////////////////////////////////////////////////////////
  
  public static void main(String[] args)
  {
     GraphFormatConverter converter = new GraphFormatConverter();
     converter.convert(args);
  }
 
}
