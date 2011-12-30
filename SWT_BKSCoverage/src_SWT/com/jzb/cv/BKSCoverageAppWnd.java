/**
 * 
 */
package com.jzb.cv;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class BKSCoverageAppWnd {

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

    private static final String APP_NAME = "BKSCoverage";

    private static AppPreferences  s_prefs  = new AppPreferences(APP_NAME);

    private CTabItem             m_debugTabItem;
    private CTabItem             m_errorTabItem;
    private CTabItem             m_infoTabItem;
    private ProgressMonitor     m_monitor;
    private Shell               m_BkscoverageShell;
    private TabbedTracerImpl    m_tracer;
    private CTabItem             m_warnTabItem;
    private Button              m_btnCheckWKSP;
    private Button              m_chkLoadFromStorage;
    private Text                m_txtWKSPFolder;
    private Text                m_txtStorageFile;
    private Label               m_lblStorageFile;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            BKSCoverageAppWnd window = new BKSCoverageAppWnd();
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
        m_BkscoverageShell.open();
        m_BkscoverageShell.layout();

        _setWndPosition();
        _initFields();

        m_BkscoverageShell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _updatePrefs();
                System.exit(0);
            }
        });

        while (!m_BkscoverageShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_BkscoverageShell = new Shell();

        m_BkscoverageShell.setImage(SWTResourceManager.getImage(BKSCoverageAppWnd.class, "/Properties.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_BkscoverageShell.setLayout(borderLayout);
        m_BkscoverageShell.setSize(800, 400);
        m_BkscoverageShell.setMinimumSize(new Point(800, 400));
        m_BkscoverageShell.setText("BKSCoverage");

        Composite composite;
        composite = new Composite(m_BkscoverageShell, SWT.NONE);
        {
            TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
            tabFolder.setBounds(10, 10, 748, 173);

            {
                TabItem tbtmTools = new TabItem(tabFolder, SWT.NONE);
                tbtmTools.setText("Parameters");

                Composite composite_1 = new Composite(tabFolder, SWT.NONE);
                tbtmTools.setControl(composite_1);

                m_txtWKSPFolder = new Text(composite_1, SWT.BORDER);
                m_txtWKSPFolder.setText("");
                m_txtWKSPFolder.setBounds(122, 14, 564, 21);

                Button btnWKSPFolder = new Button(composite_1, SWT.NONE);
                btnWKSPFolder.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        _selectFolder(m_txtWKSPFolder);

                    }
                });
                btnWKSPFolder.setText("...");
                btnWKSPFolder.setBounds(692, 12, 38, 25);

                m_txtStorageFile = new Text(composite_1, SWT.BORDER);
                m_txtStorageFile.setText("");
                m_txtStorageFile.setBounds(122, 45, 564, 21);

                Button btnStorageFile = new Button(composite_1, SWT.NONE);
                btnStorageFile.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        _selectFolder(m_txtStorageFile);

                    }
                });
                btnStorageFile.setText("...");
                btnStorageFile.setBounds(692, 43, 38, 25);

                m_lblStorageFile = new Label(composite_1, SWT.NONE);
                m_lblStorageFile.setText("Storage File:");
                m_lblStorageFile.setBounds(10, 48, 119, 15);

                Label lblUpdFolder = new Label(composite_1, SWT.NONE);
                lblUpdFolder.setText("WKSP Folder:");
                lblUpdFolder.setBounds(10, 17, 119, 15);
                {
                    m_chkLoadFromStorage = new Button(composite_1, SWT.CHECK);
                    m_chkLoadFromStorage.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                        }
                    });
                    m_chkLoadFromStorage.setLocation(122, 75);
                    m_chkLoadFromStorage.setSize(138, 16);
                    m_chkLoadFromStorage.setSelection(true);
                    m_chkLoadFromStorage.setText("Load From Storage");
                }
                {
                    m_btnCheckWKSP = new Button(composite_1, SWT.NONE);
                    m_btnCheckWKSP.setLocation(120, 109);
                    m_btnCheckWKSP.setSize(90, 25);
                    m_btnCheckWKSP.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            checkWKPS();
                        }
                    });
                    m_btnCheckWKSP.setText("Check WKSP");
                }

            }
        }
        composite.setLayoutData(BorderLayout.NORTH);

        final Label lblX = new Label(composite, SWT.NONE);
        lblX.setBounds(10, 10, 14, 180);
        final CTabFolder m_tabTraces = new CTabFolder(m_BkscoverageShell, SWT.NONE);
        m_tabTraces.setLayoutData(BorderLayout.CENTER);

        m_infoTabItem = new CTabItem(m_tabTraces, SWT.NONE);
        m_infoTabItem.setText("Info");

        final Text txtInfo = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtInfo.setEditable(false);
        m_infoTabItem.setControl(txtInfo);

        m_warnTabItem = new CTabItem(m_tabTraces, SWT.NONE);
        m_warnTabItem.setText("Warning");

        final Text txtWarn = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtWarn.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtWarn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtWarn.setEditable(false);
        m_warnTabItem.setControl(txtWarn);

        m_debugTabItem = new CTabItem(m_tabTraces, SWT.NONE);
        m_debugTabItem.setText("Debug");

        final Text txtDebug = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtDebug.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtDebug.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtDebug.setEditable(false);
        m_debugTabItem.setControl(txtDebug);

        m_errorTabItem = new CTabItem(m_tabTraces, SWT.NONE);
        m_errorTabItem.setText("Error");

        final Text txtError = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        txtError.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtError.setEditable(false);
        m_errorTabItem.setControl(txtError);
    }

    private void _disableButtons() {
        m_btnCheckWKSP.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnCheckWKSP.setEnabled(true);
    }

    private void _executionEnded() {
        _enableButtons();
    }

    private void _executionStarted() {
        Tracer.reset();
        _disableButtons();
    }

    private void _initFields() throws Exception {

        m_tracer = new TabbedTracerImpl(m_debugTabItem, m_infoTabItem, m_warnTabItem, m_errorTabItem);
        Tracer.setTracer(m_tracer);
        m_monitor = new ProgressMonitor();
        m_txtWKSPFolder.setText(s_prefs.getPref("WKSPFolder", ""));
        m_txtStorageFile.setText(s_prefs.getPref("StorageFile", ""));
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_BkscoverageShell.setBounds(x, y, w, h);
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("WKSPFolder", m_txtWKSPFolder.getText());
            s_prefs.setPref("StorageFile", m_txtStorageFile.getText());
            s_prefs.save();
        } catch (Exception ex) {
        }
    }

    private void checkWKPS() {
        _executionStarted();
        CheckWKSPWorker cw = new CheckWKSPWorker(m_monitor);
        cw.checkWKSP(m_txtWKSPFolder.getText(),m_txtStorageFile.getText(),m_chkLoadFromStorage.getSelection());
    }

    private void _selectFolder(Text control) {
        DirectoryDialog dd = new DirectoryDialog(m_BkscoverageShell);
        dd.setFilterPath(control.getText());
        String newValue = dd.open();
        if (newValue != null)
            control.setText(newValue);
    }

}