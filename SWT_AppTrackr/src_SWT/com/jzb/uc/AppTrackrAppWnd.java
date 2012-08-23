/**
 * 
 */
package com.jzb.uc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
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
import org.eclipse.swt.widgets.Group;
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
public class AppTrackrAppWnd {

    private Button    m_chkbtnDbgTraces;
    private TabFolder m_TabFolder;

    private class ProgressMonitor implements IProgressMonitor {

        /**
         * @see com.jzb.swt.util2.tools.IProgressMonitor#processingEnded(boolean)
         */
        public void processingEnded(final boolean failed, final Object result) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _executionEnded();
                    if (!failed) {
                        m_Browser.setText((String) result);
                        m_TabFolder.setSelection(1);
                    }
                }
            });
        }

    }

    private static final String   APP_NAME       = "AppTrackr";

    private static AppPreferences s_prefs        = new AppPreferences(APP_NAME);

    private Button                m_btnCheck;
    private ProgressMonitor       m_monitor;
    private Shell                 m_ApptrkrShell;
    private Text                  m_txtIPAsFolder;
    private Browser               m_Browser;

    private TabbedTracerImpl      m_tabbedTracer = new TabbedTracerImpl();

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
            AppTrackrAppWnd window = new AppTrackrAppWnd();
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
        m_ApptrkrShell.open();
        m_ApptrkrShell.layout();

        _setWndPosition();
        _initFields();

        m_ApptrkrShell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _updatePrefs();
                System.exit(0);
            }
        });

        while (!m_ApptrkrShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_ApptrkrShell = new Shell();

        m_ApptrkrShell.setImage(SWTResourceManager.getImage(AppTrackrAppWnd.class, "/Properties.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_ApptrkrShell.setLayout(borderLayout);
        m_ApptrkrShell.setSize(800, 400);
        m_ApptrkrShell.setMinimumSize(new Point(800, 400));
        m_ApptrkrShell.setText("AppTrkr");
        {
            m_TabFolder = new TabFolder(m_ApptrkrShell, SWT.NONE);

            {
                TabItem tbtmTools = new TabItem(m_TabFolder, SWT.NONE);
                tbtmTools.setText("Tools");

                Composite composite_1 = new Composite(m_TabFolder, SWT.NONE);
                composite_1.setLayout(new BorderLayout(0, 0));
                tbtmTools.setControl(composite_1);

                final Group group1 = new Group(composite_1, SWT.NONE);
                group1.setLayoutData(BorderLayout.NORTH);

                m_txtIPAsFolder = new Text(group1, SWT.BORDER);
                m_txtIPAsFolder.setBounds(122, 14, 564, 21);
                m_txtIPAsFolder.setText("");

                Button btnUpdFolder = new Button(group1, SWT.NONE);
                btnUpdFolder.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        _selectFolder(m_txtIPAsFolder);

                    }
                });
                btnUpdFolder.setText("...");
                btnUpdFolder.setBounds(692, 12, 38, 25);

                Label lblIPAsFolder = new Label(group1, SWT.NONE);
                lblIPAsFolder.setText(" IPAs Folder:");
                lblIPAsFolder.setBounds(10, 17, 119, 15);

                m_btnCheck = new Button(group1, SWT.NONE);
                m_btnCheck.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        checkUpdates();
                    }
                });
                m_btnCheck.setBounds(122, 49, 90, 25);
                m_btnCheck.setText("Check Updates");

                m_chkbtnDbgTraces = new Button(group1, SWT.CHECK);
                m_chkbtnDbgTraces.setText("Debugging traces");
                m_chkbtnDbgTraces.setBounds(227, 53, 127, 16);

                //------------------------------------------------------------------------
                // ********** TabbedTracer ***********************************************
                final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(composite_1);
                tabTraces.setLayoutData(BorderLayout.CENTER);
                Tracer.setTracer(m_tabbedTracer);

            }

            TabItem tbtmBrowser = new TabItem(m_TabFolder, SWT.NONE);
            tbtmBrowser.setText("Browser");

            Composite composite_2 = new Composite(m_TabFolder, SWT.NONE);
            composite_2.setLayout(new BorderLayout(0, 0));
            tbtmBrowser.setControl(composite_2);

            m_Browser = new Browser(composite_2, SWT.NONE);

        }

    }

    private void _disableButtons() {
        m_btnCheck.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnCheck.setEnabled(true);
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
        m_txtIPAsFolder.setText(s_prefs.getPref("IPAsFolder", ""));
    }

    private void _selectFolder(Text control) {
        DirectoryDialog dd = new DirectoryDialog(m_ApptrkrShell);
        dd.setFilterPath(control.getText());
        String newValue = dd.open();
        if (newValue != null)
            control.setText(newValue);
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_ApptrkrShell.setBounds(x, y, w, h);
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("IPAsFolder", m_txtIPAsFolder.getText());

            s_prefs.save();
        } catch (Exception ex) {
        }
    }

    private void checkUpdates() {
        _executionStarted();
        CheckerWorker cw = new CheckerWorker(m_monitor);
        cw.checkUpdates(m_txtIPAsFolder.getText(), m_chkbtnDbgTraces.getSelection());
    }
}