/**
 * 
 */
package com.jzb.tpoi.gg;

import javax.swing.JComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.demos.GraphView;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.GraphLib;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import swing2swt.layout.BorderLayout;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class POIGraphGUIWnd {

    private class ProgressMonitor implements IProgressMonitor {

        /**
         * @see com.jzb.swt.util2.tools.IProgressMonitor#processingEnded(boolean)
         */
        public void processingEnded(boolean failed, Object result) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _executionEnded();
                }
            });
        }

    }

    private static final String   APP_NAME       = "POIGraphGUIWnd";

    private static final String   edges          = "graph.edges";

    private static final String   graph          = "graph";
    private static final String   nodes          = "graph.nodes";
    private static AppPreferences s_prefs        = new AppPreferences(APP_NAME);
    private Button                m_btnSync;
    private ProgressMonitor       m_monitor;
    private TabbedTracerImpl      m_tabbedTracer = new TabbedTracerImpl();
    private Shell                 m_TravelPoisShell;

    private Text                  m_txtMapsFolder;

    private Text                  m_txtPassword;

    private Text                  m_txtUser;
    private Visualization         m_vis;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            POIGraphGUIWnd window = new POIGraphGUIWnd();
            window.open();
            s_prefs.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open() throws Exception {

        final Display display = Display.getDefault();
        createContents();
        m_TravelPoisShell.open();
        m_TravelPoisShell.layout();

        _setWndPosition();
        _initFields();

        m_TravelPoisShell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _updatePrefs();
                System.exit(0);
            }
        });

        while (!m_TravelPoisShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_TravelPoisShell = new Shell();
        final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.spacing = 5;
        m_TravelPoisShell.setLayout(fillLayout);

        m_TravelPoisShell.setImage(SWTResourceManager.getImage(POIGraphGUIWnd.class, "/Properties.ico"));
        m_TravelPoisShell.setSize(586, 400);
        m_TravelPoisShell.setMinimumSize(new Point(400, 400));
        m_TravelPoisShell.setText("Travel POIs");

        Composite composite;
        TabFolder tabFolder;
        {

        }

        Composite composite_2;
        composite = new Composite(m_TravelPoisShell, SWT.NONE);
        composite.setLayout(new BorderLayout(0, 0));
        tabFolder = new TabFolder(composite, SWT.NONE);

        {
            TabItem tbtmMaps = new TabItem(tabFolder, SWT.NONE);
            tbtmMaps.setText("Maps");

            final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
            tbtmMaps.setControl(composite_1);

            JComponent drawPanel = create_grapth();
            final Button button = new Button(composite_1, SWT.NONE);
            button.setText("button");
            button.setBounds(79, 37, 48, 25);

        }

        TabItem tbtmSettings = new TabItem(tabFolder, SWT.NONE);
        tbtmSettings.setText("Settings");

        Composite composite_3 = new Composite(tabFolder, SWT.NONE);
        tbtmSettings.setControl(composite_3);

        m_txtMapsFolder = new Text(composite_3, SWT.BORDER);
        m_txtMapsFolder.setText("");
        m_txtMapsFolder.setBounds(122, 14, 564, 21);

        Button btnMapsFolder = new Button(composite_3, SWT.NONE);
        btnMapsFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _selectFolder(m_txtMapsFolder);
            }
        });
        btnMapsFolder.setText("...");
        btnMapsFolder.setBounds(692, 12, 38, 25);

        m_txtUser = new Text(composite_3, SWT.BORDER);
        m_txtUser.setText("");
        m_txtUser.setBounds(122, 45, 283, 21);

        m_txtPassword = new Text(composite_3, SWT.BORDER | SWT.PASSWORD);
        m_txtPassword.setText("");
        m_txtPassword.setBounds(122, 76, 283, 21);

        Label lblBothIpasFolder = new Label(composite_3, SWT.NONE);
        lblBothIpasFolder.setText("Password:");
        lblBothIpasFolder.setBounds(10, 79, 119, 15);

        Label lblIpadIpasFolder = new Label(composite_3, SWT.NONE);
        lblIpadIpasFolder.setText("User:");
        lblIpadIpasFolder.setBounds(10, 48, 119, 15);

        Label lblIphoneFolder = new Label(composite_3, SWT.NONE);
        lblIphoneFolder.setText("Maps Folder:");
        lblIphoneFolder.setBounds(10, 17, 119, 15);

        m_btnSync = new Button(composite_3, SWT.NONE);
        m_btnSync.setText("Sync");
        m_btnSync.setBounds(122, 116, 48, 25);
        m_btnSync.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                syncMaps(m_txtUser.getText(), m_txtPassword.getText());
            }
        });
        composite_2 = new Composite(m_TravelPoisShell, SWT.NONE);
        composite_2.setLayout(new BorderLayout(0, 0));

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        @SuppressWarnings("unused")
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(composite_2);
        Tracer.setTracer(m_tabbedTracer);

    }

    private void _disableButtons() {
        m_btnSync.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnSync.setEnabled(true);
    }

    private void _executionEnded() {
        _enableButtons();
    }

    private void _executionStarted() {
        Tracer.reset();
        _disableButtons();
    }

    private void _initFields() throws Exception {

        m_monitor = new ProgressMonitor();

        m_txtMapsFolder.setText(s_prefs.getPref("mapsFolder", ""));
        // m_txtUser.setText(Des3Encrypter.decryptStr(s_prefs.getPref("user", "")));
        // m_txtPassword.setText(Des3Encrypter.decryptStr(s_prefs.getPref("password", "")));

        // new InitWorker(null).initMaps(m_Panel_MapList);

    }

    private void _selectFolder(Text control) {
        DirectoryDialog dd = new DirectoryDialog(m_TravelPoisShell);
        dd.setFilterPath(control.getText());
        String newValue = dd.open();
        if (newValue != null)
            control.setText(newValue);
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        // int w = (80 * r.width) / 100;
        int w = m_TravelPoisShell.getSize().x;
        int h = (80 * r.height) / 100;
        // int h = m_TravelPoisShell.getSize().y;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_TravelPoisShell.setBounds(x, y, w, h);
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("mapsFolder", m_txtMapsFolder.getText());
            // s_prefs.setPref("user", Des3Encrypter.encryptStr(m_txtUser.getText()));
            // s_prefs.setPref("password", Des3Encrypter.encryptStr(m_txtPassword.getText()));

            s_prefs.save();
        } catch (Exception ex) {
        }
    }

    // ------------------------------------------------------------------------------------------------------
    private JComponent create_grapth() {
        Graph g = null;
        g = GraphLib.getGrid(15, 15);
        // g = new GraphMLReader().readGraph(datafile);
        // create a new, empty visualization for our data

        m_vis = new Visualization();

        // --------------------------------------------------------------------
        // set up the renderers

        LabelRenderer tr = new LabelRenderer();
        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // --------------------------------------------------------------------
        // register the data with a visualization

        // adds graph to visualization and sets renderer label field
        // update labeling
        DefaultRendererFactory drf = (DefaultRendererFactory) m_vis.getRendererFactory();
        ((LabelRenderer) drf.getDefaultRenderer()).setTextField("label");

        // update graph
        m_vis.removeGroup(graph);
        VisualGraph vg = m_vis.addGraph(graph, g);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);
        VisualItem f = (VisualItem) vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(false);

        m_vis.run("draw");

        final GraphView view = new GraphView(g, "label");
        return view;

    }

    private void syncMaps(String usr, String pwd) {
        _executionStarted();
        // SyncWorker sw = new SyncWorker(m_monitor);
        // sw.syncAllMaps(usr, pwd);
    }
}