/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.Renumerate;
import com.jzb.img.tsk.Renumerate.ResetByFolder;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author jzarzuela
 * 
 */
public class RenumerateUI extends BaseUI {

    private Button m_chkResetByFolder;
    private Text   m_txtCounter;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public RenumerateUI(Composite parent, int style) {

        super(parent, style);

        Label lblCounter = new Label(this, SWT.NONE);
        lblCounter.setBounds(10, 19, 90, 14);
        lblCounter.setText("Initial index:");

        m_txtCounter = new Text(this, SWT.BORDER);
        m_txtCounter.setText("10");
        m_txtCounter.setBounds(100, 17, 60, 19);

        Label lblResetCounterBy = new Label(this, SWT.NONE);
        lblResetCounterBy.setBounds(10, 44, 90, 14);
        lblResetCounterBy.setText("Reset by folder:");

        m_chkResetByFolder = new Button(this, SWT.CHECK);
        m_chkResetByFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        m_chkResetByFolder.setBounds(100, 42, 64, 18);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTask();
            }
        });
        btnSplit.setText("Renumerate");
        btnSplit.setBounds(166, 12, 94, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p>Assigns a sequential index number to each file following their alphabetical order. Files are given a Compound File Name.</p>";
        description += "<table><tr><td><b>Initial Counter:</b></td><td>Establishes the initial index to be given.</td></tr>";
        description += "<tr><td><b>Reset by folder:</b></td><td>If checked, index is reset to the initial value for every subfolder.</td></tr></table>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Renumerate";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTask() {

        final ResetByFolder reset = m_chkResetByFolder.getSelection() ? ResetByFolder.YES : ResetByFolder.NO;
        final int counter = _parseInt(m_txtCounter.getText());

        final Renumerate task = new Renumerate(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.renumerate(counter, reset);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
}
