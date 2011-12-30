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
import y.view.AbstractCustomHotSpotPainter;
import y.view.AbstractCustomNodePainter;
import y.view.GenericNodeRealizer;
import y.view.NodeRealizer;
import y.view.SimpleUserDataHandler;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.Map;

/**
 * This class demonstrates various usages of the {@link y.view.GenericNodeRealizer} class.
 */
public class GenericNodeRealizerDemo extends DemoBase
{
  private static final boolean INITIAL_ANTIALIASING_STATE = true;

  /** Creates the GenericNodeRealizer demo. */
  public GenericNodeRealizerDemo()
  {
    super();

    view.setAntialiasedPainting(INITIAL_ANTIALIASING_STATE);
    // Take a default GenericNodeRealizer.
    GenericNodeRealizer gnr = new GenericNodeRealizer();

    // Get the factory to register custom styles/configurations.
    GenericNodeRealizer.Factory factory = GenericNodeRealizer.getFactory();

    // Retrieve a map that holds the default GenericNodeRealizer configuration.
    // The implementations contained therein can be replaced one by one in order
    // to create custom configurations...
    Map implementationsMap = factory.createDefaultConfigurationMap();

    // The node is painted as an ellipse. Both painting and containment testing
    // is done by this custom painter.
    RectangularShapePainter painter = new RectangularShapePainter(new Ellipse2D.Double());
    // Register both the painter and the contains test.
    implementationsMap.put(GenericNodeRealizer.Painter.class, new ShadowPainter(painter));
    implementationsMap.put(GenericNodeRealizer.ContainsTest.class, painter);

    // The node has four resize knobs, one at each of the node's corners. Both painting
    // and hit-testing is done by this custom hot spot painter.
    CustomHotSpotPainter chsp = new CustomHotSpotPainter(165, new Ellipse2D.Double(), null);
    // Register both the painter and the hit test.
    implementationsMap.put(GenericNodeRealizer.HotSpotPainter.class, chsp);
    implementationsMap.put(GenericNodeRealizer.HotSpotHitTest.class, chsp);

    // User-defined data objects that implement both the Cloneable and Serializable
    // interfaces are taken care of (when serializing/deserializing the realizer).
    implementationsMap.put(GenericNodeRealizer.UserDataHandler.class, new SimpleUserDataHandler(SimpleUserDataHandler.REFERENCE_ON_FAILURE));

    // Add the first configuration to the factory.
    factory.addConfiguration("Ellipse", implementationsMap);

    // The node is painted as a rounded rectangle.
    painter = new RectangularShapePainter(new RoundRectangle2D.Double(50,50,50,50,15, 15));
    implementationsMap.put(GenericNodeRealizer.Painter.class, new ShadowPainter(painter));
    implementationsMap.put(GenericNodeRealizer.ContainsTest.class, painter);
    // The node has the maximum of eight resize knobs, one at each of the node's
    // corners and also one at the middle of each side.
    chsp = new CustomHotSpotPainter(255, new Ellipse2D.Double(), Color.red);
    implementationsMap.put(GenericNodeRealizer.HotSpotPainter.class, chsp);
    implementationsMap.put(GenericNodeRealizer.HotSpotHitTest.class, chsp);
    // Add the second configuration to the factory.
    // NB: It uses the same type of handler for user-defined data objects as the
    // previous configuration.
    factory.addConfiguration("RoundRectangle", implementationsMap);

    GeneralPath gp = new GeneralPath();
    gp.moveTo(1.0f, 0.5f);
    gp.lineTo(0.0f, 1.0f);
    gp.quadTo(0.0f, 0.5f, 0.3f, 0.5f);
    gp.quadTo(0.0f, 0.5f, 0.0f, 0.0f);
    gp.closePath();

    GeneralPathPainter pp = new GeneralPathPainter(gp);
    implementationsMap.put(GenericNodeRealizer.Painter.class, new ShadowPainter(pp));
    implementationsMap.put(GenericNodeRealizer.ContainsTest.class, pp);
    // Add the third configuration to the factory.
    // NB: It uses the same type of hot spot painter/containment tester as the previous
    // configuration.
    factory.addConfiguration("Butterfly", implementationsMap);

    // Initialize the GenericNodeRealizer instance to one of the types we just
    // registered with the factory.
    gnr.setConfiguration("Ellipse");
    gnr.setUserData("This is my own userData object.");
    // Set this node realizer as the default node realizer for this graph.
    view.getGraph2D().setDefaultNodeRealizer(gnr);
//    view.setAntialiasedPainting( false );
    loadGraph( "resource/genericNodeRealizer.gml" );
  }

