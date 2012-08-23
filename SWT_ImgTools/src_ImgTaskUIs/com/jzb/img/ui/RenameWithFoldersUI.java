/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.RenameWithFolders;

/**
 * @author jzarzuela
 * 
 */
public class RenameWithFoldersUI extends BaseUI {

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public RenameWithFoldersUI(Composite parent, int style) {

        super(parent, style);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTask();
            }
        });
        btnSplit.setText("Rename");
        btnSplit.setBounds(10, 10, 94, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    public String getTaskName() {
        return "Rename with subfolder";
    }

    // --------------------------------------------------------------------------------------------------------
    public String getTaskDescription() {
        String description = "";
        description += "<p>Renames image files using folder's names as Group and Subgroup Names</p>";
        description += "<p><b><font color='red'>Warning:</font></b><i> Current compound name parts will be replaced with folders' information. Just 'Name' part will remain unchanged.</i></p>";
        description += "<p><b>Note:</b><i> Subgroup Names will start (optional) with a folder which name starts with '" + RenameWithFolders.SUBGROUP_MARKER + "'.</i></p>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTask() {

        final RenameWithFolders task = new RenameWithFolders(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.renameAsSubfolder();
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
}
