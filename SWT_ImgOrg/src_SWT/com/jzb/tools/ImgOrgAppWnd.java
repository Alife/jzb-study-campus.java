/**
 * 
 */
package com.jzb.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class ImgOrgAppWnd {

    private Button m_btnSeparateInFolders;

    private class ProgressMonitor implements IProgressMonitor {

        /**
         * @see com.jzb.tools.IProgressMonitor#processingEnded(boolean)
         */
        public void processingEnded(final boolean failed, final Object result) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _executionEnded();
                }
            });
        }

    }

    private static final String APP_NAME       = "ImgOrg";

    private static Preferences  s_prefs        = new Preferences(APP_NAME);

    private Button              m_btnAddDate;

    private Button              m_btnRemoveDate;
    private Button              m_btnRenameAsFolder;
    private Button              m_btnPutTogether;

    private Button              m_btnRenumFiles;
    private Button              m_chkJustCheck;

    private ProgressMonitor     m_monitor;
    private Shell               m_shell;
    private Text                m_txtBaseFolder;
    private TabbedTracerImpl    m_tabbedTracer = new TabbedTracerImpl();

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // En SWT hay que ajustar el tama–o de los fonts en Mac OS
            String OS_Name = System.getProperty("os.name");
            if (OS_Name != null && OS_Name.toLowerCase().contains("mac os")) {
                System.setProperty("org.eclipse.swt.internal.carbon.smallFonts", "true");
            }
            
            s_prefs.load(true);
            ImgOrgAppWnd window = new ImgOrgAppWnd();
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

        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        _updatePrefs();
    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_shell = new Shell();
        m_shell.setImage(SWTResourceManager.getImage(ImgOrgAppWnd.class, "/Tools.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_shell.setLayout(borderLayout);
        m_shell.setSize(800, 400);
        m_shell.setMinimumSize(new Point(800, 400));
        m_shell.setText("ImgOrg");

        final Composite composite_1 = new Composite(m_shell, SWT.NONE);
        composite_1.setLayout(new BorderLayout(0, 0));
        composite_1.setLayoutData(BorderLayout.NORTH);

        Composite composite;
        composite = new Composite(composite_1, SWT.NONE);
        composite.setLayoutData(BorderLayout.NORTH);

        m_txtBaseFolder = new Text(composite, SWT.BORDER);
        m_txtBaseFolder.addDisposeListener(new DisposeListener() {

            public void widgetDisposed(final DisposeEvent e) {
                s_prefs.setPref("baseFolder", m_txtBaseFolder.getText());
            }
        });
        m_txtBaseFolder.setBounds(56, 16, 664, 25);

        final Label folderLabel = new Label(composite, SWT.NONE);
        folderLabel.setText("Folder:");
        folderLabel.setBounds(10, 20, 52, 16);

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
        btnFolder.setBounds(720, 15, 24, 26);

        final Label label = new Label(composite, SWT.NONE);
        label.setBounds(775, 5, 14, 81);

        m_chkJustCheck = new Button(composite, SWT.CHECK | SWT.LEFT);
        m_chkJustCheck.setSelection(true);
        m_chkJustCheck.setText("Just check");
        m_chkJustCheck.setBounds(55, 50, 96, 17);

        final TabFolder tabFolder;
        tabFolder = new TabFolder(composite_1, SWT.NONE);
        tabFolder.setLayoutData(BorderLayout.CENTER);

        final TabItem renumTabItem = new TabItem(tabFolder, SWT.NONE);
        renumTabItem.setText("Renumerate");

        final TabItem renameTabItem = new TabItem(tabFolder, SWT.NONE);
        renameTabItem.setText("Rename As Folder");

        final Composite composite_3 = new Composite(tabFolder, SWT.NONE);
        renameTabItem.setControl(composite_3);

        m_btnRenameAsFolder = new Button(composite_3, SWT.NONE);
        m_btnRenameAsFolder.setText("Rename");
        m_btnRenameAsFolder.setBounds(680, 55, 60, 26);

        final TabItem addDateTabItem = new TabItem(tabFolder, SWT.NONE);
        addDateTabItem.setText("Add Date");

        final TabItem moveToFolderTabItem = new TabItem(tabFolder, SWT.NONE);
        moveToFolderTabItem.setText("Move to folder");

        final Composite composite_5 = new Composite(tabFolder, SWT.NONE);
        moveToFolderTabItem.setControl(composite_5);

        m_btnPutTogether = new Button(composite_5, SWT.NONE);
        m_btnPutTogether.setText("Put Together");
        m_btnPutTogether.setBounds(645, 60, 92, 26);

        final Text txtAdvice2 = new Text(composite_5, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
        txtAdvice2.setText("Separate files in different folders attending to the file's name or put all files from subfolders together in the parent folder.");
        txtAdvice2.setEnabled(false);
        txtAdvice2.setEditable(false);
        txtAdvice2.setBounds(10, 10, 726, 43);

        m_btnSeparateInFolders = new Button(composite_5, SWT.NONE);
        m_btnSeparateInFolders.setBounds(550, 60, 86, 26);
        m_btnSeparateInFolders.setText("Separare");

        final Composite composite_4 = new Composite(tabFolder, SWT.NONE);
        addDateTabItem.setControl(composite_4);

        m_btnRemoveDate = new Button(composite_4, SWT.NONE);
        m_btnRemoveDate.setText("Remove");
        m_btnRemoveDate.setBounds(680, 55, 60, 26);

        final Label timeToBeLabel = new Label(composite_4, SWT.NONE);
        timeToBeLabel.setText("Time offset (could be negative):");
        timeToBeLabel.setBounds(11, 20, 193, 16);

        final Spinner spnYear = new Spinner(composite_4, SWT.BORDER);
        spnYear.setBounds(209, 19, 46, 19);
        spnYear.setMinimum(-23);
        spnYear.setMaximum(23);

        final Spinner spnMoth = new Spinner(composite_4, SWT.BORDER);
        spnMoth.setBounds(275, 19, 46, 19);
        spnMoth.setMinimum(-59);
        spnMoth.setMaximum(59);

        final Spinner spnDay = new Spinner(composite_4, SWT.BORDER);
        spnDay.setBounds(340, 19, 46, 19);
        spnDay.setMinimum(-59);
        spnDay.setMaximum(59);

        final Spinner spnHour = new Spinner(composite_4, SWT.BORDER);
        spnHour.setMinimum(-23);
        spnHour.setMaximum(23);
        spnHour.setBounds(455, 19, 46, 19);

        final Spinner spnMin = new Spinner(composite_4, SWT.BORDER);
        spnMin.setMinimum(-59);
        spnMin.setMaximum(59);
        spnMin.setBounds(521, 19, 46, 19);

        final Spinner spnSec = new Spinner(composite_4, SWT.BORDER);
        spnSec.setMinimum(-59);
        spnSec.setMaximum(59);
        spnSec.setBounds(586, 19, 46, 19);

        m_btnAddDate = new Button(composite_4, SWT.NONE);
        m_btnAddDate.setBounds(604, 55, 60, 26);
        m_btnAddDate.setText("Add Date");

        final Composite composite_2 = new Composite(tabFolder, SWT.NONE);
        renumTabItem.setControl(composite_2);

        final Text txtIniCount = new Text(composite_2, SWT.BORDER);
        txtIniCount.setText("00000");
        txtIniCount.setBounds(85, 15, 62, 25);

        final Label initialCountLabel = new Label(composite_2, SWT.NONE);
        initialCountLabel.setText("Initial Count:");
        initialCountLabel.setBounds(10, 19, 81, 16);

        final Text txtFolderFilter = new Text(composite_2, SWT.BORDER);
        txtFolderFilter.setText("Filtradas_NO");
        txtFolderFilter.setBounds(419, 15, 210, 25);

        final Label filterLabel = new Label(composite_2, SWT.NONE);
        filterLabel.setText("Folder filter:");
        filterLabel.setBounds(345, 19, 81, 16);

        final Label label_1 = new Label(composite_2, SWT.NONE);
        label_1.setBounds(775, 0, 10, 91);

        m_btnRenumFiles = new Button(composite_2, SWT.NONE);
        m_btnRenumFiles.setText("Renum");
        m_btnRenumFiles.setBounds(680, 55, 60, 26);

        final Button chkToBeExcluded = new Button(composite_2, SWT.CHECK);
        chkToBeExcluded.setSelection(true);
        chkToBeExcluded.setText("To be excluded");
        chkToBeExcluded.setBounds(635, 19, 122, 17);

        final Button chkGroupedByName = new Button(composite_2, SWT.CHECK);
        chkGroupedByName.setSelection(true);
        chkGroupedByName.setText("Grouped by name");
        chkGroupedByName.setBounds(155, 10, 129, 17);

        final Button chkResetByFolder = new Button(composite_2, SWT.CHECK);
        chkResetByFolder.setBounds(155, 28, 129, 17);
        chkResetByFolder.setSelection(true);
        chkResetByFolder.setText("Reset by folder");

        final Composite composite_6 = new Composite(m_shell, SWT.NONE);
        composite_6.setLayout(new BorderLayout(0, 0));
        composite_6.setLayoutData(BorderLayout.CENTER);

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(composite_6);
        tabTraces.setLayoutData(BorderLayout.CENTER);
        Tracer.setTracer(m_tabbedTracer);

        m_btnRenameAsFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _renameAsFolder(m_txtBaseFolder.getText());
            }
        });

        m_btnRenumFiles.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _renumImgs(m_txtBaseFolder.getText(), Integer.parseInt(txtIniCount.getText()), chkResetByFolder.getSelection(), chkGroupedByName.getSelection(), txtFolderFilter.getText(), chkToBeExcluded.getSelection());
            }
        });


        m_btnRemoveDate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _removeDateFromFiles(m_txtBaseFolder.getText());
            }
        });

        m_btnAddDate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _addDateToFiles(m_txtBaseFolder.getText(), spnYear.getSelection(), spnMoth.getSelection(), spnDay.getSelection(), spnHour.getSelection(), spnMin.getSelection(), spnSec.getSelection());
            }
        });

        m_btnSeparateInFolders.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _separateInFolders(m_txtBaseFolder.getText());
            }
        });

        m_btnPutTogether.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _putFilesTogether(m_txtBaseFolder.getText());
            }
        });

        final Label dLabel = new Label(composite_4, SWT.NONE);
        dLabel.setBounds(391, 20, 17, 16);
        dLabel.setText("D");

        final Label jLabel = new Label(composite_4, SWT.CENTER);
        jLabel.setText("--");
        jLabel.setBounds(410, 20, 29, 16);

        final Label yLabel = new Label(composite_4, SWT.NONE);
        yLabel.setBounds(260, 20, 23, 16);
        yLabel.setText("Y");

        final Label mLabel_1 = new Label(composite_4, SWT.NONE);
        mLabel_1.setBounds(325, 20, 23, 16);
        mLabel_1.setText("M");

        final Label mLabel = new Label(composite_4, SWT.NONE);
        mLabel.setText("M");
        mLabel.setBounds(571, 20, 17, 16);

        final Label hLabel = new Label(composite_4, SWT.NONE);
        hLabel.setText("H");
        hLabel.setBounds(506, 20, 17, 16);

        final Label sLabel = new Label(composite_4, SWT.NONE);
        sLabel.setText("S");
        sLabel.setBounds(637, 20, 17, 16);

    }

    private void _addDateToFiles(final String baseFolderStr, final int years, final int months, final int days, final int hours, final int mins, final int secs) {
        Tracer.reset();
        _disableButtons();
        DateAdderWorker daw = new DateAdderWorker(m_chkJustCheck.getSelection(), m_monitor);
        daw.addDateToFiles(baseFolderStr, years, months, days, hours, mins, secs);
    }

    private void _separateInFolders(final String baseFolderStr) {
        Tracer.reset();
        _disableButtons();
        MoveToFolderWorker mtfw = new MoveToFolderWorker(m_chkJustCheck.getSelection(), m_monitor);
        mtfw.separateInFolders(baseFolderStr);
    }

    private void _putFilesTogether(final String baseFolderStr) {
        Tracer.reset();
        _disableButtons();
        MoveToFolderWorker mtfw = new MoveToFolderWorker(m_chkJustCheck.getSelection(), m_monitor);
        mtfw.putFilesTogether(baseFolderStr);
    }

    private void _disableButtons() {
        m_btnAddDate.setEnabled(false);
        m_btnRemoveDate.setEnabled(false);
        m_btnRenumFiles.setEnabled(false);
        m_btnRenameAsFolder.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnAddDate.setEnabled(true);
        m_btnRemoveDate.setEnabled(true);
        m_btnRenumFiles.setEnabled(true);
        m_btnRenameAsFolder.setEnabled(true);
    }

    private void _executionEnded() {
        _enableButtons();
    }

    private void _initFields() throws Exception {

        m_monitor = new ProgressMonitor();

        m_txtBaseFolder.setText(s_prefs.getPref("baseFolder", ""));

    }

    private void _removeDateFromFiles(final String baseFolderStr) {
        Tracer.reset();
        _disableButtons();
        DateAdderWorker daw = new DateAdderWorker(m_chkJustCheck.getSelection(), m_monitor);
        daw.removeDateFromFiles(baseFolderStr);
    }

    private void _renameAsFolder(final String baseFolderStr) {
        Tracer.reset();
        _disableButtons();
        RenameAsFolderWorker rw = new RenameAsFolderWorker(m_chkJustCheck.getSelection(), m_monitor);
        rw.rename(baseFolderStr);
    }

    private void _renumImgs(final String baseFolderStr, final int baseCounter, final boolean resetByFolder, final boolean groupedByName, final String folderFilter, final boolean toBeExcluded) {
        Tracer.reset();
        _disableButtons();
        RenumWorker rw = new RenumWorker(m_chkJustCheck.getSelection(), m_monitor);
        rw.renum(baseFolderStr, baseCounter, resetByFolder, groupedByName, folderFilter, toBeExcluded);
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
    }
}