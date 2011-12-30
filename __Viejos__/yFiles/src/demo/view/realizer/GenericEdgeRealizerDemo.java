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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.net.URL;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import y.base.ListCell;
import y.view.Arrow;
import y.view.Bend;
import y.view.BendCursor;
import y.view.BendList;
import y.view.EdgeRealizer;
import y.view.GenericEdgePainter;
import y.view.GenericEdgeRealizer;
import y.view.LineType;
import y.view.NodeRealizer;
import y.view.PolyLinePathCalculator;
import y.view.Port;
import demo.view.DemoBase;

/**
 * This class demonstrates various usages of the {@link y.view.GenericEdgeRealizer} class.
 */
public class GenericEdgeRealizerDemo extends DemoBase
{
  private static final boolean INITIAL_ANTIALIASING_STATE = true;

  /** Creates the GenericEdgeRealizer demo. */
  public GenericEdgeRealizerDemo()
  {
    super();

    // Take a default GenericEdgeRealizer... 
    GenericEdgeRealizer ger = new GenericEdgeRealizer();
    // ... and make it real flashy. 
    ger.setLineType(LineType.LINE_4);
    ger.setLineColor(Color.green);

    view.setAntialiasedPainting(INITIAL_ANTIALIASING_STATE);

    // Get the factory to register custom styles/configurations. 
    GenericEdgeRealizer.Factory factory = GenericEdgeRealizer.getFactory();

    // Retrieve a map that holds the default GenericEdgeRealizer configuration. 
    // The implementations contained therein can be replaced one by one in order 
    // to create custom configurations... 
    Map implementationsMap = factory.createDefaultConfigurationMap();

    // The edge path is painted 3D-ish and with a drop shadow. 
    implementationsMap.put(GenericEdgeRealizer.Painter.class, new CustomEdgePainter());
    // The path is calculated to be undulating. 
    implementationsMap.put(GenericEdgeRealizer.PathCalculator.class, new MyFunnyPathCalculator());

    // Add the first configuration to the factory. 
    factory.addConfiguration("Undulating", implementationsMap);

    implementationsMap.put(GenericEdgeRealizer.PathCalculator.class, new MyPathCalculator());
    // Add the second configuration to the factory. 
    // NB: It uses the same type of painter as the previous configuration. 
    factory.addConfiguration("QuadCurve", implementationsMap);

    // Special behavior for an otherwise normal poly-line edge path calculator: 
    // first and last segment of the edge path are kept axes-parallel. 
    implementationsMap.put(GenericEdgeRealizer.PathCalculator.class, new PortMoverPathCalculator(new PolyLinePathCalculator()));
    factory.addConfiguration("PolyLineAxesParallel", implementationsMap);

    // Default edge painter implementation.

    implementationsMap = factory.createDefaultConfigurationMap();
    implementationsMap.put(GenericEdgeRealizer.Painter.class, new GenericEdgePainter());
    implementationsMap.put(GenericEdgeRealizer.PathCalculator.class, new MyFunnyPathCalculator());
    // Bends are rendered differently depending on their selection state.
    // - normal rendering: blue ellipse (height is half of width) 
    // - rendering when bend is selected: red ellipse 
    implementationsMap.put(GenericEdgeRealizer.BendPainter.class,
                           new CustomBendPainter(new Ellipse2D.Double(0,0,10,5),new Ellipse2D.Double(0,0,10,10), Color.blue, Color.red));
    factory.addConfiguration("UndulatingCustomBends", implementationsMap);

    implementationsMap = factory.createDefaultConfigurationMap();
    implementationsMap.put(GenericEdgeRealizer.ArrowPainter.class, new CenterArrowPainter());
    implementationsMap.put(GenericEdgeRealizer.PathCalculator.class, new UnclippedPathCalculator());
    factory.addConfiguration("Unclipped", implementationsMap);


    // Initialize the GenericEdgeRealizer instance to one of the types we just 
    // registered with the factory. 
    ger.setConfiguration("Undulating");
    ger.setUserData("This is my own userData object.");
    // Set this edge realizer as the default edge realizer for this graph.
    ger.setTargetArrow(Arrow.STANDARD);
    view.getGraph2D().setDefaultEdgeRealizer(ger);

    loadGraph( "resource/genericEdgeRealizer.gml" );
  }

