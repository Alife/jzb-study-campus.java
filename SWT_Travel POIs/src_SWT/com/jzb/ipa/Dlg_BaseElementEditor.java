/**
 * 
 */
package com.jzb.ipa;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.jzb.tpoi.data.BaseEntityComparationType;
import com.jzb.tpoi.data.BaseEntityCompatator;
import com.jzb.tpoi.data.TBaseEntity;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TIcon;
import com.jzb.tpoi.data.TMapElement;
import com.jzb.tpoi.data.TPoint;

/**
 * @author n63636
 * 
 */
public class Dlg_BaseElementEditor extends Dialog {

    private TCategory   m_currentCat;
    private TBaseEntity m_entity;
    private boolean     m_isNew;
    private Table       m_tblCategories;
    private Table       m_tblPOIs;
    private Text        m_txtDescription;
    private Text        m_txtIcon;
    private Text        m_txtName;
    private Text        m_txtShortName;

    /**
     * Create the dialog
     * 
     * @param parentShell
     */
    public Dlg_BaseElementEditor(Shell parentShell) {
        super(parentShell);
    }

    /**
     * @param entity
     *            the entity to set
     */
    public void setEditingInfo(TBaseEntity entity, boolean isNew, TCategory currentCat) {
        m_entity = entity;
        m_isNew = isNew;
        m_currentCat = currentCat;
    }

    protected void _initFields() {
        if (m_entity.getName() != null)
            m_txtName.setText(m_entity.getName());
        if (m_entity.getShortName() != null)
            m_txtShortName.setText(m_entity.getShortName());
        if (m_entity.getDescription() != null)
            m_txtDescription.setText(m_entity.getDescription());
        if (m_entity.getIcon() != null) {
            m_txtIcon.setText(m_entity.getIcon().getName());
        }

        if (m_entity instanceof TMapElement) {
            _initFieldTableForMapElement();
        }

        if (m_entity instanceof TCategory) {
            _initFieldTableForCategory();
        }
    }

    protected void _setFields() {

        m_entity.setName(m_txtName.getText());
        m_entity.setShortName(m_txtShortName.getText());
        m_entity.setDescription(m_txtDescription.getText());
        if (m_txtIcon.getText() != null && m_txtIcon.getText().trim().length() > 0) {
            m_entity.setIcon(TIcon.createFromName(m_txtIcon.getText()));
        }

        if (m_entity instanceof TMapElement) {
            _setFieldTableForMapElement();
        }

        if (m_entity instanceof TCategory) {
            _setFieldTableForCategory();
        }

    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (m_txtName.getText().trim().length() == 0) {
                MessageBox msgBox = new MessageBox(getShell(), SWT.ERROR);
                msgBox.setText("Error in value");
                msgBox.setMessage("Field 'name' cannot be empty");
                msgBox.open();
                return;
            } else {
                _setFields();
            }
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Element editor: [" + m_entity.getType() + " - " + m_entity.getDisplayName() + "]");
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

        final Label lblName = new Label(container, SWT.NONE);
        lblName.setText("Name:");

        m_txtName = new Text(container, SWT.BORDER);
        final GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_txtName.setLayoutData(gd_txtName);

        final Label lblShortName = new Label(container, SWT.NONE);
        lblShortName.setText("Short Name:");

        m_txtShortName = new Text(container, SWT.BORDER);
        final GridData gd_txtShortName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_txtShortName.setLayoutData(gd_txtShortName);

        final Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setText("Description:");

        m_txtDescription = new Text(container, SWT.MULTI | SWT.BORDER);
        final GridData gd_txtDescription = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_txtDescription.heightHint = 60;
        m_txtDescription.setLayoutData(gd_txtDescription);

        final Label lblIcon = new Label(container, SWT.NONE);
        lblIcon.setText("Icon:");

        m_txtIcon = new Text(container, SWT.BORDER);
        final GridData gd_txtIcon = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_txtIcon.setLayoutData(gd_txtIcon);

        final Label lblCategories = new Label(container, SWT.NONE);
        lblCategories.setText("Belongs to\ncategories:");

        m_tblCategories = new Table(container, SWT.CHECK | SWT.MULTI | SWT.BORDER);
        m_tblCategories.setLinesVisible(true);
        final GridData gd_tblCategories = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_tblCategories.heightHint = 200;
        m_tblCategories.setLayoutData(gd_tblCategories);

        final TableColumn colCategories = new TableColumn(m_tblCategories, SWT.NONE);
        colCategories.setWidth(300);
        colCategories.setText("Categories");

        final Label lblPOIs = new Label(container, SWT.NONE);
        lblPOIs.setText("POIs\ncategorized:");

        m_tblPOIs = new Table(container, SWT.CHECK | SWT.MULTI | SWT.BORDER);
        m_tblPOIs.setLinesVisible(true);
        m_tblPOIs.setHeaderVisible(false);
        final GridData gd_tblPOIS = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_tblPOIS.heightHint = 200;
        m_tblPOIs.setLayoutData(gd_tblPOIS);

        final TableColumn colPOIs = new TableColumn(m_tblPOIs, SWT.NONE);
        colPOIs.setWidth(300);
        colPOIs.setText("POIs:");

        _initFields();

        //
        return container;
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(500, 440);
    }