  /**
   * Not very efficient but beautiful implementation of a Painter, that
   * wraps a given painter and paints a wonderfully smooth drop shadow below it.
   */
  public static final class ShadowPainter implements GenericNodeRealizer.Painter {
    private final GenericNodeRealizer.Painter painterDelegate;
    private BufferedImage shadowImage;
    private BufferedImage convolvedShadowImage;
    private final ConvolveOp convolveOp1;
    private final ConvolveOp convolveOp2;

    private final Color TRANSPARENT_COLOR = new Color(255,255,255,0);
    private final AlphaComposite SHADOW_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.3f);
    private final int SHADOW_OFFSET = 3;
    private final int KERNEL_SIZE = 9;
    private final double GAUSSIAN_THETA = 2.0;


    /**
     * Creates a new wrapping Painter implementation wrapping the given one.
     * @param painterDelegate the painter to wrap
     */
    public ShadowPainter(GenericNodeRealizer.Painter painterDelegate) {
      this.painterDelegate = painterDelegate;
      // create the smoothing kernels and convolutions
      Kernel kernel1 = new Kernel(1, KERNEL_SIZE, gaussian1D(GAUSSIAN_THETA, KERNEL_SIZE));
      convolveOp1 = new ConvolveOp(kernel1,ConvolveOp.EDGE_NO_OP, null);
      Kernel kernel2 = new Kernel(KERNEL_SIZE, 1, gaussian1D(GAUSSIAN_THETA, KERNEL_SIZE));
      convolveOp2 = new ConvolveOp(kernel2,ConvolveOp.EDGE_NO_OP, null);
    }

    /**
     * Perform the painting and calculate the shadow.
     */
    public void paint(NodeRealizer context, Graphics2D graphics) {
      final int enlargement = 20;
      final int width = (int) (context.getWidth() + enlargement);
      final int height = (int) (context.getHeight() + enlargement);
      if (shadowImage == null || shadowImage.getWidth() < width || shadowImage.getHeight() < height) {
        shadowImage = graphics.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        convolvedShadowImage = graphics.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
      }
      Graphics2D shadowGraphics = shadowImage.createGraphics();
      try {
        final int workWidth = Math.min(width + 1 + KERNEL_SIZE / 2, shadowImage.getWidth());
        final int workHeight = Math.min(height + 1 + KERNEL_SIZE / 2, shadowImage.getHeight());
        shadowGraphics.setBackground(TRANSPARENT_COLOR);
        shadowGraphics.clearRect(0,0,workWidth, workHeight);
        int offsetX = (int) (context.getX() - enlargement / 2);
        int offsetY = (int) (context.getY() - enlargement / 2);
        shadowGraphics.translate(-offsetX, -offsetY);
        shadowGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final boolean wasSelected = context.isSelected();
        context.setSelected(false);
        painterDelegate.paint(context, shadowGraphics);
        context.setSelected(wasSelected);
        shadowGraphics.translate(offsetX, offsetY);
        shadowGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        shadowGraphics.setComposite(SHADOW_COMPOSITE);
        shadowGraphics.setColor(Color.black);
        shadowGraphics.fillRect(0, 0, workWidth, workHeight);
        convolveOp2.filter(shadowImage, convolvedShadowImage);
        convolveOp1.filter(convolvedShadowImage, shadowImage);
        graphics.drawImage(shadowImage, offsetX + SHADOW_OFFSET, offsetY + SHADOW_OFFSET, offsetX + SHADOW_OFFSET + width, offsetY + SHADOW_OFFSET + height, 0,0, width, height, null);
      } finally {
        shadowGraphics.dispose();
      }
      painterDelegate.paint(context, graphics);
    }

    /**
     * No shadow for sloppy painting.
     */
    public void paintSloppy(NodeRealizer context, Graphics2D graphics) {
      painterDelegate.paintSloppy(context, graphics);
    }

    // helper method to calculate a gaussian 1D smoothing kernel
    static float[] gaussian1D(double theta, int size)
    {
      float [] kernel = new float[size];
      float sum = 0;
      for (int i=0; i<size; ++i)
      {
        float val = (float)gaussianDiscrete1D(theta,i-(size*.5d));
        kernel[i]= val;
        sum += val;
      }
      for(int i=0; i<kernel.length; ++i)
      {
        kernel[i] /= sum;
      }
      return kernel;
    }

