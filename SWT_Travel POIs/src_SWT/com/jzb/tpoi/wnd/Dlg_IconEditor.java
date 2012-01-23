/**
 * 
 */
package com.jzb.tpoi.wnd;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.jzb.tpoi.data.TIcon;

/**
 * @author n63636
 * 
 */
public class Dlg_IconEditor extends Dialog {

    private String m_iconURL;
    private String m_selIconURL;
    private Table  m_tblIcons;
    private Text   m_txtURL;

    /**
     * Create the dialog
     * 
     * @param parentShell
     */
    public Dlg_IconEditor(Shell parentShell) {
        super(parentShell);
    }

    /**
     * @return the selIconURL
     */
    public String getSelIconURL() {
        return m_selIconURL;
    }

    /**
     * @param entity
     *            the entity to set
     */
    public void setEditingInfo(String iconURL) {
        m_iconURL = iconURL;
        m_selIconURL = null;
    }

    protected void _initFields() {

        if (m_iconURL != null) {
            m_txtURL.setText(m_iconURL);
        }

        for (TIcon icon : TIcon.allIcons()) {
            TableItem item = new TableItem(m_tblIcons, SWT.NONE);
            item.setData(icon);
            item.setText(icon.getName());
            item.setImage(icon.getImage());
            if (m_iconURL != null && icon.getUrl().equals(m_iconURL)) {
                m_tblIcons.setSelection(item);
                m_tblIcons.showSelection();
            }
        }
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            // chequeo
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
    }

    /**
     * Create contents of the button bar
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Create contents of the dialog
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        final Label idLabel = new Label(container, SWT.NONE);
        idLabel.setText("URL:");

        m_txtURL = new Text(container, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_txtURL = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_txtURL.setLayoutData(gd_txtURL);

        final Label lblIcons = new Label(container, SWT.NONE);
        lblIcons.setLayoutData(new GridData());
        lblIcons.setText("Icons:");

        m_tblIcons = new Table(container, SWT.BORDER);
        m_tblIcons.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(final SelectionEvent e) {
                m_selIconURL = ((TIcon) ((TableItem) e.item).getData()).getUrl();
                m_txtURL.setText(m_selIconURL);
            }
        });
        m_tblIcons.setLinesVisible(true);
        m_tblIcons.setHeaderVisible(true);
        final GridData gd_tblIcons = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_tblIcons.setLayoutData(gd_tblIcons);

        _initFields();

        //
        return container;
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(500, 470);
    }
}
