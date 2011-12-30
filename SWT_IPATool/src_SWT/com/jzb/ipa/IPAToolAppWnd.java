/**
 * 
 */
package com.jzb.ipa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.jzb.ipa.InstallBundlesWorker.T_BundleInstallInfo;
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
public class IPAToolAppWnd {

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

    private static final String APP_NAME = "IPATool";

    private static AppPreferences  s_prefs  = new AppPreferences(APP_NAME);
    private Button              m_btnCheckAll;

    private Button              m_btnCheckNewBundles;
    private Button              m_btnInstall;
    private Button              m_btnLoad;
    private Button              m_btnProcess;
    private Button              m_btnReadBundles;
    private Button              m_btnReport;
    private Button              m_btnSave;
    private Button              m_btnUncheckAll;
    private Button              m_chkJustCheck;
    private Composite           m_compBundleList;

    private CTabItem             m_debugTabItem;
    private CTabItem             m_errorTabItem;
    private CTabItem             m_infoTabItem;
    private ProgressMonitor     m_monitor;
    private Shell               m_shell;
    private CTabItem             m_tabBundles;
    private TabbedTracerImpl    m_tracer;

    private Text                m_txtBaseFolder;
    private CTabItem             m_warnTabItem;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            IPAToolAppWnd window = new IPAToolAppWnd();
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
        m_shell.open();
        m_shell.layout();

        _setWndPosition();
        _initFields();

        m_shell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _updatePrefs();
                System.exit(0);
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

