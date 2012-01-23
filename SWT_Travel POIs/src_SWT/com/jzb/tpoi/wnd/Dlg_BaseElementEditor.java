/**
 * 
 */
package com.jzb.tpoi.wnd;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import com.jzb.tpoi.data.TCoordinates;
import com.jzb.tpoi.data.TIcon;
import com.jzb.tpoi.data.TMapElement;
import com.jzb.tpoi.data.TPoint;

/**
 * @author n63636
 * 
 */
public class Dlg_BaseElementEditor extends Dialog {

    private Button      m_Button;
    private TCategory   m_currentCat;
    private TBaseEntity m_entity;
    private Label       m_IdLabel;
    private boolean     m_isNew;
    private Label       m_LblCategories;
    private Label       m_LblDescription;
    private Label       m_LblIcon;
    private Label       m_LblLat;
    private Label       m_LblLng;
    private Label       m_LblName;
    private Label       m_LblPOIs;
    private Label       m_LblShortName;
    private Shell       m_parentShell;
    private Table       m_tblCategories;
    private Table       m_tblPOIs;
    private Text        m_txtDescription;
    private Text        m_txtIcon;
    private Text        m_txtID;
    private Text        m_txtLat;
    private Text        m_txtLng;
    private Text        m_txtName;
    private Text        m_txtShortName;

