/**
 * 
 */
package com.jzb.nsf;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jzb.nsf.DGetUsrPwd.T_UsrPwd;
import com.jzb.nsf.gcal.GCalHelper;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.AppPreferences;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class NotesSyncAppWnd {

    private class ProgressMonitor implements IProgressMonitor {

        /**
         * @see com.jzb.swt.util.tools.IProgressMonitor#processingEnded(boolean)
         */
        public void processingEnded(final boolean failed, final Object result) {

            final boolean infoChanged = result != null ? (Boolean) result : false;

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _executionEnded(failed, infoChanged);
                }
            });
        }

    }

    private static final String APP_NAME = "JZB_GCal_Sync";
    private static AppPreferences  s_prefs  = new AppPreferences(APP_NAME);

    private Button              m_btnSync;
    private Button              m_chkAutoSync;
    private Button              m_chkJustCheck;

    private Button              m_chkUseProxy;
    private TabItem             m_debugTabItem;
    private TabItem             m_errorTabItem;
    private TabItem             m_infoTabItem;

    private ProgressMonitor     m_monitor;
    private Shell               m_shell;
    private Spinner             m_spnMinutes;
    private Timer               m_timer;
    private TabbedTracerImpl    m_tracer;

    private TrayItem            m_trayItem;

    private Menu                m_trayMenu;

    private ToolTip             m_trayTip;
    private Text                m_txtNSFDataSource;
    private TabItem             m_warnTabItem;
    private Button              m_btnChgUsrPwd;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            NotesSyncAppWnd window = new NotesSyncAppWnd();
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
        m_shell.layout();
        _setWndPosition();
        _initFields();
        _prepareTrayIcon();
        m_shell.open();

        m_shell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _shutdownApp();
            }

            @Override
            public void shellIconified(ShellEvent e) {
                _closeToTray();
            }
        });

        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_shell = new Shell();

        m_shell.setImage(SWTResourceManager.getImage(NotesSyncAppWnd.class, "/Calendar.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_shell.setLayout(borderLayout);
        m_shell.setSize(643, 640);
        m_shell.setMinimumSize(new Point(600, 300));
        m_shell.setText("Notes to GCal Sync");

        final Composite composite_1 = new Composite(m_shell, SWT.NONE);
        composite_1.setLayout(new BorderLayout(0, 0));
        composite_1.setLayoutData(BorderLayout.NORTH);

        Composite composite;
        composite = new Composite(composite_1, SWT.BORDER);
        
        m_btnChgUsrPwd = new Button(composite, SWT.NONE);
        m_btnChgUsrPwd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DGetUsrPwd dlg= new DGetUsrPwd(m_shell);
                T_UsrPwd result=dlg.open();
                if(result!=null) {
                    GCalHelper.encodeUserPwd(s_prefs,result.user, result.pwd);
                }
            }
        });
        m_btnChgUsrPwd.setBounds(256, 76, 94, 26);
        m_btnChgUsrPwd.setText("Chg Usr/Pwd");

        m_txtNSFDataSource = new Text(composite, SWT.BORDER);
        m_txtNSFDataSource.setBounds(123, 6, 333, 22);

        Label lblNSFDataSource = new Label(composite, SWT.NONE);
        lblNSFDataSource.setBounds(22, 9, 105, 16);
        lblNSFDataSource.setText("NSF DataSource:");
        {
            m_chkAutoSync = new Button(composite, SWT.CHECK);
            m_chkAutoSync.setAlignment(SWT.RIGHT);
            m_chkAutoSync.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    _processAutoSycn();
                }
            });
            m_chkAutoSync.setBounds(244, 37, 84, 17);
            m_chkAutoSync.setText("Auto Sync:");
        }
        {
            m_spnMinutes = new Spinner(composite, SWT.BORDER);
            m_spnMinutes.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent arg0) {
                    _processAutoSycn();
                }
            });
            m_spnMinutes.setMaximum(1000);
            m_spnMinutes.setMinimum(1);
            m_spnMinutes.setSelection(30);
            m_spnMinutes.setBounds(334, 34, 73, 23);
        }
        {
            Label lblMinutes = new Label(composite, SWT.NONE);
            lblMinutes.setBounds(413, 37, 59, 16);
            lblMinutes.setText("Minutes");
        }

        m_chkUseProxy = new Button(composite, SWT.CHECK);
        m_chkUseProxy.setBounds(134, 37, 96, 17);
        m_chkUseProxy.setSelection(true);
        m_chkUseProxy.setText("Use Proxy");

        m_chkJustCheck = new Button(composite, SWT.CHECK);
        m_chkJustCheck.setBounds(22, 37, 96, 17);
        m_chkJustCheck.setText("Just Check");

        Label lblFixHeight = new Label(composite, SWT.NONE);
        lblFixHeight.setBounds(0, 0, 16, 107);
        composite.setLayoutData(BorderLayout.NORTH);
        m_btnSync = new Button(composite, SWT.NONE);
        m_btnSync.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _syncCalendars();
            }
        });
        m_btnSync.setBounds(362, 76, 94, 26);
        m_btnSync.setText("Synchronize");
        final TabFolder m_tabTraces = new TabFolder(m_shell, SWT.NONE);
        m_tabTraces.setLayoutData(BorderLayout.CENTER);

        m_debugTabItem = new TabItem(m_tabTraces, SWT.NONE);
        m_debugTabItem.setText("Debug");

        final Text txtDebug = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtDebug.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtDebug.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtDebug.setEditable(false);
        m_debugTabItem.setControl(txtDebug);

        m_infoTabItem = new TabItem(m_tabTraces, SWT.NONE);
        m_infoTabItem.setText("Info");

        final Text txtInfo = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_infoTabItem.setControl(txtInfo);
        txtInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtInfo.setEditable(false);

        m_warnTabItem = new TabItem(m_tabTraces, SWT.NONE);
        m_warnTabItem.setText("Warning");

        final Text txtWarn = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtWarn.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtWarn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtWarn.setEditable(false);
        m_warnTabItem.setControl(txtWarn);

        m_errorTabItem = new TabItem(m_tabTraces, SWT.NONE);
        m_errorTabItem.setText("Error");

        final Text txtError = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        txtError.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtError.setEditable(false);
        m_errorTabItem.setControl(txtError);
    }

    private void _closeToTray() {

        Image image = SWTResourceManager.getImage(NotesSyncAppWnd.class, "/Calendar.ico");

        Tray tray = Display.getDefault().getSystemTray();
        if (tray != null) {

            TrayItem item = new TrayItem(tray, SWT.NONE);
            item.setImage(image);
            item.setToolTip(m_trayTip);
            item.setToolTipText("Notes to GCal Synchronizer");

            item.addListener(SWT.MenuDetect, new Listener() {

                public void handleEvent(Event event) {
                    m_trayMenu.setVisible(true);
                }
            });

            item.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent ev) {
                    _openFromTray((TrayItem) ev.widget);
                }

                public void widgetSelected(SelectionEvent arg0) {
                }
            });

            m_trayItem = item;
            m_shell.setVisible(false);
        }
    }

    private void _disableButtons() {
        m_btnSync.setEnabled(false);
        m_btnChgUsrPwd.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnSync.setEnabled(true);
        m_btnChgUsrPwd.setEnabled(true);
    }

    private void _executionEnded(final boolean failed, final boolean infoChanged) {
        _enableButtons();
        _processAutoSycn();

        if (m_trayItem != null) {
            Image image = SWTResourceManager.getImage(NotesSyncAppWnd.class, "/Calendar.ico");
            m_trayItem.setImage(image);

            String msg;

            if (failed)
                msg = "ERROR: Synchronization process failed!!";
            else
                msg = "Synchronization executed." + (infoChanged ? "\n\nChanges were sent to GCal" : "");

            m_trayTip.setMessage(msg);
            m_trayTip.setVisible(true);
        }
    }

    private void _executionStarted() {
        m_tracer.reset();
        _disableButtons();
        if (m_timer != null) {
            m_timer.cancel();
            m_timer = null;
            m_tracer._info("Previous AutoSync timer destroyed");
        }
        if (m_trayItem != null) {
            Image image = SWTResourceManager.getImage(NotesSyncAppWnd.class, "/Publish.ico");
            m_trayItem.setImage(image);
        }
    }

    private void _initFields() throws Exception {

        m_tracer = new TabbedTracerImpl(m_debugTabItem, m_infoTabItem, m_warnTabItem, m_errorTabItem);
        m_monitor = new ProgressMonitor();

        m_txtNSFDataSource.setText(s_prefs.getPref("nsf_dataSource", "*NOT_DEFINED*"));
        m_chkUseProxy.setSelection(s_prefs.getPrefBool("use.proxy", true));
        m_chkAutoSync.setSelection(s_prefs.getPrefBool("autoSync", false));
        m_spnMinutes.setSelection((int) s_prefs.getPrefLong("minutes", 30));
    }

    private void _openFromTray(TrayItem trayItem) {
        if (trayItem != null)
            trayItem.dispose();
        m_shell.setMinimized(false);
        m_shell.setVisible(true);
        m_trayItem = null;
    }

    private void _prepareTrayIcon() {

        m_trayTip = new ToolTip(m_shell, SWT.BALLOON | SWT.ICON_INFORMATION);
        m_trayTip.setText("Notes to GCal Synchronization");
        m_trayTip.setMessage("");
        m_trayTip.setAutoHide(true);

        MenuItem menuItem;
        m_trayMenu = new Menu(m_shell, SWT.POP_UP);

        menuItem = new MenuItem(m_trayMenu, SWT.PUSH);
        menuItem.setText("Synch now");
        menuItem.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event e) {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        _syncCalendars();
                    }
                });
            }

        });

        menuItem = new MenuItem(m_trayMenu, SWT.PUSH);
        menuItem.setText("Exit");
        menuItem.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event e) {
                _shutdownApp();
            }
        });

    }

    private void _processAutoSycn() {

        if (m_timer != null) {
            m_timer.cancel();
            m_timer = null;
            m_tracer._info("Previous AutoSync timer destroyed");
        }

        boolean checked = m_chkAutoSync.getSelection();
        m_spnMinutes.setEnabled(checked);

        if (!checked)
            return;

        m_timer = new Timer("NotesGCalSyncTimer", true);

        TimerTask tt = new TimerTask() {

            @Override
            public void run() {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        m_tracer._info("*T* TIMER Execution");
                        _syncCalendars();
                    }
                });
            }
        };

        long period = m_spnMinutes.getSelection();
        m_timer.schedule(tt, period * 60000L);
        m_tracer._info("Created new AutoSync timer. Execution in " + period + " minutes");
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (60 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_shell.setBounds(x, y, w, h);
    }

    private void _shutdownApp() {
        if (m_trayItem != null)
            m_trayItem.dispose();
        _updatePrefs();
        SWTResourceManager.dispose();
        System.exit(0);
    }

    private void _syncCalendars() {

        if (!m_btnSync.getEnabled()) {
            m_tracer._info("* Already synchronizing. Skipping.");
            return;
        }

        _executionStarted();
        _updatePrefs();

        NotesSyncWorker gcsw = new NotesSyncWorker(m_chkJustCheck.getSelection(), m_monitor);
        gcsw.syncCalendars(s_prefs);
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("nsf_dataSource", m_txtNSFDataSource.getText());
            s_prefs.setPrefBool("use.proxy", m_chkUseProxy.getSelection());
            s_prefs.setPrefBool("autoSync", m_chkAutoSync.getSelection());
            s_prefs.setPrefLong("minutes", m_spnMinutes.getSelection());
            s_prefs.save();
        } catch (Exception ex) {
        }
    }
}