        m_shell.setImage(SWTResourceManager.getImage(IPAToolAppWnd.class, "/Properties.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_shell.setLayout(borderLayout);
        m_shell.setSize(800, 400);
        m_shell.setMinimumSize(new Point(800, 400));
        m_shell.setText("IPATool");

        Composite composite;
        composite = new Composite(m_shell, SWT.NONE);

        m_chkJustCheck = new Button(composite, SWT.CHECK | SWT.LEFT);
        m_chkJustCheck.setSelection(false);
        m_chkJustCheck.setText("Just check");
        m_chkJustCheck.setBounds(10, 20, 96, 17);
        {
            CTabFolder tabFolder = new CTabFolder(composite, SWT.NONE);
            tabFolder.setBounds(10, 48, 580, 115);
            {
                CTabItem tbtmProcess = new CTabItem(tabFolder, SWT.NONE);
                tbtmProcess.setText("Process");

                Composite composite_2 = new Composite(tabFolder, SWT.NONE);
                tbtmProcess.setControl(composite_2);

                final Button chkExtractImage = new Button(composite_2, SWT.CHECK);
                chkExtractImage.setBounds(155, 10, 109, 17);
                chkExtractImage.setSelection(true);
                chkExtractImage.setText("Extract Image");

                final Button chkRecurseFolders = new Button(composite_2, SWT.CHECK);
                chkRecurseFolders.setBounds(10, 10, 120, 17);
                chkRecurseFolders.setSelection(true);
                chkRecurseFolders.setText("Recurse Folders");

                m_btnProcess = new Button(composite_2, SWT.NONE);
                m_btnProcess.setBounds(10, 47, 110, 26);
                m_btnProcess.setText("Process");
                m_btnProcess.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        processFolder(m_txtBaseFolder.getText(), chkRecurseFolders.getSelection(), chkExtractImage.getSelection());
                    }
                });

                m_btnReport = new Button(composite_2, SWT.NONE);
                m_btnReport.setBounds(136, 47, 110, 26);
                m_btnReport.setText("HTML Report");
                m_btnReport.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        HTMLReport(m_txtBaseFolder.getText(), chkRecurseFolders.getSelection());
                    }
                });

                m_btnCheckNewBundles = new Button(composite_2, SWT.NONE);
                m_btnCheckNewBundles.setBounds(262, 47, 110, 26);
                m_btnCheckNewBundles.setText("Check Repeated");
                m_btnCheckNewBundles.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        checkNewBundles(m_txtBaseFolder.getText());
                    }
                });
            }

            {
                CTabItem tbtmInstall = new CTabItem(tabFolder, SWT.NONE);
                tbtmInstall.setText("Install");

                Composite composite_3 = new Composite(tabFolder, SWT.NONE);
                tbtmInstall.setControl(composite_3);

                m_btnReadBundles = new Button(composite_3, SWT.NONE);
                m_btnReadBundles.setBounds(10, 50, 110, 26);
                m_btnReadBundles.setText("Read Bundles");
                m_btnReadBundles.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        readBundles(m_txtBaseFolder.getText(), m_compBundleList);
                    }
                });

                m_btnInstall = new Button(composite_3, SWT.NONE);
                m_btnInstall.setBounds(134, 50, 110, 26);
                m_btnInstall.setText("Install");
                {
                    Button btnCheckSelected = new Button(composite_3, SWT.NONE);
                    btnCheckSelected.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            checkBundlesSelected(m_txtBaseFolder.getText());
                        }
                    });
                    btnCheckSelected.setBounds(260, 50, 110, 26);
                    btnCheckSelected.setText("Check Selected");
                }
                m_btnInstall.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        installBundles(m_compBundleList);
                    }
                });

            }
        }
        composite.setLayoutData(BorderLayout.NORTH);

        m_txtBaseFolder = new Text(composite, SWT.BORDER);
        m_txtBaseFolder.setBounds(157, 17, 405, 25);

        final Label folderLabel = new Label(composite, SWT.NONE);
        folderLabel.setText("Folder:");
        folderLabel.setBounds(112, 20, 52, 16);

        final Button btnFolder = new Button(composite, SWT.NONE);
        btnFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                DirectoryDialog dd = new DirectoryDialog(m_shell);
                dd.setFilterPath(m_txtBaseFolder.getText());
                String newValue = dd.open();
                if (newValue != null)
                    m_txtBaseFolder.setText(newValue);
            }
        });
        btnFolder.setText("...");
        btnFolder.setBounds(566, 15, 24, 26);

        final Label lblX = new Label(composite, SWT.NONE);
        lblX.setBounds(10, 10, 14, 166);
        final CTabFolder m_tabTraces = new CTabFolder(m_shell, SWT.NONE);
        m_tabTraces.setLayoutData(BorderLayout.CENTER);

        m_debugTabItem = new CTabItem(m_tabTraces, SWT.NONE);
        m_debugTabItem.setText("Debug");

        final Text txtDebug = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtDebug.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtDebug.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtDebug.setEditable(false);
        m_debugTabItem.setControl(txtDebug);

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

        m_errorTabItem = new CTabItem(m_tabTraces, SWT.NONE);
        m_errorTabItem.setText("Error");

        final Text txtError = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        txtError.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtError.setEditable(false);
        m_errorTabItem.setControl(txtError);
        
                m_tabBundles = new CTabItem(m_tabTraces, SWT.NONE);
                m_tabBundles.setText("Bundles");
                
                        ScrolledComposite scrolledComposite = new ScrolledComposite(m_tabTraces, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
                        m_tabBundles.setControl(scrolledComposite);
                        scrolledComposite.setAlwaysShowScrollBars(true);
                        scrolledComposite.setExpandHorizontal(true);
                        scrolledComposite.setExpandVertical(true);
                        
                                m_compBundleList = new Composite(scrolledComposite, SWT.NONE);
                                {
                                    m_btnCheckAll = new Button(m_compBundleList, SWT.NONE);
                                    m_btnCheckAll.addSelectionListener(new SelectionAdapter() {

                                        @Override
                                        public void widgetSelected(SelectionEvent e) {
                                            _checkAll(true);
                                        }
                                    });
                                    m_btnCheckAll.setBounds(10, 10, 77, 26);
                                    m_btnCheckAll.setText("Check All");
                                }
                                {
                                    m_btnUncheckAll = new Button(m_compBundleList, SWT.NONE);
                                    m_btnUncheckAll.addSelectionListener(new SelectionAdapter() {

                                        @Override
                                        public void widgetSelected(SelectionEvent e) {
                                            _checkAll(false);
                                        }
                                    });
                                    m_btnUncheckAll.setBounds(92, 10, 77, 26);
                                    m_btnUncheckAll.setText("Uncheck All");
                                }
                                {
                                    m_btnSave = new Button(m_compBundleList, SWT.NONE);
                                    m_btnSave.addSelectionListener(new SelectionAdapter() {

                                        @Override
                                        public void widgetSelected(SelectionEvent e) {
                                            _saveStatus();
                                        }
                                    });
                                    m_btnSave.setBounds(218, 10, 77, 26);
                                    m_btnSave.setText("Save");
                                }
                                {
                                    m_btnLoad = new Button(m_compBundleList, SWT.NONE);
                                    m_btnLoad.addSelectionListener(new SelectionAdapter() {

                                        @Override
                                        public void widgetSelected(SelectionEvent e) {
                                            _loadStatus();
                                        }
                                    });
                                    m_btnLoad.setBounds(301, 10, 77, 26);
                                    m_btnLoad.setText("Load");
                                }
                                scrolledComposite.setContent(m_compBundleList);
                                scrolledComposite.setMinSize(m_compBundleList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void _checkAll(final boolean selected) {

        Control clist[] = m_compBundleList.getChildren();
        if (clist != null) {
            for (Control c : clist) {
                if (c instanceof Button) {
                    Button btn = (Button) c;
                    if ((btn.getStyle() & SWT.CHECK) != 0) {
                        btn.setSelection(selected);
                    }
                }
            }
        }
    }

    private void _disableButtons() {
        m_btnInstall.setEnabled(false);
        m_btnReadBundles.setEnabled(false);
        m_btnCheckAll.setEnabled(false);
        m_btnUncheckAll.setEnabled(false);
        m_btnSave.setEnabled(false);
        m_btnLoad.setEnabled(false);
        m_btnProcess.setEnabled(false);
        m_btnReport.setEnabled(false);
        m_btnCheckNewBundles.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnProcess.setEnabled(true);
        m_btnReport.setEnabled(true);
        m_btnCheckNewBundles.setEnabled(true);
        m_btnInstall.setEnabled(true);
        m_btnReadBundles.setEnabled(true);
        m_btnCheckAll.setEnabled(true);
        m_btnUncheckAll.setEnabled(true);
        m_btnSave.setEnabled(true);
        m_btnLoad.setEnabled(true);
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

        m_txtBaseFolder.setText(s_prefs.getPref("baseFolder", ""));

    }

    private void _loadStatus() {

        _disableButtons();

        try {
            File baseFolder = new File(m_txtBaseFolder.getText());
            File stFile = new File(baseFolder, "checkStatus.txt");
            Tracer._debug("Loading checks status from text file: " + stFile);

            HashSet<String> files = new HashSet<String>();
            BufferedReader br = new BufferedReader(new FileReader(stFile));
            while (br.ready()) {
                String line = br.readLine();
                files.add(line);
            }
            br.close();

            Control clist[] = m_compBundleList.getChildren();
            if (clist != null) {
                for (Control c : clist) {
                    if (c instanceof Button) {
                        Button btn = (Button) c;
                        if ((btn.getStyle() & SWT.CHECK) != 0) {
                            String fileName = btn.getData().toString();
                            btn.setSelection(files.contains(fileName));
                        }
                    }
                }
            }

            Tracer._debug("Checks status loaded");
        } catch (Throwable th) {
            Tracer._error("Error loading checks status", th);
        }

        _enableButtons();
    }

    private void _saveStatus() {

        _disableButtons();

        try {
            File baseFolder = new File(m_txtBaseFolder.getText());
            File stFile = new File(baseFolder, "checkStatus.txt");
            Tracer._debug("Saving checks status in text file: " + stFile);

            PrintStream ps = new PrintStream(stFile);
            Control clist[] = m_compBundleList.getChildren();
            if (clist != null) {
                for (Control c : clist) {
                    if (c instanceof Button) {
                        Button btn = (Button) c;
                        if ((btn.getStyle() & SWT.CHECK) != 0 && btn.getSelection()) {
                            ps.println(btn.getData());
                        }
                    }
                }
            }
            ps.close();

            Tracer._debug("Checks status saved");
        } catch (Throwable th) {
            Tracer._error("Error saving checks status", th);
        }

        _enableButtons();
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_shell.setBounds(x, y, w, h);
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("baseFolder", m_txtBaseFolder.getText());
            s_prefs.save();
        } catch (Exception ex) {
        }
    }

    private void checkNewBundles(final String baseFolderStr) {
        _executionStarted();
        CheckNewBundlesWorker cnbw = new CheckNewBundlesWorker(m_chkJustCheck.getSelection(), m_monitor);
        cnbw.check(baseFolderStr);
    }

    private void HTMLReport(final String baseFolderStr, final boolean recurseFolders) {
        _executionStarted();
        ReportWorker rw = new ReportWorker(m_chkJustCheck.getSelection(), m_monitor);
        rw.createReport(baseFolderStr, recurseFolders);
    }

    private void installBundles(final Composite compBundleList) {

        final ArrayList<T_BundleInstallInfo> chkList = new ArrayList<T_BundleInstallInfo>();

        Control clist[] = compBundleList.getChildren();
        if (clist != null) {
            for (Control c : clist) {
                if (c instanceof Button) {
                    Button btn = (Button) c;
                    if ((btn.getStyle() & SWT.CHECK) != 0 && btn.getSelection()) {
                        T_BundleInstallInfo ipaInfo = new T_BundleInstallInfo();
                        ipaInfo.ipaFile = (File) btn.getData();
                        ipaInfo.chkFeedback = btn;
                        chkList.add(ipaInfo);
                        if (chkList.size() >= 400) {
                            MessageBox mb = new MessageBox(m_shell, SWT.ICON_WARNING | SWT.OK);
                            mb.setText("IPA installation");
                            mb.setMessage("Just a maximum of 400 Bundles will be installed");
                            mb.open();
                            break;
                        }
                    }
                }
            }
        }

        // Exit if there aren't any checkbox selected
        if (chkList.size() <= 0)
            return;

        _executionStarted();
        InstallBundlesWorker ibw = new InstallBundlesWorker(m_chkJustCheck.getSelection(), m_monitor);
        ibw.installBundles(chkList);
    }

    private void processFolder(final String baseFolderStr, final boolean recurseFolders, final boolean extractImage) {
        _executionStarted();
        ProcessBundlesWorker pbw = new ProcessBundlesWorker(m_chkJustCheck.getSelection(), m_monitor);
        pbw.processFolder(baseFolderStr, recurseFolders, extractImage);
    }

    private void readBundles(final String baseFolderStr, final Composite compBundleList) {
        _executionStarted();
        ReadBundlesWorker rbw = new ReadBundlesWorker(m_chkJustCheck.getSelection(), m_monitor);
        rbw.readBundles(baseFolderStr, compBundleList);
    }
    
    private void checkBundlesSelected(final String baseFolderStr) {
        _executionStarted();
        CheckBundlesSelectedWorker cbsw = new CheckBundlesSelectedWorker(m_chkJustCheck.getSelection(), m_monitor);
        cbsw.check(baseFolderStr);
    }
}