    /**
     * Create the dialog
     * 
     * @param parentShell
     */
    public Dlg_BaseElementEditor(Shell parentShell) {
        super(parentShell);
        m_parentShell = parentShell;
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
        m_txtID.setText(m_entity.getId());
        if (m_entity.getName() != null)
            m_txtName.setText(m_entity.getName());
        if (m_entity.getShortName() != null)
            m_txtShortName.setText(m_entity.getShortName());
        if (m_entity.getDescription() != null)
            m_txtDescription.setText(m_entity.getDescription());
        if (m_entity.getIcon() != null) {
            m_txtIcon.setText(m_entity.getIcon().getUrl());
        }

        if (m_entity instanceof TPoint) {
            TPoint point = (TPoint) m_entity;
            if (point.getCoordinates() != null) {
                m_txtLat.setText("" + point.getCoordinates().getLat());
                m_txtLng.setText("" + point.getCoordinates().getLng());
            }
        }

        if (m_entity instanceof TMapElement) {
            _initFieldTableForBoth();
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
            m_entity.setIcon(TIcon.createFromURL(m_txtIcon.getText()));
        }

        if (m_entity instanceof TPoint) {
            try {
                String val = m_txtLat.getText() + "," + m_txtLng.getText() + ", 0.0";
                TCoordinates coord = new TCoordinates(val);
                TPoint point = (TPoint) m_entity;
                point.setCoordinates(coord);
            } catch (Throwable th) {
            }
        }

        if (m_entity instanceof TMapElement) {
            _setFieldTableForBoth();
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
        gridLayout.numColumns = 4;
        container.setLayout(gridLayout);

        m_IdLabel = new Label(container, SWT.NONE);
        m_IdLabel.setText("ID:");

        m_txtID = new Text(container, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_txtID = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        m_txtID.setLayoutData(gd_txtID);

        m_LblName = new Label(container, SWT.NONE);
        m_LblName.setText("Name:");

        m_txtName = new Text(container, SWT.BORDER);
        final GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        m_txtName.setLayoutData(gd_txtName);

        m_LblShortName = new Label(container, SWT.NONE);
        m_LblShortName.setText("Short Name:");

        m_txtShortName = new Text(container, SWT.BORDER);
        final GridData gd_txtShortName = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        m_txtShortName.setLayoutData(gd_txtShortName);

        m_LblDescription = new Label(container, SWT.NONE);
        m_LblDescription.setText("Description:");

        m_txtDescription = new Text(container, SWT.MULTI | SWT.BORDER);
        final GridData gd_txtDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        gd_txtDescription.heightHint = 60;
        m_txtDescription.setLayoutData(gd_txtDescription);

        m_LblLat = new Label(container, SWT.NONE);
        m_LblLat.setText("Lat:");

        m_txtLat = new Text(container, SWT.BORDER);
        final GridData gd_txtLat = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_txtLat.setLayoutData(gd_txtLat);

        m_LblLng = new Label(container, SWT.NONE);
        m_LblLng.setText("Lng:");

        m_txtLng = new Text(container, SWT.BORDER);
        final GridData gd_txtLng = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_txtLng.setLayoutData(gd_txtLng);

        m_LblIcon = new Label(container, SWT.NONE);
        m_LblIcon.setText("Icon:");

        m_txtIcon = new Text(container, SWT.BORDER);
        final GridData gd_txtIcon = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        m_txtIcon.setLayoutData(gd_txtIcon);

        m_Button = new Button(container, SWT.NONE);
        m_Button.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(final SelectionEvent e) {
                Dlg_IconEditor dlg = new Dlg_IconEditor(m_parentShell);
                dlg.setEditingInfo(m_txtIcon.getText());
                if (dlg.open() == 0) {
                    m_txtIcon.setText(dlg.getSelIconURL());
                }
            }
        });
        m_Button.setText("...");

        m_LblCategories = new Label(container, SWT.NONE);
        m_LblCategories.setText("Belongs to\ncategories:");

        m_tblCategories = new Table(container, SWT.CHECK | SWT.MULTI | SWT.BORDER);
        m_tblCategories.setLinesVisible(true);
        final GridData gd_tblCategories = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gd_tblCategories.heightHint = 200;
        m_tblCategories.setLayoutData(gd_tblCategories);

        final TableColumn colCategories = new TableColumn(m_tblCategories, SWT.NONE);
        colCategories.setWidth(300);
        colCategories.setText("Categories");

        m_LblPOIs = new Label(container, SWT.NONE);
        m_LblPOIs.setText("POIs\ncategorized:");

        m_tblPOIs = new Table(container, SWT.CHECK | SWT.MULTI | SWT.BORDER);
        m_tblPOIs.setLinesVisible(true);
        m_tblPOIs.setHeaderVisible(false);
        final GridData gd_tblPOIS = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gd_tblPOIS.heightHint = 200;
        m_tblPOIs.setLayoutData(gd_tblPOIS);

        final TableColumn colPOIs = new TableColumn(m_tblPOIs, SWT.NONE);
        colPOIs.setWidth(300);
        colPOIs.setText("POIs:");
        container.setTabList(new Control[] { m_LblName, m_txtName, m_LblShortName, m_txtShortName, m_LblDescription, m_txtDescription, m_LblLat, m_txtLat, m_LblLng, m_txtLng, m_LblIcon, m_txtIcon,
                m_Button, m_LblCategories, m_tblCategories, m_LblPOIs, m_tblPOIs, m_IdLabel, m_txtID });

        _initFields();
        m_txtName.setFocus();

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

    private void _initFieldTableForBoth() {

        boolean isACategory = (m_entity instanceof TCategory);
        TCategory meAsCat = null;
        TPoint meAsPoint = null;

        if (isACategory) {
            meAsCat = (TCategory) m_entity;
        } else {
            meAsPoint = (TPoint) m_entity;
        }

        m_tblCategories.removeAll();

        ArrayList<TCategory> categories = new ArrayList<TCategory>(((TMapElement) m_entity).getOwnerMap().getCategories().values());
        BaseEntityCompatator comparator = new BaseEntityCompatator(BaseEntityComparationType.name);
        Collections.sort(categories, comparator);

        for (TCategory cat : categories) {

            if (isACategory) {
                // NO se puede asignar a si mismo
                // y no debe estarlo por no crear ciclos
                if (meAsCat.equals(cat) || meAsCat.recursiveContainsSubCategory(cat)) {
                    continue;
                }
            }

            TableItem item = new TableItem(m_tblCategories, SWT.NONE);
            item.setData(cat);
            item.setText(cat.getDisplayName());
            // item.setImage(image);

            boolean defCat = m_isNew && cat.equals(m_currentCat);
            boolean contained = isACategory ? cat.getSubCategories().contains(meAsCat) : cat.getPoints().contains(meAsPoint);

            if (defCat || contained) {
                item.setChecked(true);
            } else {
            }

        }
    }

    private void _initFieldTableForCategory() {

        TCategory cat = (TCategory) m_entity;

        m_tblPOIs.removeAll();

        ArrayList<TPoint> points = new ArrayList<TPoint>(cat.getOwnerMap().getPoints().values());
        BaseEntityCompatator comparator = new BaseEntityCompatator(BaseEntityComparationType.name);
        Collections.sort(points, comparator);

        for (TPoint point : points) {

            TableItem item = new TableItem(m_tblPOIs, SWT.NONE);
            item.setData(point);
            item.setText(point.getDisplayName());
            // item.setImage(image);

            if (cat.getPoints().getById(point.getId()) != null) {
                item.setChecked(true);
            }
        }
    }

    private void _setFieldTableForBoth() {

        for (TableItem item : m_tblCategories.getItems()) {

            TCategory cat = (TCategory) item.getData();

            boolean modified;
            if (item.getChecked()) {
                if (m_entity instanceof TCategory) {
                    modified = cat.getSubCategories().add((TCategory) m_entity);
                } else {
                    modified = cat.getPoints().add((TPoint) m_entity);
                }
            } else {
                if (m_entity instanceof TCategory) {
                    modified = cat.getSubCategories().remove((TCategory) m_entity);
                } else {
                    modified = cat.getPoints().remove((TPoint) m_entity);
                }
            }

            if (modified) {
                cat.touchAsUpdated();
                m_entity.touchAsUpdated();
            }
        }
    }

    private void _setFieldTableForCategory() {

        TCategory cat = (TCategory) m_entity;

        for (TableItem item : m_tblPOIs.getItems()) {

            TPoint point = (TPoint) item.getData();

            boolean modified;
            if (item.getChecked()) {
                modified = cat.getPoints().add(point);
            } else {
                modified = cat.getPoints().remove(point);
            }

            if (modified) {
                cat.touchAsUpdated();
                point.touchAsUpdated();
            }

        }
    }
}