    // helper method for above method
    static final double gaussianDiscrete1D(double theta, double x)
    {
      final double tmp = 1.0/(Math.sqrt(2.0*Math.PI)*theta);
      double g = 0;
      for(double xSubPixel = x - 0.5; xSubPixel < x + 0.6; xSubPixel += 0.1)
      {
        g = g + tmp * Math.pow(Math.E, -xSubPixel*xSubPixel/(2*theta*theta));
      }
      g = g/11;
      return g;
    }
  }

  /** Creates a toolbar that allows to switch the default node realizer type. */
  protected JToolBar createToolBar()
  {
    JToolBar retValue;

    retValue = super.createToolBar();
    final JComboBox cb = new JComboBox(new Object[]{"Ellipse", "RoundRectangle", "Butterfly"});
    retValue.add(cb);
    cb.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        ((GenericNodeRealizer)view.getGraph2D().getDefaultNodeRealizer()).setConfiguration(cb.getSelectedItem().toString());
      }
    });
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
   * A custom HotSpotPainter implementation that uses the given shape and color
   * to paint the resize knobs, a.k.a. hot spots.
   * If the given color is <code>null</code>, then the node's fill color is used
   * instead.
   * <p>
   * Note that his painter also provides support for hit-testing the resize knobs.
   */
  static final class CustomHotSpotPainter extends AbstractCustomHotSpotPainter
  {
    private RectangularShape shape;
    private Color color;

    CustomHotSpotPainter(int mask, RectangularShape shape, Color color)
    {
      super(mask);
      this.shape = shape;
      this.color = color;
    }

    protected void initGraphics(NodeRealizer context, Graphics2D g)
    {
      super.initGraphics(context, g);
      if (color == null)
      {
        Color fc = context.getFillColor();
        if (fc != null)
        {
          g.setColor(fc);
        }
      }
      else
      {
        g.setColor(color);
      }
    }

    protected void paint(byte hotSpot, double centerX, double centerY, Graphics2D graphics)
    {
      shape.setFrame(centerX - 2, centerY - 2, 5, 5);
      graphics.fill(shape);
    }

    protected boolean isHit(byte hotSpot, double centerX, double centerY, double testX, double testY)
    {
      return Math.abs(testX - centerX) < 3 && Math.abs(testY - centerY) < 3;
    }
  }

  /**
   * A custom Painter and ContainsTest implementation that can be used with any
   * kind of <code>RectangularShape</code>.
   */
  public static final class RectangularShapePainter extends AbstractCustomNodePainter implements GenericNodeRealizer.ContainsTest
  {
    private RectangularShape shape;

    public RectangularShapePainter(RectangularShape shape)
    {
      this.shape = shape;
    }

    /** Overrides the default fill color. */
    protected Color getFillColor(NodeRealizer context, boolean selected)
    {
      if (selected)
      {
        return Color.red;
      }
      else
      {
        return super.getFillColor(context, selected);
      }
    }

    protected void paintNode(NodeRealizer context, Graphics2D graphics, boolean sloppy)
    {
      shape.setFrame(context.getX(), context.getY(), context.getWidth(), context.getHeight());
      if (initializeFill(context, graphics))
      {
        graphics.fill(shape);
      }
      if (initializeLine(context, graphics))
      {
        graphics.draw(shape);
      }
    }

    public boolean contains(NodeRealizer context, double x, double y)
    {
      shape.setFrame(context.getX(), context.getY(), context.getWidth(), context.getHeight());
      return shape.contains(x, y);
    }
  }

  /**
   * A custom Painter and ContainsTest implementation that can be used with any
   * kind of <code>GeneralPath</code>.
   */
  public static final class GeneralPathPainter extends AbstractCustomNodePainter implements GenericNodeRealizer.ContainsTest
  {
    private GeneralPath path;
    private AffineTransform aft;

    public GeneralPathPainter(GeneralPath path)
    {
      this.path = path;
      this.aft = AffineTransform.getScaleInstance(1.0d, 1.0d);
    }

    protected void paintNode(NodeRealizer context, Graphics2D graphics, boolean sloppy)
    {
      aft.setToIdentity();
      aft.translate(context.getX(), context.getY());
      aft.scale(context.getWidth(), context.getHeight());
      Shape shape = path.createTransformedShape(aft);
      if (initializeFill(context, graphics))
      {
        graphics.fill(shape);
      }
      if (initializeLine(context, graphics))
      {
        graphics.draw(shape);
      }
    }

    /** Overrides the default fill color to be the same as the unselected fill color. */
    protected Color getFillColor(NodeRealizer context, boolean selected)
    {
      return super.getFillColor(context, false);
    }

    public boolean contains(NodeRealizer context, double x, double y)
    {
      return path.contains((x - context.getX()) / context.getWidth(), (y - context.getY()) / context.getHeight());
    }
  }

  /**
   * Launcher method.
   * Execute this class to see sample instantiations of {@link GenericNodeRealizer}
   * in action.
   */
  public static void main(String[] args)
  {
    new GenericNodeRealizerDemo().start("GenericNodeRealizer Demo");
  }
}