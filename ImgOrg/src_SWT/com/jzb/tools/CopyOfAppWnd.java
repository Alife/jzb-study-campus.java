/**
 * 
 */
package com.jzb.tools;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.jzb.tools.xmlbean.IActTask;
import com.jzb.tools.xmlbean.IActTaskManager;
import com.jzb.tools.xmlbean.ITracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class CopyOfAppWnd {

    private Button m_chkRetryIfFailed;

    private class Monitor implements IActTaskManager.IProgressMonitor, ITracer {

        /**
         * @see com.jzb.flickr.xmlbean.ITracer#_debug(java.lang.String)
         */
        public void _debug(String msg) {
            _addTraceText(Thread.currentThread().getName(), false, TRC_D, msg);
        }

        /**
         * @see com.jzb.flickr.xmlbean.ITracer#_error(java.lang.String)
         */
        public void _error(String msg) {
            _addTraceText(Thread.currentThread().getName(), true, TRC_E, msg);
        }

        /**
         * @see com.jzb.flickr.xmlbean.ITracer#_error(java.lang.String, java.lang.Throwable)
         */
        public void _error(String msg, Throwable th) {
            StringWriter sw = new StringWriter();
            th.printStackTrace(new PrintWriter(sw));
            _addTraceText(Thread.currentThread().getName(), true, TRC_E, msg);
            _addTraceText(Thread.currentThread().getName(), true, TRC_E, sw.getBuffer().toString());
        }

        /**
         * @see com.jzb.flickr.xmlbean.ITracer#_info(java.lang.String)
         */
        public void _info(String msg) {
            _addTraceText(Thread.currentThread().getName(), false, TRC_I, msg);
        }

        /**
         * @see com.jzb.flickr.xmlbean.ITracer#_warn(java.lang.String)
         */
        public void _warn(String msg) {
            _addTraceText(Thread.currentThread().getName(), true, TRC_W, msg);
        }

        /**
         * @see com.jzb.flickr.xmlbean.ITracer#_warn(java.lang.String, java.lang.Throwable)
         */
        public void _warn(String msg, Throwable th) {
            StringWriter sw = new StringWriter();
            th.printStackTrace(new PrintWriter(sw));
            _addTraceText(Thread.currentThread().getName(), true, TRC_W, msg);
            _addTraceText(Thread.currentThread().getName(), true, TRC_W, sw.getBuffer().toString());
        }

        public void newTaskAdded(IActTask actTask, int queued) {
            m_totalTasksNumber++;

            _updateFields(m_totalTasksNumber, queued, -1);
            // _addTraceText(Thread.currentThread().getName(), false, TRC_D, "New task submitted: " + actTask);
        }

        /**
         * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#processingEnded()
         */
        public void processingEnded(final boolean failed) {
            _addTraceText(Thread.currentThread().getName(), false, TRC_I, "Task processing finished");
            
            Display.getDefault().asyncExec(new Runnable() {
                
                public void run() {
                    if (failed && m_chkRetryIfFailed.getSelection()) {
                        _addTraceText(Thread.currentThread().getName(), false, TRC_I, "\r\n\r\n******* EXECUTION FAILED. IT WILL BE RETRYED *******\r\n\r\n");
                    }
                    _executionEnded(failed);
                }
            });
        }

        /**
         * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#taskEnded(com.jzb.flickr.xmlbean.IActTask, int, int)
         */
        public void taskEnded(IActTask actTask, int msToExecute, int queued) {

            m_totalTasksExecuted++;
            m_totalTasksTime += msToExecute;
            int taskAvgTime = (int) ((double) m_totalTasksTime / (double) m_totalTasksExecuted);

            _updateFields(m_totalTasksNumber, queued, taskAvgTime);
            // _addTraceText(Thread.currentThread().getName(), false, TRC_D, "Task ended: " + actTask);

        }

        public void _addTraceText(final String threadName, final boolean isError, final String level, final String msg) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {

                    Text control = isError ? m_txtError : m_txtDebug;
                    TabItem tabItem = isError ? m_errorTabItem : m_debugTabItem;

                    String txt = control.getText();

                    String fullMsg = level + " " + m_sdf.format(System.currentTimeMillis()) + " " + threadName + "\t- " + msg + "\r\n";
                    txt += fullMsg;

                    control.setText(txt);
                    int lc = control.getLineCount();
                    control.setTopIndex(lc);

                    tabItem.setText((isError ? "Error" : "Debug") + "(" + lc + ")");

                }
            });
        }

        public void _updateFields(final int totalNumber, final int queued, final int avgTime) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    m_lblTaskCreated.setText("" + totalNumber);
                    m_lblTaskQueued.setText("" + queued);
                    if (avgTime >= 0) {
                        m_lblAvgTaskTime.setText(m_df.format(avgTime));
                        long t1 = System.currentTimeMillis() + (queued * avgTime) / FlickrThread.NUM_THREADS;
                        m_lblEstEndingTime.setText(m_sdf.format(t1));
                    }
                }
            });
        }

    }

    private static final String TRC_D = "D";
    private static final String TRC_E = "E";

    private static final String TRC_I = "I";

    private static final String TRC_W = "W";
    protected Shell             shell;
    private Button              m_btnCancel;
    private Button              m_btnStart;
    private TabItem             m_debugTabItem;

    private DecimalFormat       m_df  = new DecimalFormat("0.###");
    private TabItem             m_errorTabItem;
    private FlickrThread        m_flickrThread;
    private Label               m_lblAvgTaskTime;
    private Label               m_lblEndTime;
    private Label               m_lblEstEndingTime;
    private Label               m_lblStartTime;
    private Label               m_lblTaskCreated;
    private Label               m_lblTaskQueued;
    private SimpleDateFormat    m_sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
    private long                m_startTime;

    private int                 m_totalTasksExecuted;
    private int                 m_totalTasksNumber;
    private int                 m_totalTasksTime;
    private Text                m_txtDebug;
    private Text                m_txtDoneTasksFolder;

    private Text                m_txtError;
    private Text                m_txtExamples;

    private Text                m_txtScript;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            CopyOfAppWnd window = new CopyOfAppWnd();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open() {
        final Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();

        _setWndPosition(shell);
        _initFields();
        m_txtDoneTasksFolder.setText("c:\\temp\\DoneTsksLogs");
        m_txtExamples.setText(_loadXmlResource("Examples.xml"));
        m_txtScript.setText(_loadXmlResource("Data.xml"));

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        shell = new Shell();
        shell.setImage(SWTResourceManager.getImage(CopyOfAppWnd.class, "/Publish.ico"));
        shell.setMinimumSize(new Point(800, 400));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(10);
        shell.setLayout(borderLayout);
        shell.setSize(812, 481);
        shell.setText("JZB_Tool");

        final TabFolder tabFolder_1 = new TabFolder(shell, SWT.NONE);
        tabFolder_1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_txtScript != null && tabFolder_1.getSelectionIndex() == 1) {
                    m_txtScript.setFocus();
                }
            }
        });
        tabFolder_1.setLayoutData(BorderLayout.CENTER);

        final TabItem mainTabItem = new TabItem(tabFolder_1, SWT.NONE);
        mainTabItem.setText("Main");

        final Composite composite_1 = new Composite(tabFolder_1, SWT.NONE);
        composite_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        composite_1.setLayout(new BorderLayout(0, 0));
        mainTabItem.setControl(composite_1);

        final TabFolder m_tabTraces = new TabFolder(composite_1, SWT.NONE);
        m_tabTraces.setLayoutData(BorderLayout.CENTER);

        m_debugTabItem = new TabItem(m_tabTraces, SWT.NONE);
        m_debugTabItem.setText("Debug");

        m_txtDebug = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_txtDebug.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        m_txtDebug.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        m_txtDebug.setEditable(false);
        m_debugTabItem.setControl(m_txtDebug);

        m_errorTabItem = new TabItem(m_tabTraces, SWT.NONE);
        m_errorTabItem.setText("Error");

        m_txtError = new Text(m_tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_txtError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        m_txtError.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        m_txtError.setEditable(false);
        m_errorTabItem.setControl(m_txtError);

        final Composite composite = new Composite(composite_1, SWT.BORDER);
        composite.setLayoutData(BorderLayout.NORTH);

        final Group group = new Group(composite, SWT.NONE);
        group.setBounds(10, 35, 758, 143);

        final Label lbl1 = new Label(group, SWT.RIGHT);
        lbl1.setText("Start Time:");
        lbl1.setBounds(10, 23, 130, 16);

        m_lblStartTime = new Label(group, SWT.CENTER | SWT.SHADOW_IN | SWT.BORDER);
        m_lblStartTime.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        m_lblStartTime.setText("00/00/0000 - 00:00:00");
        m_lblStartTime.setBounds(145, 20, 220, 23);

        final Label lbl2 = new Label(group, SWT.RIGHT);
        lbl2.setBounds(385, 23, 130, 16);
        lbl2.setText("End Time:");

        m_lblEndTime = new Label(group, SWT.CENTER | SWT.SHADOW_IN | SWT.BORDER);
        m_lblEndTime.setBounds(521, 20, 220, 23);
        m_lblEndTime.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        m_lblEndTime.setText("00/00/0000 - 00:00:00");

        final Label lbl3 = new Label(group, SWT.RIGHT);
        lbl3.setBounds(10, 65, 130, 16);
        lbl3.setText("Avg. Task Time (ms):");

        m_lblAvgTaskTime = new Label(group, SWT.CENTER | SWT.SHADOW_IN | SWT.BORDER);
        m_lblAvgTaskTime.setBounds(145, 62, 115, 23);
        m_lblAvgTaskTime.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        m_lblAvgTaskTime.setText("999,999");

        final Label lbl4 = new Label(group, SWT.RIGHT);
        lbl4.setBounds(385, 65, 130, 16);
        lbl4.setText("Est. Ending Time:");

        m_lblEstEndingTime = new Label(group, SWT.CENTER | SWT.SHADOW_IN | SWT.BORDER);
        m_lblEstEndingTime.setBounds(521, 62, 220, 23);
        m_lblEstEndingTime.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        m_lblEstEndingTime.setText("00/00/0000 - 00:00:00");

        final Label lbl5 = new Label(group, SWT.RIGHT);
        lbl5.setBounds(10, 108, 130, 16);
        lbl5.setText("Task Created:");

        m_lblTaskCreated = new Label(group, SWT.CENTER | SWT.SHADOW_IN | SWT.BORDER);
        m_lblTaskCreated.setBounds(145, 105, 115, 23);
        m_lblTaskCreated.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        m_lblTaskCreated.setText("99.999.999");

        final Label lbl5_1 = new Label(group, SWT.RIGHT);
        lbl5_1.setBounds(385, 108, 130, 16);
        lbl5_1.setText("Task Queued:");

        m_lblTaskQueued = new Label(group, SWT.CENTER | SWT.SHADOW_IN | SWT.BORDER);
        m_lblTaskQueued.setBounds(520, 105, 115, 23);
        m_lblTaskQueued.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        m_lblTaskQueued.setText("99.999.999");

        m_btnStart = new Button(composite, SWT.NONE);
        m_btnStart.setText("Start");
        m_btnStart.setBounds(10, 190, 63, 26);
        m_btnStart.setEnabled(true);
        m_btnStart.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                _startExecution();
            }
        });

        m_btnCancel = new Button(composite, SWT.NONE);
        m_btnCancel.setText("Cancel");
        m_btnCancel.setBounds(93, 190, 62, 26);
        m_btnCancel.setEnabled(false);
        m_btnCancel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                _cancelExecution();
            }
        });

        final Label lblMaxHeight = new Label(composite, SWT.NONE);
        lblMaxHeight.setBounds(173, 195, 30, 28);

        m_txtDoneTasksFolder = new Text(composite, SWT.BORDER);
        m_txtDoneTasksFolder.setBounds(228, 5, 540, 25);

        final Label lbl0 = new Label(composite, SWT.NONE);
        lbl0.setText("Already Executed Tasks Log Folder:");
        lbl0.setBounds(10, 9, 212, 16);

        m_chkRetryIfFailed = new Button(composite, SWT.CHECK);
        m_chkRetryIfFailed.setText("Retry if failed");
        m_chkRetryIfFailed.setBounds(668, 195, 105, 17);

        final TabItem scriptTabItem = new TabItem(tabFolder_1, SWT.NONE);
        scriptTabItem.setText("Script");

        final Composite composite_3 = new Composite(tabFolder_1, SWT.NONE);
        composite_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        final BorderLayout borderLayout_1 = new BorderLayout(0, 0);
        borderLayout_1.setVgap(5);
        composite_3.setLayout(borderLayout_1);
        scriptTabItem.setControl(composite_3);

        m_txtScript = new Text(composite_3, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_txtScript.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        m_txtScript.setLayoutData(BorderLayout.CENTER);

        final Label typeScriptToLabel = new Label(composite_3, SWT.BORDER);
        typeScriptToLabel.setFont(SWTResourceManager.getFont("Arial", 10, SWT.BOLD));
        typeScriptToLabel.setText("Type Script to execute here:");
        typeScriptToLabel.setLayoutData(BorderLayout.NORTH);

        final TabItem examplesTabItem = new TabItem(tabFolder_1, SWT.NONE);
        examplesTabItem.setText("Examples");

        m_txtExamples = new Text(tabFolder_1, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_txtExamples.setFont(SWTResourceManager.getFont("Courier", 10, SWT.NONE));
        examplesTabItem.setControl(m_txtExamples);

    }

    private void _cancelExecution() {
        m_btnCancel.setEnabled(false);
        m_flickrThread.cancel();
    }

    private void _executionEnded(boolean failed) {
        shell.setText("JZB_Tool [DONE]");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        m_lblEndTime.setText(sdf.format(System.currentTimeMillis()));

        m_btnStart.setEnabled(true);
        m_btnCancel.setEnabled(false);

        if (failed && m_chkRetryIfFailed.getSelection()) {
            _startExecution();
        }
    }

    private void _initFields() {

        shell.setText("JZB_Tool");

        m_lblAvgTaskTime.setText("");
        m_lblEndTime.setText("");
        m_lblEstEndingTime.setText("");
        m_lblStartTime.setText("");
        m_lblTaskCreated.setText("");
        m_lblTaskQueued.setText("");
        m_debugTabItem.setText("Debug");
        m_errorTabItem.setText("Error");
        m_btnCancel.setEnabled(false);
        m_btnStart.setEnabled(true);
        m_txtDebug.setText("");
        m_txtError.setText("");

        m_startTime = 0;
        m_totalTasksExecuted = 0;
        m_totalTasksNumber = 0;
        m_totalTasksTime = 0;
    }

    private String _loadXmlResource(String name) {

        StringBuffer sb = new StringBuffer();
        char buffer[] = new char[4096];

        try {
            InputStream is = Class.class.getResourceAsStream("/" + name);
            InputStreamReader isr = new InputStreamReader(is);
            while (isr.ready()) {
                int l = isr.read(buffer);
                sb.append(buffer, 0, l);
            }
            isr.close();

            return sb.toString();

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            return "Error loading resource '" + name + "' from base folder:\n\n" + sw.getBuffer().toString();
        }
    }

    private void _setWndPosition(Shell shell) {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        shell.setBounds(x, y, w, h);
    }

    private void _startExecution() {

        _initFields();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        m_startTime = System.currentTimeMillis();
        m_lblStartTime.setText(sdf.format(m_startTime));

        m_btnStart.setEnabled(false);
        m_btnCancel.setEnabled(true);

        Monitor mon = new Monitor();
        m_flickrThread = new FlickrThread(m_txtDoneTasksFolder.getText(), mon, mon);
        m_flickrThread.start(m_txtScript.getText());
    }

}
