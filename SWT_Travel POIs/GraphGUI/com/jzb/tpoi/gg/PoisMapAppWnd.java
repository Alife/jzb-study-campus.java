/**
 * 
 */
package com.jzb.tpoi.gg;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.wnd.TravelPoisAppWnd;
import com.jzb.util.AppPreferences;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class PoisMapAppWnd {

    private Button m_btnInitialize;
    private Button m_btnSyncMap2;
    private Button m_btnReadMap2;
    private Button m_btnSyncMap1;
    private Button m_btnReadMap1;

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

    private static final String   APP_NAME       = "Pois_Map";

    private static AppPreferences s_prefs        = new AppPreferences(APP_NAME);

    private TMap                  m_activeMap1;
    private TMap                  m_activeMap2;

    private ProgressMonitor       m_monitor;
    private Shell                 m_PoisMapShell;
    private TabbedTracerImpl      m_tabbedTracer = new TabbedTracerImpl();
    private Text                  m_txtMapsFolder1;
    private Text                  m_txtMapsFolder2;
    private Text                  m_txtPassword;
    private Text                  m_txtUser;

    private SwingMapPanel         m_smPanel1;
    private SwingMapPanel         m_smPanel2;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            PoisMapAppWnd window = new PoisMapAppWnd();
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
        m_PoisMapShell.open();
        m_PoisMapShell.layout();

        _setWndPosition();
        _initFields();

        m_PoisMapShell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _updatePrefs();
                System.exit(0);
            }
        });

        while (!m_PoisMapShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_PoisMapShell = new Shell();
        final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.spacing = 5;
        m_PoisMapShell.setLayout(fillLayout);

        m_PoisMapShell.setImage(SWTResourceManager.getImage(TravelPoisAppWnd.class, "/Properties.ico"));
        m_PoisMapShell.setSize(760, 444);
        m_PoisMapShell.setMinimumSize(new Point(400, 400));
        m_PoisMapShell.setText("Travel POIs");

        Composite composite;
        TabFolder tabFolder;
        {

        }

        Composite composite_2;
        composite = new Composite(m_PoisMapShell, SWT.NONE);
        composite.setLayout(new BorderLayout(0, 0));
        tabFolder = new TabFolder(composite, SWT.NONE);

        {
            TabItem tbtmMaps = new TabItem(tabFolder, SWT.NONE);
            tbtmMaps.setText("Maps");

            final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
            composite_1.setLayout(new BorderLayout(0, 0));
            composite_1.addControlListener(new ControlAdapter() {

                @Override
                public void controlResized(final ControlEvent e) {
                }
            });
            tbtmMaps.setControl(composite_1);

            final Composite composite_3 = new Composite(composite_1, SWT.BORDER);
            composite_3.setLayout(new FormLayout());
            composite_3.setLayoutData(BorderLayout.NORTH);

            m_btnReadMap1 = new Button(composite_3, SWT.NONE);
            final FormData fd_btnReadMap1 = new FormData();
            fd_btnReadMap1.top = new FormAttachment(0, 5);
            fd_btnReadMap1.right = new FormAttachment(0, 93);
            fd_btnReadMap1.left = new FormAttachment(0, 10);
            m_btnReadMap1.setLayoutData(fd_btnReadMap1);
            m_btnReadMap1.setText("Read Map 1");
            m_btnReadMap1.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(final SelectionEvent e) {
                    _readMap1();
                }
            });

            m_btnSyncMap1 = new Button(composite_3, SWT.NONE);
            final FormData fd_btnSyncMap1 = new FormData();
            fd_btnSyncMap1.top = new FormAttachment(m_btnReadMap1, -25, SWT.BOTTOM);
            fd_btnSyncMap1.bottom = new FormAttachment(m_btnReadMap1, 0, SWT.BOTTOM);
            fd_btnSyncMap1.left = new FormAttachment(m_btnReadMap1, 5, SWT.RIGHT);
            m_btnSyncMap1.setLayoutData(fd_btnSyncMap1);
            m_btnSyncMap1.setText("Sync Map 1");
            m_btnSyncMap1.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(final SelectionEvent e) {
                    _syncMap1();
                }
            });

            m_btnReadMap2 = new Button(composite_3, SWT.NONE);
            final FormData fd_btnReadMap2 = new FormData();
            fd_btnReadMap2.top = new FormAttachment(m_btnSyncMap1, 0, SWT.TOP);
            fd_btnReadMap2.left = new FormAttachment(0, 210);
            m_btnReadMap2.setLayoutData(fd_btnReadMap2);
            m_btnReadMap2.setText("Read Map 2");
            m_btnReadMap2.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(final SelectionEvent e) {
                    _readMap2();
                }
            });

            m_btnSyncMap2 = new Button(composite_3, SWT.NONE);
            final FormData fd_btnSyncMap2 = new FormData();
            fd_btnSyncMap2.bottom = new FormAttachment(m_btnReadMap2, 25, SWT.TOP);
            fd_btnSyncMap2.top = new FormAttachment(m_btnReadMap2, 0, SWT.TOP);
            fd_btnSyncMap2.right = new FormAttachment(m_btnReadMap2, 78, SWT.RIGHT);
            fd_btnSyncMap2.left = new FormAttachment(m_btnReadMap2, 5, SWT.RIGHT);
            m_btnSyncMap2.setLayoutData(fd_btnSyncMap2);
            m_btnSyncMap2.setText("Sync Map 2");
            m_btnSyncMap2.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(final SelectionEvent e) {
                    _syncMap2();
                }
            });

            m_btnInitialize = new Button(composite_3, SWT.NONE);
            m_btnInitialize.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(final SelectionEvent e) {
                    _initialize();
                }
            });
            final FormData fd_btnInitialize = new FormData();
            fd_btnInitialize.top = new FormAttachment(m_btnSyncMap2, -25, SWT.BOTTOM);
            fd_btnInitialize.bottom = new FormAttachment(m_btnSyncMap2, 0, SWT.BOTTOM);
            fd_btnInitialize.left = new FormAttachment(0, 395);
            fd_btnInitialize.right = new FormAttachment(0, 450);
            m_btnInitialize.setLayoutData(fd_btnInitialize);
            m_btnInitialize.setText("Initialize");

            final Composite composite_4 = new Composite(composite_1, SWT.NONE);
            composite_4.setLayout(new FillLayout());
            composite_4.setLayoutData(BorderLayout.CENTER);

            final Composite composite_5 = new Composite(composite_4, SWT.BORDER | SWT.EMBEDDED | SWT.NO_BACKGROUND);
            composite_5.setLayout(new FillLayout());
            Frame frame1 = SWT_AWT.new_Frame(composite_5);
            m_smPanel1 = new SwingMapPanel();
            frame1.add(m_smPanel1);

            final Composite composite_6 = new Composite(composite_4, SWT.BORDER | SWT.EMBEDDED | SWT.NO_BACKGROUND);
            composite_6.setLayout(new FillLayout());
            Frame frame2 = SWT_AWT.new_Frame(composite_6);
            m_smPanel2 = new SwingMapPanel();
            frame2.add(m_smPanel2);
        }

        TabItem tbtmSettings = new TabItem(tabFolder, SWT.NONE);
        tbtmSettings.setText("Settings");

        Composite composite_3 = new Composite(tabFolder, SWT.NONE);
        tbtmSettings.setControl(composite_3);

        m_txtMapsFolder1 = new Text(composite_3, SWT.BORDER);
        m_txtMapsFolder1.setText("");
        m_txtMapsFolder1.setBounds(98, 12, 564, 21);

        Button btnMapsFolder1 = new Button(composite_3, SWT.NONE);
        btnMapsFolder1.setText("...");
        btnMapsFolder1.setBounds(668, 10, 38, 25);
        btnMapsFolder1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _selectFolder(m_txtMapsFolder1);
            }
        });

        m_txtMapsFolder2 = new Text(composite_3, SWT.BORDER);
        m_txtMapsFolder2.setText("");
        m_txtMapsFolder2.setBounds(98, 39, 564, 21);

        Button btnMapsFolder2 = new Button(composite_3, SWT.NONE);
        btnMapsFolder2.setText("...");
        btnMapsFolder2.setBounds(668, 37, 38, 25);
        btnMapsFolder2.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _selectFolder(m_txtMapsFolder2);
            }
        });

        m_txtUser = new Text(composite_3, SWT.BORDER);
        m_txtUser.setText("");
        m_txtUser.setBounds(98, 66, 283, 21);

        m_txtPassword = new Text(composite_3, SWT.BORDER | SWT.PASSWORD);
        m_txtPassword.setText("");
        m_txtPassword.setBounds(98, 93, 283, 21);

        Label lblBothIpasFolder = new Label(composite_3, SWT.NONE);
        lblBothIpasFolder.setText("Password:");
        lblBothIpasFolder.setBounds(10, 96, 119, 15);

        Label lblIpadIpasFolder = new Label(composite_3, SWT.NONE);
        lblIpadIpasFolder.setText("User:");
        lblIpadIpasFolder.setBounds(10, 69, 119, 15);

        Label lblIphoneFolder1 = new Label(composite_3, SWT.NONE);
        lblIphoneFolder1.setText("Maps Folder 1:");
        lblIphoneFolder1.setBounds(10, 15, 119, 15);

        Label lblIphoneFolder2 = new Label(composite_3, SWT.NONE);
        lblIphoneFolder2.setText("Maps Folder 2:");
        lblIphoneFolder2.setBounds(10, 42, 119, 15);

        composite_2 = new Composite(m_PoisMapShell, SWT.NONE);
        composite_2.setLayout(new BorderLayout(0, 0));

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        @SuppressWarnings("unused")
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(composite_2);
        Tracer.setTracer(m_tabbedTracer);

    }

    private void _disableButtons() {
        m_btnReadMap1.setEnabled(false);
        m_btnSyncMap1.setEnabled(false);
        m_btnReadMap2.setEnabled(false);
        m_btnSyncMap2.setEnabled(false);
        m_btnInitialize.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnReadMap1.setEnabled(true);
        m_btnSyncMap1.setEnabled(true);
        m_btnReadMap2.setEnabled(true);
        m_btnSyncMap2.setEnabled(true);
        m_btnInitialize.setEnabled(true);
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

        m_txtMapsFolder1.setText(s_prefs.getPref("mapsFolder1", ""));
        m_txtMapsFolder2.setText(s_prefs.getPref("mapsFolder2", ""));
        m_txtUser.setText(Des3Encrypter.decryptStr(s_prefs.getPref("user", "")));
        m_txtPassword.setText(Des3Encrypter.decryptStr(s_prefs.getPref("password", "")));

    }

    private void _selectFolder(Text control) {
        DirectoryDialog dd = new DirectoryDialog(m_PoisMapShell);
        dd.setFilterPath(control.getText());
        String newValue = dd.open();
        if (newValue != null)
            control.setText(newValue);
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        // int w = (80 * r.width) / 100;
        int w = m_PoisMapShell.getSize().x;
        int h = (80 * r.height) / 100;
        // int h = m_PoisMapShell.getSize().y;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_PoisMapShell.setBounds(x, y, w, h);
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("mapsFolder1", m_txtMapsFolder1.getText());
            s_prefs.setPref("mapsFolder2", m_txtMapsFolder2.getText());
            s_prefs.setPref("user", Des3Encrypter.encryptStr(m_txtUser.getText()));
            s_prefs.setPref("password", Des3Encrypter.encryptStr(m_txtPassword.getText()));

            s_prefs.save();
        } catch (Exception ex) {
        }
    }

    private void _readMap1() {
        _executionStarted();
        MapReaderWorker worker = new MapReaderWorker(m_monitor);
        worker.readMap(m_txtMapsFolder1.getText(), new MapReaderWorker.INotification() {

            public void mapRead(TMap map) {
                m_activeMap1 = map;
                m_smPanel1.setMap(map);
            }
        });
    }

    private void _readMap2() {
        _executionStarted();
        MapReaderWorker worker = new MapReaderWorker(m_monitor);
        worker.readMap(m_txtMapsFolder2.getText(), new MapReaderWorker.INotification() {

            public void mapRead(TMap map) {
                m_activeMap2 = map;
                m_smPanel2.setMap(map);
            }
        });
    }

    private void _syncMap1() {
        _executionStarted();
        MapSyncWorker worker = new MapSyncWorker(m_monitor);
        worker.syncMap(m_txtMapsFolder1.getText(), m_activeMap1, new MapSyncWorker.INotification() {

            public void mapSynced(TMap map) {
                m_activeMap1 = map;
                m_smPanel1.setMap(map);
            }
        });
    }

    private void _syncMap2() {
        _executionStarted();
        MapSyncWorker worker = new MapSyncWorker(m_monitor);
        worker.syncMap(m_txtMapsFolder2.getText(), m_activeMap2, new MapSyncWorker.INotification() {

            public void mapSynced(TMap map) {
                m_activeMap2 = map;
                m_smPanel2.setMap(map);
            }
        });
    }

    private void _initialize() {
        _executionStarted();
        InitializeWorker worker = new InitializeWorker(m_monitor);
        worker.init(m_txtMapsFolder1.getText(), m_txtMapsFolder2.getText(), new InitializeWorker.INotification() {

            public void done() {

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        _readMap1();
                    }
                });
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        _readMap2();
                    }
                });
            }
        });
    }
}
