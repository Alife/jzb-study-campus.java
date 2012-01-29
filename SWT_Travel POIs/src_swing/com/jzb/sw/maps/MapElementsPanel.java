/**
 * 
 */
package com.jzb.sw.maps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.tuple.TableEdge;
import prefuse.data.tuple.TableTuple;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.Force;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableEdgeItem;

/**
 * @author n63636
 * 
 */
@SuppressWarnings({ "serial", "synthetic-access" })
public class MapElementsPanel extends JPanel {

    // -------------------------------------------------------------------------------------
    private class MyControl extends ControlAdapter {

        public void itemClicked(VisualItem item, MouseEvent e) {

            if (!SwingUtilities.isLeftMouseButton(e))
                return;

            switch (m_toolOption) {
                case OPT_MODIFY:
                    String name = item.getString("name");
                    if (!name.startsWith("*")) {
                        item.setString("name", "* " + name);
                        m_vis.repaint();
                        return;
                    }
                case OPT_DEL:
                    if (item instanceof TableEdgeItem)
                        m_graph.removeEdge(item.getRow());
                    else
                        m_graph.removeNode(item.getRow());
                    m_vis.repaint();
                    return;
                case OPT_CONNECT:
                    if (m_originItem == null) {
                        if (item.getString("type").equalsIgnoreCase("category")) {
                            m_originItem = item;
                        }
                    } else {
                        _linkCatToElement(m_originItem.getString("id"), item.getString("id"));
                        m_originItem = null;
                        m_vis.repaint();
                        return;
                    }
                    break;
                default:
                    return;
            }

            if (m_lastSelectedItem != null) {
                m_lastSelectedItem.setBoolean("selected", false);
                m_lastSelectedItem.setInt(VisualItem.TEXTCOLOR, ColorLib.gray(0));
            }

            m_lastSelectedItem = item;
            m_lastSelectedItem.setBoolean("selected", true);
            m_lastSelectedItem.setInt(VisualItem.TEXTCOLOR, ColorLib.rgb(255, 0, 0));
            m_vis.repaint();
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                String type;
                switch (m_toolOption) {
                    case OPT_ADD_POI:
                        type = "point";
                        break;
                    case OPT_ADD_CAT:
                        type = "category";
                        break;
                    default:
                        return;
                }
                String response = JOptionPane.showInputDialog(null, "What is the element's name?", "Name:", JOptionPane.QUESTION_MESSAGE);
                if (response != null) {
                    _createElement(e.getX(), e.getY(), type, "id", response);
                    m_vis.repaint();
                }
            }
        }

    }

    // -------------------------------------------------------------------------------------
    private static enum TB_OPTION {
        OPT_ADD_CAT, OPT_ADD_POI, OPT_CONNECT, OPT_DEL, OPT_MODIFY, OPT_NOTHING, OPT_PAN
    }

    private ButtonGroup   m_buttonGroup      = new ButtonGroup();
    private Graph         m_graph;
    private VisualItem    m_lastSelectedItem = null;
    private VisualItem    m_originItem       = null;

    private JToolBar      m_toolBar;
    private TB_OPTION     m_toolOption       = TB_OPTION.OPT_NOTHING;

    private Visualization m_vis;
    private VisualGraph   m_visualGraph;

    // -------------------------------------------------------------------------------------
    /**
     * Create the panel
     */
    public MapElementsPanel() {
        super();
        createContents();
        _createVisualization();
    }

    // -------------------------------------------------------------------------------------
    private Node _createElement(int x, int y, String type, String id, String name) {

        Node n = m_graph.addNode();
        n.setString("type", type);
        n.setString("id", id);
        n.setString("name", name);
        n.setBoolean("selected", false);
        VisualItem f = (VisualItem) m_visualGraph.getNode(n.getRow());

        f.setInt(VisualItem.STROKECOLOR, ColorLib.gray(0));
        f.setInt(VisualItem.TEXTCOLOR, ColorLib.gray(0));
        if (type.equalsIgnoreCase("category")) {
            f.setInt(VisualItem.FILLCOLOR, ColorLib.rgb(255, 180, 180));
        } else {
            f.setInt(VisualItem.FILLCOLOR, ColorLib.rgb(190, 190, 255));
        }

        f.setX(x);
        f.setEndX(x);
        f.setY(y);
        f.setEndY(y);

        if (type.equalsIgnoreCase("category") && !id.equals("id-root")) {
            _linkCatToElement("id-root", id);
        }

        return n;
    }

    // -------------------------------------------------------------------------------------
    private Node _createElement(String type, String id, String name) {
        return _createElement(100, 100, type, id, name);
    }

    // -------------------------------------------------------------------------------------
    private void _createVisualization() {

        // -------------------------------------------------------------------------------------
        m_graph = new Graph(true);
        // m_graph = new Tree();
        m_vis = new Visualization();
        m_visualGraph = m_vis.addGraph("graph", m_graph);

        // -------------------------------------------------------------------------------------
        m_graph.getNodeTable().addColumn("type", String.class);
        m_graph.getNodeTable().addColumn("id", String.class);
        m_graph.getNodeTable().addColumn("name", String.class);
        m_graph.getNodeTable().addColumn("selected", boolean.class);

        // -------------------------------------------------------------------------------------
        // draw the "name" label for NodeItems
        LabelRenderer r = new LabelRenderer("name");
        r.setRoundedCorner(8, 8); // round the corners
        EdgeRenderer er = new EdgeRenderer(Constants.EDGE_TYPE_CURVE, Constants.EDGE_ARROW_FORWARD);
        m_vis.setRendererFactory(new DefaultRendererFactory(r, er));

        // -------------------------------------------------------------------------------------
        // create an action list with an animated layout
        // the INFINITY parameter tells the action list to run indefinitely
        ActionList layout = new ActionList(2000);
        ForceDirectedLayout fdl = new ForceDirectedLayout("graph");
        fdl.getForceSimulator().addForce(new SpringForce(SpringForce.DEFAULT_SPRING_COEFF, 2 * SpringForce.DEFAULT_SPRING_LENGTH));
        Force fc[] = fdl.getForceSimulator().getForces();
        System.out.println(fc);
        layout.add(fdl);
        layout.add(new RepaintAction());

        // -------------------------------------------------------------------------------------
        // add the actions to the visualization
        m_vis.putAction("layout", layout);

        // -------------------------------------------------------------------------------------
        // create a new Display that pull from our Visualization
        Display display = new Display(m_vis);
        display.setSize(2000, 5000); // set display size
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl(Control.LEFT_MOUSE_BUTTON, false, Control.CTRL_MASK));
        // display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        // display.addControlListener(new ZoomToFitControl());
        // display.addControlListener(new NeighborHighlightControl());

        display.addControlListener(new MyControl());

        // -------------------------------------------------------------------------------------
        m_vis.run("layout");

        // -------------------------------------------------------------------------------------
        add(display, BorderLayout.CENTER);

    }

    // -------------------------------------------------------------------------------------
    private void _linkCatToElement(String idCat, String idElement) {

        TableTuple cat = null, element = null;

        Iterator iter = m_graph.getNodes().tuples();
        while (iter.hasNext()) {
            TableTuple tt = (TableTuple) iter.next();
            if (tt.getString("id").equals(idCat)) {
                cat = tt;
            } else if (tt.getString("id").equals(idElement)) {
                element = tt;
            }
            if (cat != null && element != null) {
                break;
            }
        }

        if (cat != null && element != null) {
            System.out.println(cat);
            Node n1 = m_graph.getNode(cat.getRow());
            Node n2 = m_graph.getNode(element.getRow());
            TableEdge e = (TableEdge) m_graph.addEdge(n1, n2);
            VisualItem f = (VisualItem) m_visualGraph.getEdge(e.getRow());
            f.setInt(VisualItem.STROKECOLOR, ColorLib.gray(200));
            f.setInt(VisualItem.FILLCOLOR, ColorLib.gray(200));
        }

    }

    // -------------------------------------------------------------------------------------
    /**
     * Initialize the contents of the frame
     */
    private void createContents() {
        setLayout(new BorderLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        m_toolBar = new JToolBar();
        m_toolBar.setActionMap(null);
        add(m_toolBar, BorderLayout.NORTH);

        final JToggleButton panToggleButton = new JToggleButton();
        panToggleButton.setText("PAN");
        m_buttonGroup.add(panToggleButton);
        m_toolBar.add(panToggleButton);
        panToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_PAN;
            }
        });

        final JToggleButton pointToggleButton = new JToggleButton();
        m_buttonGroup.add(pointToggleButton);
        pointToggleButton.setText(" + Poi");
        m_toolBar.add(pointToggleButton);
        pointToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_ADD_POI;
            }
        });

        final JToggleButton catToggleButton = new JToggleButton();
        m_buttonGroup.add(catToggleButton);
        catToggleButton.setText("+ Cat");
        m_toolBar.add(catToggleButton);
        catToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_ADD_CAT;
            }
        });

        final JToggleButton connectToggleButton = new JToggleButton();
        m_buttonGroup.add(connectToggleButton);
        connectToggleButton.setText("-->");
        m_toolBar.add(connectToggleButton);
        connectToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_CONNECT;
            }
        });

        final JToggleButton modifyToggleButton = new JToggleButton();
        m_buttonGroup.add(modifyToggleButton);
        modifyToggleButton.setActionMap(null);
        modifyToggleButton.setMinimumSize(new Dimension(100, 20));
        modifyToggleButton.setText("   *   ");
        m_toolBar.add(modifyToggleButton);
        modifyToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_MODIFY;
            }
        });

        final JToggleButton delToggleButton = new JToggleButton();
        m_buttonGroup.add(delToggleButton);
        delToggleButton.setActionMap(null);
        delToggleButton.setMinimumSize(new Dimension(100, 20));
        delToggleButton.setText("   -   ");
        m_toolBar.add(delToggleButton);
        delToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_DEL;
            }
        });
    }

}
