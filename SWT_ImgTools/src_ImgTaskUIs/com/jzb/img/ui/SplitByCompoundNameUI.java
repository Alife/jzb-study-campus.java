/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.SplitByCompoundName;

/**
 * @author jzarzuela
 * 
 */
public class SplitByCompoundNameUI extends BaseUI {

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public SplitByCompoundNameUI(Composite parent, int style) {

        super(parent, style);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTask();
            }
        });
        btnSplit.setText("Split");
        btnSplit.setBounds(10, 10, 94, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    public String getTaskName() {
        return "Split by CompoundName";
    }

    // --------------------------------------------------------------------------------------------------------
    public String getTaskDescription() {
        String description = "";
        description += "<p>Splits files moving them into subfolders named after the parts of the compound name.</p>";
        description += "<p><b>Note:</b><i> No recursive processing is done and files without a compound name won't be moved.</i></p>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTask() {

        final SplitByCompoundName task = new SplitByCompoundName(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.splitByCompoundName();
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

}
