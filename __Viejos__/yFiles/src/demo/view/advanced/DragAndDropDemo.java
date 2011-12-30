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

package demo.view.advanced;

import y.util.D;
import y.view.NodeRealizer;
import y.view.ShapeNodeRealizer;
import y.view.Graph2DView;
import y.view.Graph2D;
import y.view.Drawable;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;

import demo.view.DemoBase;

/**
 * Demo that shows how to display drag different {@link NodeRealizer} instances in and from
 * a list and how to drop
 * them on a {@link Graph2DView} using a {@link Drawable} that indicates the drop operation.
 * This demo makes use of the {@link java.awt.dnd} package.
 */
public class DragAndDropDemo extends DemoBase
{
  /** Creates a new instance of DragAndDropDemo */
  public DragAndDropDemo()
  {
    // create two maps, mapping NodeRealizer instances to strings...
    final Map stringToNodeRealizerMap = createStringToRealizerMap();
    final Map inverse = new HashMap();
    for(Iterator it = stringToNodeRealizerMap.entrySet().iterator();it.hasNext();){
      Map.Entry entry = (Map.Entry) it.next();
      inverse.put(entry.getValue(), entry.getKey());
    }


    // create the customized DnD support instance
    final DragAndDropSupport dndSupport = new DragAndDropSupport(inverse.keySet(), view) {
      protected String getTextValue(NodeRealizer selected) {
        return (String) inverse.get(selected);
      }

      protected NodeRealizer createNodeRealizerFromTextValue(String s) {
        return (NodeRealizer) stringToNodeRealizerMap.get(s);
      }
    };

    // get the List UI
    final JList realizerList = dndSupport.getList();
    realizerList.setBackground(Color.lightGray);

    //add the realizer list to the panel
    contentPane.add(new JScrollPane(realizerList),BorderLayout.WEST);
  }

