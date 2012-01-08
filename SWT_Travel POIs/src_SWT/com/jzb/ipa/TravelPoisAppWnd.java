/**
 * 
 */
package com.jzb.ipa;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
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

import swing2swt.layout.BorderLayout;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.tpoi.data.TBaseEntity;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.util.AppPreferences;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class TravelPoisAppWnd implements IMapPanelOwner {

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

    private static final String   APP_NAME           = "Travel_Pois";

    private static AppPreferences s_prefs            = new AppPreferences(APP_NAME);

    private TMap                  m_activeMap;

    private Button                m_btnSync;
    private ArrayList<TCategory>  m_categoryNavStack = new ArrayList<TCategory>();
    private ProgressMonitor       m_monitor;
    private Panel_MapElementList  m_Panel_MapElementList;
    private Panel_ListOfMaps      m_Panel_MapList;
    private TabbedTracerImpl      m_tabbedTracer     = new TabbedTracerImpl();
    private Shell                 m_TravelPoisShell;
    private Text                  m_txtMapsFolder;
    private Text                  m_txtPassword;
    private Text                  m_txtUser;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            TravelPoisAppWnd window = new TravelPoisAppWnd();
            window.open();
            s_prefs.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see com.jzb.ipa.IMapPanelOwner#navigateBackward()
     */
    public void navigateBackward() {

        if (m_categoryNavStack.size() > 0) {
            m_categoryNavStack.remove(m_categoryNavStack.size() - 1);
        } else {
            m_activeMap = null;
        }

        if (m_activeMap != null) {
            m_Panel_MapElementList.setFilteringCategories(m_activeMap, m_categoryNavStack);
            m_Panel_MapElementList.setVisible(true);
            m_Panel_MapList.setVisible(false);
        } else {
            m_Panel_MapList.setVisible(true);
            m_Panel_MapElementList.setVisible(false);
        }
    }

    /**
     * @see com.jzb.ipa.IMapPanelOwner#navigateForward(com.jzb.tpoi.data.TBaseEntity)
     */
    public void navigateForward(TBaseEntity entity) {

        if (entity instanceof TMap) {
            m_activeMap = (TMap) entity;
            m_categoryNavStack.clear();
            try {
                ModelService.inst.readMapData(m_activeMap);
            } catch (Exception ex) {
                Tracer._error("Error reading map data", ex);
            }
        } else {
            m_categoryNavStack.add((TCategory) entity);
        }
        m_Panel_MapElementList.setFilteringCategories(m_activeMap, m_categoryNavStack);
        m_Panel_MapElementList.setVisible(true);
        m_Panel_MapList.setVisible(false);
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

        m_TravelPoisShell.setImage(SWTResourceManager.getImage(TravelPoisAppWnd.class, "/Properties.ico"));
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
            composite_1.addControlListener(new ControlAdapter() {

                @Override
                public void controlResized(final ControlEvent e) {
                    m_Panel_MapList.setSize(composite_1.getSize());
                    m_Panel_MapElementList.setSize(composite_1.getSize());
                }
            });
            tbtmMaps.setControl(composite_1);

            m_Panel_MapList = new Panel_ListOfMaps(this, composite_1, SWT.NONE);
            m_Panel_MapList.setBounds(0, 0, 220, 130);
            m_Panel_MapList.setVisible(true);

            m_Panel_MapElementList = new Panel_MapElementList(this, composite_1, SWT.NONE);
            m_Panel_MapElementList.setBounds(0, 0, 220, 130);
            m_Panel_MapElementList.setVisible(false);

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
        m_txtUser.setText(Des3Encrypter.decryptStr(s_prefs.getPref("user", "")));
        m_txtPassword.setText(Des3Encrypter.decryptStr(s_prefs.getPref("password", "")));

        new InitWorker(null).initMaps(m_Panel_MapList);

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
            s_prefs.setPref("user", Des3Encrypter.encryptStr(m_txtUser.getText()));
            s_prefs.setPref("password", Des3Encrypter.encryptStr(m_txtPassword.getText()));

            s_prefs.save();
        } catch (Exception ex) {
        }
    }

    private void syncMaps(String usr, String pwd) {
        _executionStarted();
        SyncWorker sw = new SyncWorker(m_monitor);
        sw.syncAllMaps(usr, pwd);
    }
}