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

import y.base.Node;
import y.base.NodeMap;
import y.io.GMLIOHandler;
import y.io.gml.EncoderFactory;
import y.io.gml.ParserFactory;
import y.option.OptionHandler;
import y.view.EditMode;
import y.view.Graph2D;
import y.view.PopupMode;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class demonstrates how to create a customized GML
 * format. It is capable of associating attributes to nodes
 * dynamically, encode them in gml and parse them back in.
 * {@link #createNodeDataList } is a factory method, that
 * creates a list of the possible node attributes.
 * The view component is configured to present a popup menu
 * on all visible nodes, which can be used to access the
 * properties from the list. When the graph is saved using the
 * GML format, these properties are encoded in the .graph.node.
 * section as additional attributes. When this file is opened
 * using this demo, the attributes will be parsed back in
 * and associated with the nodes.
 * <br>
 * Additionally, the use
 * of customized PopupModes and OptionHandlers is demonstrated briefly.
 * <br>
 * Note that this demo makes use of the following classes:
 * {@link demo.io.CustomGMLFactory},
 * {@link demo.io.CustomNodeObjectEncoder}, and
 * {@link demo.io.CustomNodeParser}. 
 */
public class CustomGMLDemo extends demo.view.DemoBase
{
  private EncoderFactory backupEncoderFactory;
  private ParserFactory backupParserFactory;

  protected void registerViewModes() {
    Graph2D g = view.getGraph2D();
    List list = createNodeDataList(g);

    backupEncoderFactory = GMLIOHandler.getEncoderFactory();
    backupParserFactory = GMLIOHandler.getParserFactory();
    CustomGMLFactory factory = new CustomGMLFactory(list);
    GMLIOHandler.setEncoderFactory(factory);
    GMLIOHandler.setParserFactory(factory);

    EditMode editMode = new EditMode();
    view.addViewMode( editMode );
    editMode.setPopupMode( new DemoPopupMode( list ) );
  }


  class DemoPopupMode extends PopupMode
  {
    private List nodeDataList;
    
    DemoPopupMode(List nodeDataList)
    {
      this.nodeDataList = nodeDataList;
    }
    
    /** Popup menu for a hit node, this will trigger the display
     * of the OptionHandler for each node
     */
    public JPopupMenu getNodePopup(Node v)
    {
      JPopupMenu pm = new JPopupMenu();
      pm.add(new ShowNodeProperties(v, nodeDataList));
      return pm;
    }
  }
  
  class ShowNodeProperties extends AbstractAction
  {
    private Node n;
    private List nodeDataList;
    private OptionHandler op;
    
    ShowNodeProperties(Node n, List nodeDataList)
    {
      super("Edit Properties...");
      this.n = n;
      this.nodeDataList = nodeDataList;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent)
    {
      if(op == null)
      {
        op = new OptionHandler("Node Properties");
        for (Iterator it = nodeDataList.iterator(); it.hasNext();)
        {
          NodeData data = (NodeData) it.next();
          if (data.getClassType().equals(String.class)){
            Object value = data.getNodeMap().get(n);
            op.addString(data.getPropertyName(), (String) value, 1);
          } else if (data.getClassType().equals(Double.class)){
            double value = data.getNodeMap().getDouble(n);
            op.addDouble(data.getPropertyName(), value);
          } else if (data.getClassType().equals(Float.class)){
            double value = data.getNodeMap().getDouble(n);
            op.addDouble(data.getPropertyName(), value);
          } else if (data.getClassType().equals(Boolean.class)){
            boolean value = data.getNodeMap().getBool(n);
            op.addBool(data.getPropertyName(), value);
          } else if (data.getClassType().equals(Integer.class)){
            int value = data.getNodeMap().getInt(n);
            op.addInt(data.getPropertyName(), value);
          }
        }
      }
      if(op.showEditor())
      {
        for (Iterator it = nodeDataList.iterator(); it.hasNext();)
        {
          NodeData data = (NodeData) it.next();
          if (data.getClassType().equals(String.class)){
            Object value = op.getString(data.getPropertyName());
            data.getNodeMap().set(n, value);
          } else if (data.getClassType().equals(Double.class)){
            Object value = new Double(op.getDouble(data.getPropertyName()));
            data.getNodeMap().set(n, value);
          } else if (data.getClassType().equals(Float.class)){
            Object value = new Double(op.getDouble(data.getPropertyName()));
            data.getNodeMap().set(n, value);
          } else if (data.getClassType().equals(Boolean.class)){
            Object value = new Boolean(op.getBool(data.getPropertyName()));
            data.getNodeMap().set(n, value);
          } else if (data.getClassType().equals(Integer.class)){
            Object value = new Integer(op.getInt(data.getPropertyName()));
            data.getNodeMap().set(n, value);
          }
        }
      }
    }
  }
  
  /** This factory method is used to determine the additional
   * attributes a node can have. The list must consists of
   * {@link NodeData} objects. This is list is used for
   * encoding, parsing and creating an appropriate
   * {@link y.option.OptionHandler}
   * @param forGraph the graph which should be used to create
   * {@link y.base.NodeMap}s, which will store the
   * attributes.
   * @return a list of {@link NodeData} objects
   */  
  protected List createNodeDataList(Graph2D forGraph)
  {
    List list = new ArrayList(5);
    NodeMap nodeMap;
    nodeMap = forGraph.createNodeMap();
    list.add(new CustomGMLDemo.NodeData("stringValue", String.class, nodeMap));
    nodeMap = forGraph.createNodeMap();
    list.add(new CustomGMLDemo.NodeData("doubleValue", Double.class, nodeMap));
    nodeMap = forGraph.createNodeMap();
    list.add(new CustomGMLDemo.NodeData("booleanValue", Boolean.class, nodeMap));
    nodeMap = forGraph.createNodeMap();
    list.add(new CustomGMLDemo.NodeData("intValue", Integer.class, nodeMap));
    nodeMap = forGraph.createNodeMap();
    list.add(new CustomGMLDemo.NodeData("floatValue", Float.class, nodeMap));
    return list;
  }

  public void dispose()
  {
    GMLIOHandler.setEncoderFactory(backupEncoderFactory );
    GMLIOHandler.setParserFactory(backupParserFactory);
  }

  /**
   * Launches this demo.
   */
  public static void main(String args[])
  {
    CustomGMLDemo demo = new CustomGMLDemo();
    demo.start("Custom GML Demo");
  }
  
  /** overwritten to provide this demo with the GMLIOHandler
   */  
  protected javax.swing.Action createLoadAction()
  {
    return new demo.io.CustomGMLDemo.LoadAction();
  }
  
  /** overwritten to provide this demo with the GMLIOHandler
   */  
  protected javax.swing.Action createSaveAction()
  {
    return new demo.io.CustomGMLDemo.SaveAction();
  }
  
  /**
   * Action that saves the current graph to a file in GML format.
   */
  class SaveAction extends javax.swing.AbstractAction
  {
    SaveAction()
    {
      super("Save...");
    }
    
    public void actionPerformed(ActionEvent e)
    {
      JFileChooser chooser = new JFileChooser();
      if(chooser.showSaveDialog(contentPane) == JFileChooser.APPROVE_OPTION)
      {
        String name = chooser.getSelectedFile().toString();
        if(!name.endsWith(".gml")) name = name + ".gml";
        y.io.IOHandler ioh = new GMLIOHandler();
        try
        {
          ioh.write(view.getGraph2D(),name);
        } catch (java.io.IOException ioe)
        {
          y.util.D.show(ioe);
        }
      }
    }
  }
  
  
  /**
   * Action that loads the current graph from a file in GML format.
   */
  class LoadAction extends javax.swing.AbstractAction
  {
    LoadAction()
    {
      super("Load...");
    }
    
    public void actionPerformed(ActionEvent e)
    {
      JFileChooser chooser = new JFileChooser();
      if(chooser.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION)
      {
        String name = chooser.getSelectedFile().toString();
        if(!name.endsWith(".gml")) name = name + ".gml";
        y.io.IOHandler ioh = new GMLIOHandler();
        try
        {
          Graph2D graph = view.getGraph2D();
          graph.clear();
          ioh.read(graph,name);
        } catch (java.io.IOException ioe)
        {
          y.util.D.show(ioe);
        }
        //force redisplay of view contents
        view.updateView();
      }
    }
  }
  
  /** This class is used to store the meta-data for the additional
   * node attributes.
   */  
  public static class NodeData
  {
    
    /** Holds value of property propertyName. */
    private String propertyName;
    
    /** Holds value of property classType. */
    private Class classType;
    
    /** Holds value of property nodeMap. */
    private NodeMap nodeMap;
    
    /** creates a generic node meta data object
     * @param propertyName the name of the property, which will be used for
     * display in the optionhandler, and in the gml file
     * @param classtype the type of the attributes, either
     * <CODE>String.class</CODE>, <CODE>Float.class</CODE>,
     * <CODE>Double.class</CODE>, <CODE>Integer.class</CODE>
     * or <CODE>Boolean.class</CODE>
     * @param nodeMap the nodemap used for the mapping between the nodes
     * and the values
     */    
    public NodeData(String propertyName, Class classtype, NodeMap nodeMap)
    {
      this.propertyName = propertyName;
      this.classType = classtype;
      this.nodeMap = nodeMap;
    }
    
    /** Getter for property propertyName.
     * @return Value of property propertyName.
     */
    public String getPropertyName()
    {
      return this.propertyName;
    }
    
    /** Getter for property classType.
     * @return Value of property classType.
     */
    public Class getClassType()
    {
      return this.classType;
    }
    
    /** Getter for property nodeMap.
     * @return Value of property nodeMap.
     */
    public NodeMap getNodeMap()
    {
      return this.nodeMap;
    }
  }
}
