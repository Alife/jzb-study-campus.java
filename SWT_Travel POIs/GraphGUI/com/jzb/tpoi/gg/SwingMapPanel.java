/**
 * 
 */
package com.jzb.tpoi.gg;

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
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableEdgeItem;

import com.jzb.tpoi.data.EntityType;
import com.jzb.tpoi.data.GMercatorProjection;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TCoordinates;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TMapElement;
import com.jzb.tpoi.data.TPoint;

/**
 * @author n63636
 * 
 */
@SuppressWarnings({ "serial", "synthetic-access" })
public class SwingMapPanel extends JPanel {

    // -------------------------------------------------------------------------------------
    private class MyControl extends ControlAdapter {

        // -------------------------------------------------------------------------------------
        public void itemClicked(VisualItem item, MouseEvent e) {

            TMapElement me;

            if (!SwingUtilities.isLeftMouseButton(e))
                return;

            switch (m_toolOption) {
                case OPT_MODIFY:
                    _markAsTouched((TMapElement) item.get("value"));
                    break;

                case OPT_DEL:
                    if (item instanceof TableEdgeItem) {
                        NodeItem ni1 = ((TableEdgeItem) item).getSourceItem();
                        NodeItem ni2 = ((TableEdgeItem) item).getTargetItem();
                        TCategory catOrig = (TCategory) ni1.get("value");
                        TMapElement element = (TMapElement) ni2.get("value");
                        if (element.getType() == EntityType.Category) {
                            catOrig.getSubCategories().remove((TCategory) element);
                        } else {
                            catOrig.getPoints().remove((TPoint) element);
                        }
                        _markAsTouched(catOrig);
                        // _markAsTouched(element); NO SE MARCA UN ELEMENTO POR PERDER INFORMACION DE CATEGORIZACION
                        m_graph.removeEdge(item.getRow());
                    } else {
                        me = (TMapElement) item.get("value");
                        if (me.getType() == EntityType.Point) {
                            for (TCategory catOrig : ((TPoint) me).getCategories()) {
                                _markAsTouched(catOrig);
                            }
                            m_map.getPoints().remove((TPoint) me);
                            _markAsTouched(me);
                        } else {
                            for (TCategory catOrig : ((TCategory) me).getCategories()) {
                                _markAsTouched(catOrig);
                            }
                            // NO SE MARCAN LOS ELEMENTOS CATEGORIZADOS POR LA PERDIDA DE INFORMACION DE CATEGORIA
                            /*
                             * for (TCategory cat : ((TCategory) me).getSubCategories()) { _markAsTouched(cat); } for (TPoint point : ((TCategory) me).getPoints()) { _markAsTouched(point); }
                             */
                            m_map.getCategories().remove((TCategory) me);
                            _markAsTouched(me);
                        }
                        m_graph.removeNode(item.getRow());
                    }
                    m_vis.repaint();
                    return;

                case OPT_CONNECT:
                    me = (TMapElement) item.get("value");
                    if (m_originItem == null) {
                        if (me.getType() == EntityType.Category) {
                            m_originItem = item;
                        }
                    } else {
                        TCategory catOrig = (TCategory) m_originItem.get("value");
                        if (me.getType() == EntityType.Category) {
                            if (catOrig.getSubCategories().add((TCategory) me)) {
                                // Solo la marca si no existia ya esa union
                                _markAsTouched(catOrig);
                                // NO SE MARCAN LOS ELEMENTOS CATEGORIZADOS POR LA INFORMACION DE CATEGORIZACION
                                // _markAsTouched(me);
                            }
                        } else {
                            if (catOrig.getPoints().add((TPoint) me)) {
                                // Solo la marca si no existia ya esa union
                                _markAsTouched(catOrig);
                                // NO SE MARCAN LOS ELEMENTOS CATEGORIZADOS POR LA INFORMACION DE CATEGORIZACION
                                // _markAsTouched(me);
                            }
                        }

                        _linkCatToElement(catOrig, me);
                        m_originItem = null;
                        m_vis.repaint();
                        return;
                    }
                    break;
            }

            if (m_lastSelectedItem != null && m_lastSelectedItem.isValid()) {
                m_lastSelectedItem.setInt(VisualItem.TEXTCOLOR, ColorLib.gray(0));
            }

            m_lastSelectedItem = item;
            m_lastSelectedItem.setInt(VisualItem.TEXTCOLOR, ColorLib.rgb(255, 0, 0));
            m_vis.repaint();
        }