  /**
   * Creates a map that has the names of realizers as keys and node realizer
   * instances as associated values. The realizer instances have different shapes
   * and colors.
   */
  protected Map createStringToRealizerMap()
  {
    Map result = new HashMap();

    Map shapeTypeToStringMap = ShapeNodeRealizer.shapeTypeToStringMap();
    float hueIncrease = 1.0f/shapeTypeToStringMap.size();
    float hue = 0.0f;
    for(Iterator iter = shapeTypeToStringMap.keySet().iterator(); iter.hasNext(); hue += hueIncrease)
    {
      Byte shapeType = (Byte)iter.next();
      ShapeNodeRealizer r = new ShapeNodeRealizer(shapeType.byteValue());
      r.setWidth(100);
      r.setLabelText( (String)shapeTypeToStringMap.get(shapeType) );
      r.setFillColor(new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f)));
      result.put(r.getLabelText(), r);
    }
    return result;
  }

  /**
   * Support class that be used to create a JList that contains NodeRealizers that can be dragged
   * and dropped onto the given Graph2DView object.
   */
  public static abstract class DragAndDropSupport {
    private final JList realizerList;

    public DragAndDropSupport(Collection nodeRealizerList, final Graph2DView view) {
      this( nodeRealizerList.toArray(), view );
    }

    public DragAndDropSupport(Object[] nodeRealizers, final Graph2DView view) {
      // create a nice GUI for displaying NodeRealizers
      realizerList = new JList(nodeRealizers );
      realizerList.setCellRenderer(new NodeRealizerCellRenderer());

      // set the currently selected NodeRealizer as default nodeRealizer
      realizerList.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (realizerList.getSelectedValue() instanceof NodeRealizer) {
            nodeRealizerSelected(view);
          }
        }
      });

      realizerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      realizerList.setSelectedIndex(0);

      // define the realizer list to be the drag source
      // use the string-valued name of the realizer as transferable
      final DragSource dragSource = new DragSource();
      dragSource.createDefaultDragGestureRecognizer(realizerList, DnDConstants.ACTION_MOVE,
          new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent event) {
              Object selected = realizerList.getSelectedValue();
              final String textValue = getTextValue((NodeRealizer) selected);
              if (textValue != null) {
                StringSelection text = new StringSelection(textValue);
                // as the name suggests, starts the dragging
                dragSource.startDrag(event, DragSource.DefaultMoveDrop, text, null);
              }
            }
          });

      // define the graph view to be the drop target. Create a node with the
      // dropped shape to the graph.
      DropTarget dropTarget = new DropTarget(view.getCanvasComponent(), new DropTargetListener() {

        // called by the dnd framework once a drag enters the view
        public void dragEnter(DropTargetDragEvent event) {
          if (checkStringFlavor(event)) {
            event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
          } else {
            event.rejectDrag();
          }
        }

        // inspects the event and tries to create a NodeRealizer from it
        private boolean checkStringFlavor(DropTargetDragEvent event) {
          // we accept only Strings
          DataFlavor[] flavors = event.getCurrentDataFlavors();
          for (int i = 0; i < flavors.length; i++) {
            if (flavors[i] == DataFlavor.stringFlavor) {
              return true;
            }
          }
          return false;
        }

        // called by the dnd framework once a drag ends with a drop operation
        public void drop(DropTargetDropEvent event) {
          try {
            // we accept only Strings
            boolean foundStringFlavor = false;
            DataFlavor[] flavors = event.getCurrentDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
              if (flavors[i] == DataFlavor.stringFlavor) {
                foundStringFlavor = true;
                break;
              }
            }
            if (foundStringFlavor) {
              event.acceptDrop(event.getDropAction());
            } else {
              event.rejectDrop();
              return;
            }
          } catch (RuntimeException rex) {
            event.rejectDrop();
            D.show(rex);
          }
          try {
            Transferable transferable = event.getTransferable();
            String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            Point p = event.getLocation();
            NodeRealizer r = createNodeRealizerFromTextValue(s);
            if (r != null) {
              // found a suitable realizer using the given name
              final double worldCoordX = view.toWorldCoordX(p.x);
              final double worldCoordY = view.toWorldCoordY(p.y);
              event.dropComplete(dropRealizer(view, r, worldCoordX, worldCoordY));
            } else {
              // no suitable realizer
              event.dropComplete(false);
            }
          } catch (IOException ioe) {
            // should not happen
            event.dropComplete(false);
            D.show(ioe);
          } catch (UnsupportedFlavorException ufe) {
            // should never happen
            event.dropComplete(false);
            D.show(ufe);
          } catch (RuntimeException x) {
            event.dropComplete(false);
            throw x;
          }
        }

        // called by the dnd framework when a drag leaves the view
        public void dragExit(DropTargetEvent dte) {
        }

        // called by the dnd framework when the drag action changes
        public void dropActionChanged(DropTargetDragEvent dtde) {
          dragEnter(dtde);
        }

        // called by the dnd framework when the drag hovers over the view
        public void dragOver(DropTargetDragEvent dtde) {
          dragEnter(dtde);
        }
      });
    }

    /**
     * Callback method that returns a String representation for use during the DnD operation
     * of a NodeRealizer instance.
     * @param selected the selected NodeRealizer
     * @return a String representation or <code>null</code>
     */
    protected abstract String getTextValue(NodeRealizer selected);

    /**
     * Callback method that converts the given String representation to a valid NodeRealizer
     * instance
     * @param s the String transferable
     * @return a NodeRealizer instance or <code>null</code>
     */
    protected abstract NodeRealizer createNodeRealizerFromTextValue(String s);

    /**
     * The code that performs the actual drop operation.
     * Note that this method should return quickly since it may block the OS's drag-and-drop system.
     * @return <code>true</code> iff the drop was successfull.
     */
    protected boolean dropRealizer( Graph2DView view, NodeRealizer r, double worldCoordX, double worldCoordY ) {
      final Graph2D graph = view.getGraph2D();
      r = r.createCopy();

      if ( view.getGridMode() ) {
        double gridSize = view.getGridResolution();
        double x = Math.floor( worldCoordX / gridSize + 0.5 ) * gridSize;
        double y = Math.floor( worldCoordY / gridSize + 0.5 ) * gridSize;

        r.setCenter( x, y );
      } else {
        r.setCenter( worldCoordX, worldCoordY );
      }
      graph.createNode( r );
      view.updateView();
      return true;
    }

    /**
     * Callback method that is triggered whenever the selection changes in the JList.
     * This method sets the given NodeRealizer as the view's graph default node realizer.
     */
    protected void nodeRealizerSelected(Graph2DView view) {
      view.getGraph2D().setDefaultNodeRealizer((NodeRealizer) realizerList.getSelectedValue());
    }

    /**
     * Return the JList that has been configured by this support class.
     */
    public JList getList(){
      return realizerList;
    }
  }

  public static final class NodeRealizerDrawable implements Drawable {

    private final NodeRealizer nodeRealizer;

    public NodeRealizerDrawable(NodeRealizer nodeRealizer) {
      this.nodeRealizer = nodeRealizer;
    }

    public NodeRealizer getNodeRealizer() {
      return nodeRealizer;
    }

    public void paint(Graphics2D g) {
      nodeRealizer.paint(g);
    }

    public Rectangle getBounds() {
      return nodeRealizer.getBoundingBox().getBounds();
    }
  }

  /**
   * ListCellRenderer implementation that handles NodeRealizer instances.
   * Used internally by {@link DragAndDropSupport} and publicly available for others.
   */
  public static final class NodeRealizerCellRenderer implements ListCellRenderer {

    private DefaultListCellRenderer renderer = new DefaultListCellRenderer();
    private NodeRealizerIcon icon = new NodeRealizerIcon();

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label = (JLabel) renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      icon.setRealizer((NodeRealizer) value);
      label.setText("");
      label.setIcon(icon);
      return label;
    }

    /**
     * Icon implementation that renders a NodeRealizer
     */
    public static final class NodeRealizerIcon implements Icon {
      private static final int inset = 10;
      private NodeRealizer realizer;

      public void setRealizer(NodeRealizer realizer) {
        this.realizer = realizer;
      }

      public int getIconWidth() {
        return (int) (realizer.getWidth() + inset);
      }

      public int getIconHeight() {
        return (int) (realizer.getHeight() + inset);
      }

      public void paintIcon(Component c, Graphics g, int x, int y) {
        realizer.setLocation(x + inset * 0.5d, y + inset * 0.5d);
        g = g.create();
        try {
          final Graphics2D gfx = (Graphics2D) g;
          gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          realizer.paint(gfx);
        } finally {
          g.dispose();
        }
      }
    }
  }

  /**
   * Instantiates and starts this demo.
   */
  public static void main(String[] args)
  {
    initLnF();
    DragAndDropDemo demo = new DragAndDropDemo();
    demo.start("Drag and Drop Demo");
  }
}
