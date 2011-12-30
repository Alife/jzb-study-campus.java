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

import demo.view.DemoBase;
import y.base.Edge;
import y.base.Node;
import y.view.ArcEdgeRealizer;
import y.view.Arrow;
import y.view.AutoRotationSliderEdgeLabelModel;
import y.view.BridgeCalculator;
import y.view.DefaultGraph2DRenderer;
import y.view.Drawable;
import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.ImageNodeRealizer;
import y.view.InterfacePort;
import y.view.LineType;
import y.view.NodeLabel;
import y.view.NodeRealizer;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;
import y.view.YLabel;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Set;

/**
 * Demonstrates visual features and editor behaviour
 * <ol>
 *
 * <li>EdgeLabels that display icons and text
 *
 * <li>Rotated Labels
 *
 * <li>Auto rotating EdgeLabels
 *
 * <li>Transparent colors
 *
 * <li>Gradients
 *
 * <li>Bridges for crossing PolyLine Edges
 *
 * <li>InterfacePorts that display icons. (A port defines the logical
 * and visual endpoint of and edge path)
 *
 * <li>In edit mode you can reposition an edge label by pressing
 * on it with the left mouse button and then by dragging the label around.
 * Possible label candidate boxes will appear along the edge. If you release
 * the mouse button again, the label will snap to the closest of the candidate boxes.
 *
 * <li>In edit mode you can interactively change the offsets of edge ports.
 * Select the edge that should have different ports. A little black dot will
 * appear at the point where the port has it's logical location.
 * You can drag the black dot around. By doing so, port candidate boxes
 * will appear around the connected node. If you release the mouse again the 
 * port will snap to the closest available port candidate position.
 *
 * <li>In edit mode you can create an edge that has non-zero port offsets by
 * starting edge creation with the shift key pressed down. The point where you press
 * will become the source port location of the edge. If you have the shift key down
 * when you finish edge creation (by releasing the mouse over a node) 
 * that the release point will become the offset of the target port of the edge.
 *
 * </ol>
 */