    private void _initFieldTableForCategory() {

        TCategory cat = (TCategory) m_entity;

        m_tblPOIs.removeAll();

        ArrayList<TPoint> points = new ArrayList<TPoint>(cat.getOwnerMap().getAllPoints());
        BaseEntityCompatator comparator = new BaseEntityCompatator(BaseEntityComparationType.name);
        Collections.sort(points, comparator);

        for (TPoint point : points) {

            TableItem item = new TableItem(m_tblPOIs, SWT.NONE);
            item.setData(point);
            item.setText(point.getDisplayName());
            // item.setImage(image);

            if (cat.getPointById(point.getId()) != null) {
                item.setChecked(true);
            }
        }
    }

    private void _initFieldTableForMapElement() {

        TMapElement me = (TMapElement) m_entity;
        boolean isACategory = (me instanceof TCategory);
        String meID = me.getId();

        m_tblCategories.removeAll();

        ArrayList<TCategory> categories = new ArrayList<TCategory>(me.getOwnerMap().getAllCategories());
        BaseEntityCompatator comparator = new BaseEntityCompatator(BaseEntityComparationType.name);
        Collections.sort(categories, comparator);

        for (TCategory cat : categories) {

            if (isACategory) {
                TCategory meAsCat = (TCategory) me;
                // NO se puede asignar a si mismo
                // y no debe estarlo por no crear ciclos
                if (meAsCat.equals(cat) || meAsCat.containsCategoryById(cat.getId(), true)) {
                    continue;
                }
            }

            TableItem item = new TableItem(m_tblCategories, SWT.NONE);
            item.setData(cat);
            item.setText(cat.getDisplayName());
            // item.setImage(image);

            boolean defCat = m_isNew && cat.equals(m_currentCat);
            boolean contained = isACategory ? cat.containsCategoryById(meID) : cat.containsPointById(meID);

            if (defCat || contained) {
                item.setChecked(true);
            } else {
            }

        }
    }

    private void _setFieldTableForCategory() {

        TCategory cat = (TCategory) m_entity;
        for (TableItem item : m_tblPOIs.getItems()) {
            TPoint point = (TPoint) item.getData();
            if (item.getChecked()) {
                cat.addPoint(point);
            } else {
                cat.deletePoint(point);
            }
        }
    }

    private void _setFieldTableForMapElement() {

        for (TableItem item : m_tblCategories.getItems()) {
            TCategory cat = (TCategory) item.getData();
            if (item.getChecked()) {
                if (m_entity instanceof TCategory) {
                    cat.addCategory((TCategory) m_entity);
                } else {
                    cat.addPoint((TPoint) m_entity);
                }
            } else {
                if (m_entity instanceof TCategory) {
                    cat.deleteCategory((TCategory) m_entity);
                } else {
                    cat.deletePoint((TPoint) m_entity);
                }
            }
        }
    }
}