  /** Creates a toolbar that allows to switch the default edge realizer type. */
  protected JToolBar createToolBar()
  {
    JToolBar retValue;

    retValue = super.createToolBar();

    final JComboBox cb = new JComboBox(new Object[]{"Undulating", "QuadCurve", "PolyLineAxesParallel", "UndulatingCustomBends", "Unclipped"});
    cb.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        ((GenericEdgeRealizer)view.getGraph2D().getDefaultEdgeRealizer()).setConfiguration(cb.getSelectedItem().toString());
      }
    });
    retValue.addSeparator();
    retValue.add(cb);

    final URL iconUrl = getClass().getResource("resource/antialiasing.png");
    final JToggleButton toggleAa = new JToggleButton(new AbstractAction("AA") {
      {
        if (iconUrl != null) {
          putValue(AbstractAction.SMALL_ICON, new ImageIcon(iconUrl));
        }
        putValue(AbstractAction.SHORT_DESCRIPTION, "Toggle Anti-Aliasing");
      }

      public void actionPerformed(ActionEvent e) {
        final boolean newAaState = !view.isAntialiasedPainting();
        view.setAntialiasedPainting(newAaState);
        view.updateView();
      }
    });
    if (iconUrl != null) {
      toggleAa.setText("");
      toggleAa.setMargin(new Insets(0,0,0,0));
    }
    toggleAa.setSelected(INITIAL_ANTIALIASING_STATE);
    retValue.addSeparator();
    retValue.add(toggleAa);
    return retValue;
  }

  /**
   * A custom EdgePainter implementation that draws the edge path 3D-ish and adds 
   * a drop shadow also. 
   */
  static final class CustomEdgePainter extends GenericEdgePainter {
    protected void paintPath(EdgeRealizer context, BendList bends, GeneralPath path, Graphics2D gfx, boolean selected) {
      Stroke s = gfx.getStroke();
      Color oldColor = gfx.getColor();
      if (s instanceof BasicStroke){
        Color c;
        if (!context.isSelected()){
          initializeLine(context, gfx, selected);
          c = gfx.getColor();
          gfx.setColor(new Color(0,0,0,64));
          gfx.translate(4, 4);
          gfx.draw(path);
          gfx.translate(-4, -4);
        } else {
          initializeSelectionLine(context, gfx, selected);
          c = gfx.getColor();
        }
        Color newC = context.isSelected() ? Color.red :c;
        gfx.setColor(new Color(128 + newC.getRed()/ 2, 128 + newC.getGreen()/ 2,128 + newC.getBlue()/ 2));
        gfx.translate(-1, -1);
        gfx.draw(path);
        gfx.setColor(new Color(newC.getRed()/ 2, newC.getGreen()/ 2,newC.getBlue()/ 2));
        gfx.translate(2, 2);
        gfx.draw(path);
        gfx.translate(-1, -1);
        gfx.setColor(c);
        gfx.draw(path);
        gfx.setColor(oldColor);
      } else {
        gfx.draw(path);
      }
    }
  }

  /**
   * A custom PathCalculator implementation that keeps the first and last segment 
   * of an edge path axes-parallel. 
   * To achieve this behavior, the edge's source port and target port are moved 
   * to match any movement of the bend at the opposite of the respective segment. 
   * <p>
   * If the edge path has only a single segment, it is drawn axes-parallel as soon 
   * as the projections of the two nodes overlap on either x-axis or y-axis. 
   */
  static final class PortMoverPathCalculator implements GenericEdgeRealizer.PathCalculator {
    private GenericEdgeRealizer.PathCalculator innerCalculator;

    PortMoverPathCalculator(GenericEdgeRealizer.PathCalculator innerCalculator){
      this.innerCalculator = innerCalculator;
    }

    public byte calculatePath(EdgeRealizer context, BendList bends, GeneralPath path, Point2D sourceIntersectionPointOut,
                              Point2D targetIntersectionPointOut) {
      final Port sp = context.getSourcePort();
      final Port tp = context.getTargetPort();
      final NodeRealizer snr = context.getSourceRealizer();
      final NodeRealizer tnr = context.getTargetRealizer();
      if (bends.size() > 0){
        adjustPort(bends.firstBend(), snr, sp);
        adjustPort((Bend) bends.last(), tnr, tp);
      } else {
        double minx = Math.max(snr.getX() , tnr.getX());
        double maxx = Math.min(snr.getX() + snr.getWidth(), tnr.getX() + tnr.getWidth());
        if (maxx >= minx){
          double pos = (minx + maxx) * 0.5d;
          sp.setOffsetX(pos - snr.getCenterX());
          tp.setOffsetX(pos - tnr.getCenterX());
        }
        double miny = Math.max(snr.getY() , tnr.getY());
        double maxy = Math.min(snr.getY() + snr.getHeight(), tnr.getY() + tnr.getHeight());
        if (maxy >= miny){
          double pos = (miny + maxy) * 0.5d;
          sp.setOffsetY(pos - snr.getCenterY());
          tp.setOffsetY(pos - tnr.getCenterY());
        }
      }
      return innerCalculator.calculatePath(context, bends, path, sourceIntersectionPointOut, targetIntersectionPointOut);
    }

    private void adjustPort(Bend b, NodeRealizer realizer, Port port) {
      double x = b.getX();
      double y = b.getY();
      boolean inXRange = x >= realizer.getX() && x <= realizer.getX() + realizer.getWidth();
      boolean inYRange = y >= realizer.getY() && y <= realizer.getY() + realizer.getHeight();
      if (inXRange && !inYRange){
        port.setOffsetX(x - realizer.getCenterX());
      }
      if (inYRange && ! inXRange){
        port.setOffsetY(y - realizer.getCenterY());
      }
    }
  }

  /**
   * A custom PathCalculator implementation that draws a quad curve edge path. 
   */
  static final class MyPathCalculator extends PolyLinePathCalculator implements GenericEdgeRealizer.PathCalculator {
    private final GeneralPath scratch = new GeneralPath();


    public byte calculatePath(EdgeRealizer context, BendList bends, GeneralPath path, Point2D sourceIntersectionPointOut,
                            Point2D targetIntersectionPointOut) {
      if (bends.size() == 0) {
        return super.calculatePath(context, bends, path, sourceIntersectionPointOut, targetIntersectionPointOut);
      } else {
        final int npoints = bends.size();

        path.reset();
        scratch.reset();

        NodeRealizer nr = context.getSourceRealizer();
        Port pp = context.getSourcePort();
        float lastPointx;
        float lastPointy;
        float secondLastPointx;
        float secondLastPointy;
        scratch.moveTo(lastPointx = (float) pp.getX(nr), lastPointy = (float) pp.getY(nr));

        int index = 0;

        secondLastPointx = lastPointx;
        secondLastPointy = lastPointy;

        BendCursor bc = bends.bends();

        {
          Bend b = bc.bend();
          lastPointx = (float) b.getX();
          lastPointy = (float) b.getY();
          bc.next();
          index++;
        }

        for (; index < npoints; bc.next(), index++) {
          Bend b = bc.bend();
          float nextPointx = (float) b.getX();
          float nextPointy = (float) b.getY();
          {
            final float sx = 0.5f * lastPointx + secondLastPointx * 0.5f;
            final float sy = 0.5f * lastPointy + secondLastPointy * 0.5f;
            scratch.lineTo(sx, sy);
          }
          {
            final float sx = 0.5f * nextPointx + lastPointx * 0.5f;
            final float sy = 0.5f * nextPointy + lastPointy * 0.5f;
            scratch.quadTo(lastPointx, lastPointy, sx, sy);
            secondLastPointx = lastPointx;
            secondLastPointy = lastPointy;
            lastPointx = nextPointx;
            lastPointy = nextPointy;
          }
        }

        nr = context.getTargetRealizer();
        pp = context.getTargetPort();

        {
          float nextPointx = (float) pp.getX(nr);
          float nextPointy = (float) pp.getY(nr);
          {
            final float sx = 0.5f * lastPointx + secondLastPointx * 0.5f;
            final float sy = 0.5f * lastPointy + secondLastPointy * 0.5f;
            scratch.lineTo(sx, sy);
          }
          {
            final float sx = 0.5f * nextPointx + lastPointx * 0.5f;
            final float sy = 0.5f * nextPointy + lastPointy * 0.5f;
            scratch.quadTo(lastPointx, lastPointy, sx, sy);
          }
          scratch.lineTo(nextPointx, nextPointy);
        }
        path.append(scratch.getPathIterator(null, 1.0), false);
      }
      return EdgeRealizer.calculateClippingAndIntersection(context, path, path, sourceIntersectionPointOut, targetIntersectionPointOut);
    }
  }

  /**
   * A custom PathCalculator implementation that draws an undulating edge path. 
   */
  static final class MyFunnyPathCalculator extends PolyLinePathCalculator implements GenericEdgeRealizer.PathCalculator {
    private final GeneralPath scratch = new GeneralPath();

    public byte calculatePath(EdgeRealizer context, BendList bends, GeneralPath path, Point2D sourceIntersectionPointOut,
                            Point2D targetIntersectionPointOut) {
      scratch.reset();

      NodeRealizer nr = context.getSourceRealizer();
      Port pp = context.getSourcePort();
      float lastPointX;
      float lastPointY;
      scratch.moveTo(lastPointX = (float)pp.getX(nr), lastPointY = (float)pp.getY(nr));

      int wobbleCount = 0;
      for(BendCursor bc = bends.bends(); bc.ok(); bc.next())
      {
        Bend b = bc.bend();
        float nextPointX = (float) b.getX();
        float nextPointY = (float) b.getY();
        float dx = nextPointX - lastPointX;
        float dy = nextPointY - lastPointY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0){
          int count = (int) (len / 30) + 1;
          for (int i = 0; i < count; i++){
            final float height = wobbleCount%2 == 0 ? 10 : -10;
            wobbleCount++;
            scratch.quadTo(lastPointX + (i+0.5f)/((float)count) * dx + dy * height / len, lastPointY + (i+0.5f)/((float)count) * dy - dx * height/len, lastPointX + (i+1)/((float)count) * dx, lastPointY + (i+1)/((float)count) * dy);
          }
        } else {
          scratch.lineTo(nextPointX, nextPointY);
        }
        lastPointX = nextPointX;
        lastPointY = nextPointY;
      }

      nr = context.getTargetRealizer();
      pp = context.getTargetPort();

      {
        float nextPointX = (float)pp.getX(nr);
        float nextPointY = (float)pp.getY(nr);
        float dx = nextPointX - lastPointX;
        float dy = nextPointY - lastPointY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0){
          int count = (int) (len / 30) + 1;
          for (int i = 0; i < count; i++){
            final float height = wobbleCount%2 == 0 ? 10 : -10;
            wobbleCount++;
            scratch.quadTo(lastPointX + (i+0.5f)/((float)count) * dx + dy * height / len, lastPointY + (i+0.5f)/((float)count) * dy - dx * height/len, lastPointX + (i+1)/((float)count) * dx, lastPointY + (i+1)/((float)count) * dy);
          }
        } else {
          scratch.lineTo(nextPointX, nextPointY);
        }
      }
      path.reset();
      return EdgeRealizer.calculateClippingAndIntersection(context, scratch, path, sourceIntersectionPointOut, targetIntersectionPointOut);
    }
  }


  /**
   * A custom BendPainter implementation that renders bends differently depending 
   * on the bend's selection state, but also the edge's selection state. 
   */
  static final class CustomBendPainter implements GenericEdgeRealizer.BendPainter
  {
    private RectangularShape shape;
    private RectangularShape selectedShape;
    private Color fillColor;
    private Color selectedFillColor;

    CustomBendPainter(RectangularShape shape, RectangularShape selectedShape, Color fillColor, Color selectedFillColor)
    {
      this.selectedShape = selectedShape;
      this.shape = shape;
      this.fillColor = fillColor;
      this.selectedFillColor = selectedFillColor;
    }

    public void paintBends(EdgeRealizer context, BendList bends, GeneralPath path, Graphics2D gfx, boolean selected) {
      final Color oldColor = gfx.getColor();
      for (BendCursor bendCursor = bends.bends(); bendCursor.ok(); bendCursor.next()){
        Bend b = bendCursor.bend();
        gfx.setColor((selected || b.isSelected()) ? this.selectedFillColor : this.fillColor);
        final double x = b.getX();
        final double y = b.getY();
        RectangularShape shape = selected ? this.selectedShape : this.shape;
        shape.setFrame(x - shape.getWidth()/2, y - shape.getHeight()/2, shape.getWidth(), shape.getHeight());
        gfx.fill(shape);
      }
      gfx.setColor(oldColor);
    }
  }

  /**
   * A simple ArrowPainter implementation that
   * paints the arrow at the center of the segment in the
   * middle of the poly-line control path.
   */
  public static final class CenterArrowPainter implements GenericEdgeRealizer.ArrowPainter {
    public void paintArrows(EdgeRealizer context, BendList bends, GeneralPath path, Graphics2D gfx) {
      Arrow targetArrow = context.getTargetArrow();
      if (targetArrow != null){

        Point2D sourceIntersection = context.getSourceIntersection();
        Point2D targetIntersection = context.getTargetIntersection();

        if (bends.size() > 0) {
          int mid = bends.size() / 2;
          if (mid > 0){
            Bend bend = context.getBend(mid - 1);
            sourceIntersection.setLocation(bend.getX(), bend.getY());
          }
          {
            Bend bend = context.getBend(mid);
            targetIntersection.setLocation(bend.getX(), bend.getY());
          }
        }

        double centerX = (targetIntersection.getX() + sourceIntersection.getX()) * 0.5d;
        double centerY = (targetIntersection.getY() + sourceIntersection.getY()) * 0.5d;
        double dx = (targetIntersection.getX() - sourceIntersection.getX());
        double dy = (targetIntersection.getY() - sourceIntersection.getY());
        double l = Math.sqrt(dx * dx + dy * dy);
        double arrowScaleFactor = context.getArrowScaleFactor();
        if (l > 0){
          targetArrow.paint(gfx, centerX, centerY, arrowScaleFactor * dx / l , arrowScaleFactor * dy / l);
        }
      }
    }
  }

  /**
   * A simple custom PathCalculator implementation that
   * performs no clipping of the ends at the adjacent nodes.
   */
  public static final class UnclippedPathCalculator implements GenericEdgeRealizer.PathCalculator {
    public byte calculatePath(EdgeRealizer context, BendList bends, GeneralPath path, Point2D sourceIntersectionPointOut,
                              Point2D targetIntersectionPointOut) {
      sourceIntersectionPointOut.setLocation(context.getSourcePort().getX(context.getSourceRealizer()), context.getSourcePort().getY(context.getSourceRealizer()));
      targetIntersectionPointOut.setLocation(context.getTargetPort().getX(context.getTargetRealizer()), context.getTargetPort().getY(context.getTargetRealizer()));
      path.reset();
      path.moveTo((float)sourceIntersectionPointOut.getX(), (float)sourceIntersectionPointOut.getY());
      for (ListCell cell = bends.firstCell(); cell != null; cell = cell.succ()){
        Bend b = (Bend) cell.getInfo();
        path.lineTo((float)b.getX(), (float)b.getY());
      }
      path.lineTo((float)targetIntersectionPointOut.getX(), (float)targetIntersectionPointOut.getY());

      return EdgeRealizer.PATH_CLIPPED_AT_SOURCE_AND_TARGET;
    }
  }

  /**
   * Launcher method.
   * Execute this class to see sample instantiations of {@link GenericEdgeRealizer} 
   * in action.
   */
  public static void main(String[] args)
  {
    new GenericEdgeRealizerDemo().start("GenericEdgeRealizer Demo");
  }
}