        // -------------------------------------------------------------------------------------
        public void itemDragged(VisualItem item, MouseEvent e) {

            TMapElement element = (TMapElement) item.get("value");
            element.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(e.getX()), GMercatorProjection.YToLat(e.getY())));
            _markAsTouched(element);
            m_vis.repaint();
        }

        // -------------------------------------------------------------------------------------
        public void mousePressed(MouseEvent e) {
        }

        // -------------------------------------------------------------------------------------
        public void mouseReleased(MouseEvent e) {

            if (e.getModifiers() != InputEvent.BUTTON1_MASK)
                return;

            String response = JOptionPane.showInputDialog(null, "What is the element's name?", "Name:", JOptionPane.QUESTION_MESSAGE);
            if (response == null)
                return;

            TMapElement element;
            switch (m_toolOption) {
                case OPT_ADD_POI:
                    element = new TPoint(m_map);
                    m_map.getPoints().add((TPoint) element);
                    break;
                case OPT_ADD_CAT:
                    element = new TCategory(m_map);
                    m_map.getCategories().add((TCategory) element);
                    break;
                default:
                    return;
            }

            element.setName(response);
            element.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(e.getX()), GMercatorProjection.YToLat(e.getY())));
            _createElement(element, true);

            m_vis.repaint();

        }

    }

    private static enum TB_OPTION {
        OPT_ADD_CAT, OPT_ADD_POI, OPT_CONNECT, OPT_DEL, OPT_MODIFY, OPT_NOTHING, OPT_PAN
    }

    private ButtonGroup   m_ButtonGroup      = new ButtonGroup();
    private Graph         m_graph;
    private VisualItem    m_lastSelectedItem = null;
    private TMap          m_map;
    private VisualItem    m_originItem       = null;                  ;
    private JToolBar      m_toolBar;
    private TB_OPTION     m_toolOption       = TB_OPTION.OPT_NOTHING;
    private Visualization m_vis;
    private VisualGraph   m_visualGraph;

    // -------------------------------------------------------------------------------------
    /**
     * Create the panel
     */
    public SwingMapPanel() {
        super();
        setLayout(new BorderLayout());

        _createContent();
        _createVisualization();
    }

    // -------------------------------------------------------------------------------------
    public void setMap(TMap map) {
        m_map = map;
        _createMapData();
        m_vis.repaint();
    }

    // -------------------------------------------------------------------------------------
    private void _createContent() {

        setLayout(new BorderLayout());

        m_toolBar = new JToolBar();
        add(m_toolBar, BorderLayout.NORTH);

        final JToggleButton panToggleButton = new JToggleButton();
        panToggleButton.setText("PAN");
        m_ButtonGroup.add(panToggleButton);
        m_toolBar.add(panToggleButton);
        panToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_PAN;
            }
        });

        final JToggleButton pointToggleButton = new JToggleButton();
        m_ButtonGroup.add(pointToggleButton);
        pointToggleButton.setText(" + Poi");
        m_toolBar.add(pointToggleButton);
        pointToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_ADD_POI;
            }
        });

        final JToggleButton catToggleButton = new JToggleButton();
        m_ButtonGroup.add(catToggleButton);
        catToggleButton.setText("+ Cat");
        m_toolBar.add(catToggleButton);
        catToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_ADD_CAT;
            }
        });

        final JToggleButton connectToggleButton = new JToggleButton();
        m_ButtonGroup.add(connectToggleButton);
        connectToggleButton.setText("-->");
        m_toolBar.add(connectToggleButton);
        connectToggleButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_toolOption = TB_OPTION.OPT_CONNECT;
            }
        });

        final JToggleButton modifyToggleButton = new JToggleButton();
        m_ButtonGroup.add(modifyToggleButton);
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
        m_ButtonGroup.add(delToggleButton);
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

    // -------------------------------------------------------------------------------------
    private Node _createElement(TMapElement item, boolean asTouched) {

        if (asTouched) {
            item.touchAsUpdated();
        }

        Node n = m_graph.addNode();
        n.set("value", item);
        VisualItem f = (VisualItem) m_visualGraph.getNode(n.getRow());

        if (item.getType() == EntityType.Point)
            f.setString("name", (asTouched ? "* " : "") + item.getName() + " [" + item.getSyncETag().substring(9, 14) + "]");
        else
            f.setString("name", (asTouched ? "* " : "") + item.getName() + " [" + item.getSyncETag().substring(item.getSyncETag().length() - 5) + "]");

        f.setInt(VisualItem.STROKECOLOR, ColorLib.gray(0));
        f.setInt(VisualItem.TEXTCOLOR, ColorLib.gray(0));
        if (item.getType() == EntityType.Point) {
            f.setInt(VisualItem.FILLCOLOR, ColorLib.rgb(255, 180, 180));
        } else {
            f.setInt(VisualItem.FILLCOLOR, ColorLib.rgb(190, 190, 255));
        }

        f.setX(GMercatorProjection.lngToX(item.getCoordinates().getLng()));
        f.setEndX(GMercatorProjection.lngToX(item.getCoordinates().getLng()));
        f.setY(GMercatorProjection.latToY(item.getCoordinates().getLat()));
        f.setEndY(GMercatorProjection.latToY(item.getCoordinates().getLat()));

        return n;
    }

    // -------------------------------------------------------------------------------------
    private void _createMapData() {

        m_graph.getEdges().clear();
        m_graph.getNodes().clear();
        m_lastSelectedItem = null;
        m_originItem = null;
        m_toolOption = TB_OPTION.OPT_NOTHING;

        for (TPoint point : m_map.getPoints()) {
            _createElement(point, false);
        }

        for (TCategory cat : m_map.getCategories()) {
            _createElement(cat, false);
        }

        for (TCategory cat : m_map.getCategories()) {
            for (TPoint point : cat.getPoints()) {
                _linkCatToElement(cat, point);
            }
        }

        for (TCategory cat : m_map.getCategories()) {
            for (TCategory scat : cat.getSubCategories()) {
                _linkCatToElement(cat, scat);
            }
        }
    }

    // -------------------------------------------------------------------------------------
    private void _createVisualization() {

        // -------------------------------------------------------------------------------------
        m_graph = new Graph(true);
        // m_graph = new Tree();
        m_vis = new Visualization();
        m_visualGraph = m_vis.addGraph("graph", m_graph);

        // -------------------------------------------------------------------------------------
        m_graph.getNodeTable().addColumn("name", String.class);
        m_graph.getNodeTable().addColumn("value", TMapElement.class);

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
        // m_vis.putAction("layout", layout);

        // -------------------------------------------------------------------------------------
        // create a new Display that pull from our Visualization
        Display display = new Display(m_vis);
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
        add(display);

    }

    // -------------------------------------------------------------------------------------
    private void _linkCatToElement(TCategory aCat, TMapElement aElement) {

        Node n1 = _searchNodeForTMapElement(aCat);
        Node n2 = _searchNodeForTMapElement(aElement);

        if (n1 != null && n2 != null) {
            TableEdge e = (TableEdge) m_graph.addEdge(n1, n2);
            VisualItem f = (VisualItem) m_visualGraph.getEdge(e.getRow());
            f.setInt(VisualItem.STROKECOLOR, ColorLib.gray(200));
            f.setInt(VisualItem.FILLCOLOR, ColorLib.gray(200));
        }

    }

    // -------------------------------------------------------------------------------------
    private void _markAsTouched(TMapElement item) {

        item.getOwnerMap().touchAsUpdated();
        item.touchAsUpdated();
        Node node = _searchNodeForTMapElement(item);
        if (node != null) {
            if (item.getType() == EntityType.Point)
                node.setString("name", "* " + item.getName() + " [" + item.getSyncETag().substring(9, 14) + "]");
            else
                node.setString("name", "* " + item.getName() + " [" + item.getSyncETag().substring(item.getSyncETag().length() - 5) + "]");
            m_vis.repaint();
        }
    }

    // -------------------------------------------------------------------------------------
    private Node _searchNodeForTMapElement(TMapElement me) {

        Iterator iter = m_graph.getNodes().tuples();
        while (iter.hasNext()) {
            TableTuple tt = (TableTuple) iter.next();
            if (tt.get("value").equals(me)) {
                Node node = m_graph.getNode(tt.getRow());
                return node;
            }
        }
        return null;
    }

}