public class VisualFeatureDemo extends DemoBase
{
  public VisualFeatureDemo()
  {
    //setup default edge realizer

    final Graph2D graph = view.getGraph2D();
    EdgeRealizer er = graph.getDefaultEdgeRealizer();

    // show bridges
    ((DefaultGraph2DRenderer) view.getGraph2DRenderer()).setBridgeCalculator(new BridgeCalculator());

    //setup source arrow drawable
    Drawable drawable = new Drawable() {
      public void paint(Graphics2D g) {
        Color color = g.getColor();
        g.setColor(Color.yellow);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(-20, -10, 20, 20);
        g.fill(ellipse);
        g.setColor(Color.orange);
        g.draw(ellipse);
        g.setColor(Color.black);
        g.drawString("A", -13, 5);
        g.setColor(color);
      }

      public Rectangle getBounds() {
        return new Rectangle(-20, -20, 20, 20);
      }
    };


    // and register it
    er.setSourceArrow(Arrow.addCustomArrow("coolArrow", drawable, 20, 3));

    //setup target arrow type
    er.setTargetArrow(Arrow.STANDARD);

    // choose smooth bends
    ((PolyLineEdgeRealizer) er).setSmoothedBends(true);

    // choose a thicker line
    er.setLineType(LineType.LINE_2);

    Icon icon;
    //setup edge label
    EdgeLabel label = er.getLabel();
    label.setText("What should I say?");
    icon = new ImageIcon(getClass().getResource("resource/about24.gif"));
    label.setIcon(icon);

    //setup visual source port 
    icon = new ImageIcon(getClass().getResource("resource/info24.gif"));
    InterfacePort p = new InterfacePort();
    p.setIcon(icon);
    er.setSourcePort(p);

    //setup visual target port
    icon = new ImageIcon(getClass().getResource("resource/home16.gif"));
    p = new InterfacePort();
    p.setIcon(icon);
    er.setTargetPort(p);

    //setup default node
    ShapeNodeRealizer nodeRealizer = new ShapeNodeRealizer();
    nodeRealizer.setShapeType(ShapeNodeRealizer.OCTAGON);
    // configure a drop shadow
    nodeRealizer.setDropShadowColor(new Color(0,0,0,64));
    nodeRealizer.setDropShadowOffsetX((byte)5);
    nodeRealizer.setDropShadowOffsetY((byte)5);
    // and default size
    nodeRealizer.setSize(80,80);
    graph.setDefaultNodeRealizer(nodeRealizer);

    // display an ImageNodeRealizer
    ImageNodeRealizer imageNodeRealizer = new ImageNodeRealizer();
    imageNodeRealizer.setImageURL(getClass().getResource("/demo/view/ports/resource/4pkt.gif"));
    // clip on the visual bounds
    imageNodeRealizer.setAlphaImageUsed(true);
    imageNodeRealizer.setCenter(300, 340);
    imageNodeRealizer.setToImageSize();

    // create sample nodes
    final Node node1 = graph.createNode(imageNodeRealizer);
    final Node node2 = graph.createNode(50, 50);

    // and a sample edge
    final Edge edge = graph.createEdge(node1, node2);
    graph.getRealizer(edge).appendBend(200, 70);

    // reconfigure the default NodeRealizer
    nodeRealizer.setShapeType(ShapeNodeRealizer.ELLIPSE);
    final NodeLabel nodeLabel = nodeRealizer.createNodeLabel();
    nodeLabel.setText("<html>Hello <b>world</b>!</html>");
    nodeRealizer.addLabel(nodeLabel);
    nodeLabel.setModel(NodeLabel.SANDWICH);
    nodeLabel.setPosition(NodeLabel.S);
    final Node node3 = graph.createNode(350, 50);
    nodeRealizer.setFillColor(Color.yellow);
    final Node node4 = graph.createNode(50, 200);

    final NodeRealizer node4Realizer = graph.getRealizer(node4);
    node4Realizer.setSize(100, 100);
    node4Realizer.setFillColor2(Color.red);
    final NodeLabel node4Label = node4Realizer.getLabel();
    node4Label.setText("Whoohoo! Transparency! and automatically cropped text for custom label size!.");

    Set configurations = NodeLabel.getFactory().getAvailableConfigurations();
    // set a custom configuration for the label
    if (configurations.contains("CroppingLabel")){
      node4Label.setConfiguration("CroppingLabel");
      node4Label.setAutoSizePolicy(YLabel.AUTOSIZE_NONE);
      node4Label.setContentSize(85, 60);
    }

    node4Label.setRotationAngle(45);
    node4Label.setBackgroundColor(new Color(255, 255, 255, 128));
    node4Label.setLineColor(Color.GRAY);

    // add an edge
    final ArcEdgeRealizer arcEdgeRealizer = new ArcEdgeRealizer();
    arcEdgeRealizer.setTargetArrow(Arrow.STANDARD);
    arcEdgeRealizer.setLineType(LineType.DOTTED_2);
    graph.createEdge(node3, node4, arcEdgeRealizer);

    // add another edge
    final PolyLineEdgeRealizer polyLineEdgeRealizer = new PolyLineEdgeRealizer();
    // set a custom target arrow - clip the edge at the target side by 10 pixels
    polyLineEdgeRealizer.setTargetArrow(Arrow.addCustomArrow("offsetArrow", Arrow.WHITE_DELTA, 10));
    polyLineEdgeRealizer.setLineType(LineType.DASHED_1);
    // add an auto rotating label
    final EdgeLabel polyLineLabel = polyLineEdgeRealizer.getLabel();
    final AutoRotationSliderEdgeLabelModel labelModel = new AutoRotationSliderEdgeLabelModel();
    labelModel.setDistance(-15);
    polyLineLabel.setText("PolyLine Label");

    polyLineLabel.setLabelModel(labelModel);
    polyLineLabel.setModelParameter(labelModel.getDefaultParameter());

    graph.createEdge(node4, node3, polyLineEdgeRealizer);
  }

  public static void main(String args[])
  {
    initLnF();
    VisualFeatureDemo demo = new VisualFeatureDemo();
    demo.start("Visual  Feature Demo");
  }
}


      
