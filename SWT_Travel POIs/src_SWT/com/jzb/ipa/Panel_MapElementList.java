/**
 * 
 */
package com.jzb.ipa;

import java.util.ArrayList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import swing2swt.layout.BorderLayout;

import com.jzb.tpoi.data.BaseEntityComparationType;
import com.jzb.tpoi.data.TBaseEntity;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TMapElement;
import com.jzb.tpoi.data.TPoint;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class Panel_MapElementList extends Composite {

    private Button                    m_btnFlat;
    private ArrayList<TCategory>      m_categoryNavStack;
    private TCategory                 m_currentCat;
    private Table                     m_elementsTable;
    private Label                     m_lblTitle;
    private TMap                      m_map;
    private IMapPanelOwner            m_panelOwner;
    private BaseEntityComparationType m_sortingType = BaseEntityComparationType.categoryAndName;

    /**
     * Create the composite
     * 
     * @param parent
     * @param style
     */
    public Panel_MapElementList(IMapPanelOwner panelOwner, Composite parent, int style) {
        super(parent, style);
        m_panelOwner = panelOwner;
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        setLayout(borderLayout);

        final Composite composite_1 = new Composite(this, SWT.NONE);
        composite_1.setLayout(new FormLayout());
        composite_1.setLayoutData(BorderLayout.NORTH);

        final Button btnAddPOI = new Button(composite_1, SWT.NONE);
        btnAddPOI.setText("+");
        final FormData fd_btnAddPOI = new FormData();
        fd_btnAddPOI.top = new FormAttachment(0, 0);
        fd_btnAddPOI.left = new FormAttachment(0, 0);
        btnAddPOI.setLayoutData(fd_btnAddPOI);
        btnAddPOI.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _addPOI();
            }
        });

        final Button btnDel = new Button(composite_1, SWT.NONE);
        btnDel.setText("-");
        final FormData fd_btnDel = new FormData();
        fd_btnDel.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_btnDel.left = new FormAttachment(btnAddPOI, 0);
        btnDel.setLayoutData(fd_btnDel);
        btnDel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_elementsTable.getSelection().length > 0) {
                    _delElement();
                }
            }
        });

        final Button btnAddCat = new Button(composite_1, SWT.NONE);
        btnAddCat.setText("[+]");
        final FormData fd_btnAddCat = new FormData();
        fd_btnAddCat.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_btnAddCat.left = new FormAttachment(btnDel, 5);
        btnAddCat.setLayoutData(fd_btnAddCat);
        btnAddCat.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _addCat();
            }
        });

        final Button btnEdit = new Button(composite_1, SWT.NONE);
        btnEdit.setText("...");
        final FormData fd_btnEdit = new FormData();
        fd_btnEdit.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_btnEdit.left = new FormAttachment(btnAddCat, 5);
        btnEdit.setLayoutData(fd_btnEdit);
        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_elementsTable.getSelection().length > 0) {
                    _editElement();
                }
            }
        });

        final Button btnNavigate = new Button(composite_1, SWT.NONE);
        btnNavigate.setText("->");
        final FormData fd_btnNavigate = new FormData();
        fd_btnNavigate.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_btnNavigate.left = new FormAttachment(btnEdit, 5);
        btnNavigate.setLayoutData(fd_btnNavigate);
        btnNavigate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_elementsTable.getSelection().length > 0) {
                    _navigateTo();
                }
            }
        });

        m_lblTitle = new Label(composite_1, SWT.BORDER);
        m_lblTitle.setText("<Entity Name: ''>");
        final FormData fd_lblTitle = new FormData();
        fd_lblTitle.right = new FormAttachment(0, 310);
        fd_lblTitle.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_lblTitle.left = new FormAttachment(btnNavigate, 10);
        m_lblTitle.setLayoutData(fd_lblTitle);

        m_btnFlat = new Button(composite_1, SWT.CHECK);
        m_btnFlat.setText("Flat");
        final FormData fd_btnFlat = new FormData();
        fd_btnFlat.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_btnFlat.left = new FormAttachment(m_lblTitle, 10);
        m_btnFlat.setLayoutData(fd_btnFlat);
        m_btnFlat.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _refreshAllTableElements();
            }
        });

        final Combo cmbSorting = new Combo(composite_1, SWT.READ_ONLY);
        cmbSorting.select(0);
        String texts[] = new String[BaseEntityComparationType.values().length];
        for (int n = 0; n < BaseEntityComparationType.values().length; n++) {
            texts[n] = BaseEntityComparationType.values()[n].toString();
        }
        cmbSorting.setItems(texts);
        final FormData fd_combo = new FormData();
        fd_combo.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_combo.left = new FormAttachment(m_btnFlat, 5);
        cmbSorting.setLayoutData(fd_combo);
        cmbSorting.setText(BaseEntityComparationType.categoryAndName.toString());
        cmbSorting.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                int index = cmbSorting.getSelectionIndex();
                if (index == 0) {
                    m_sortingType = BaseEntityComparationType.categoryAndName;
                } else {
                    m_sortingType = BaseEntityComparationType.values()[index];
                }
                _refreshAllTableElements();
            }
        });

        final Button btnBack = new Button(composite_1, SWT.NONE);
        btnBack.setText("<<<");
        final FormData fd_btnBack = new FormData();
        fd_btnBack.top = new FormAttachment(btnAddPOI, 0, SWT.CENTER);
        fd_btnBack.left = new FormAttachment(cmbSorting, 5);
        btnBack.setLayoutData(fd_btnBack);
        btnBack.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _navigateBack();
            }
        });

        final Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(new BorderLayout(0, 0));

        final TableViewer tableViewer = new TableViewer(composite, SWT.BORDER);
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(final DoubleClickEvent arg0) {
                if (m_elementsTable.getSelection().length > 0) {
                    _navigateTo();
                }
            }
        });
        m_elementsTable = tableViewer.getTable();
        m_elementsTable.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(final ControlEvent e) {
                m_elementsTable.getColumn(0).setWidth(m_elementsTable.getSize().x);
            }
        });
        m_elementsTable.setLayoutData(BorderLayout.CENTER);
        m_elementsTable.setLinesVisible(true);
        m_elementsTable.setHeaderVisible(false);

        final TableColumn newColumnTableColumn = new TableColumn(m_elementsTable, SWT.NONE);
        newColumnTableColumn.setResizable(false);
        newColumnTableColumn.setWidth(200);
        newColumnTableColumn.setText("New column");
        //
    }

    /**
     * @param map
     *            the map to set
     */
    public void setFilteringCategories(TMap activeMap, ArrayList<TCategory> categoryNavStack) {

        m_map = activeMap;
        m_categoryNavStack = categoryNavStack;
        if (m_categoryNavStack != null && m_categoryNavStack.size() > 0) {
            m_currentCat = m_categoryNavStack.get(m_categoryNavStack.size() - 1);
        } else {
            m_currentCat = null;
        }

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (m_currentCat == null) {
                    m_lblTitle.setText("Map: '" + m_map.getDisplayName() + "'");
                } else {
                    String title = "Category: ";
                    boolean isFirst = true;
                    for (TCategory cat : m_categoryNavStack) {
                        if (!isFirst) {
                            title += "|";
                        }
                        title += cat.getDisplayName();
                        isFirst = false;
                    }
                    m_lblTitle.setText(title);
                }
                _refreshAllTableElements();
            }
        });
    }

    protected void _addCat() {
        TCategory cat = new TCategory(m_map);
        Dlg_BaseElementEditor dlg = new Dlg_BaseElementEditor(this.getShell());
        dlg.setEditingInfo(cat, true, m_currentCat);
        if (dlg.open() == 0) {
            try {
                m_map.addCategory(cat);
                ModelService.inst.updateMap(m_map);
                _refreshAllTableElements();
            } catch (Throwable th) {
                _errorMsg("Error adding new map:\n    " + th.getMessage());
            }
        }
    }

    protected void _addCellForElement(TMapElement element) {
        TableItem item = new TableItem(m_elementsTable, SWT.NONE);
        item.setData(element);
        item.setText(_getCellName(element));
        // item.setImage(image);
    }

    protected void _addPOI() {

        TPoint point = new TPoint(m_map);

        Dlg_BaseElementEditor dlg = new Dlg_BaseElementEditor(this.getShell());
        dlg.setEditingInfo(point, true, m_currentCat);
        if (dlg.open() == 0) {
            try {
                m_map.addPoint(point);
                ModelService.inst.updateMap(m_map);
                _refreshAllTableElements();
            } catch (Throwable th) {
                _errorMsg("Error adding new map:\n    " + th.getMessage());
            }
        }
    }

    protected void _delElement() {
        TMapElement elem = (TMapElement) m_elementsTable.getSelection()[0].getData();
        if (_areYouSure(elem)) {
            try {
                if (elem instanceof TCategory) {
                    m_map.deleteCategory((TCategory) elem);
                } else {
                    m_map.deletePoint((TPoint) elem);
                }
                ModelService.inst.updateMap(m_map);
                _refreshAllTableElements();
            } catch (Throwable th) {
                _errorMsg("Error deleting map:\n    " + th.getMessage());
            }
        }
    }

    protected void _editElement() {
        TMapElement elem = (TMapElement) m_elementsTable.getSelection()[0].getData();
        Dlg_BaseElementEditor dlg = new Dlg_BaseElementEditor(this.getShell());
        dlg.setEditingInfo(elem, false, m_currentCat);
        if (dlg.open() == 0) {
            try {
                ModelService.inst.updateMap(m_map);
                _refreshAllTableElements();
            } catch (Throwable th) {
                _errorMsg("Error updating map:\n    " + th.getMessage());
            }
        }
    }

    protected void _navigateBack() {
        m_panelOwner.navigateBackward();
    }

    protected void _navigateTo() {
        Object obj = m_elementsTable.getSelection()[0].getData();
        if (obj instanceof TCategory) {
            m_panelOwner.navigateForward((TCategory) obj);
        }
    }

    protected void _refreshAllTableElements() {

        m_elementsTable.removeAll();
        ArrayList<TMapElement> elements;
        if (m_btnFlat.getSelection()) {
            elements = ViewModelUtil.getFlatContent(m_map, m_categoryNavStack);
        } else {
            elements = ViewModelUtil.getHierarchicalContent(m_map, m_categoryNavStack);
        }

        ViewModelUtil.shortCollection(elements, m_sortingType);

        for (final TMapElement elem : elements) {
            _addCellForElement(elem);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    private boolean _areYouSure(TBaseEntity ent) {
        MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
        msgBox.setText("Delete element");
        msgBox.setMessage("are you sure you want to delete '" + ent.getDisplayName() + "' ?");
        return msgBox.open() == SWT.YES;
    }

    private void _errorMsg(String errStr) {
        MessageBox msgBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
        msgBox.setText("Error info");
        msgBox.setMessage(errStr);
        msgBox.open();
        Tracer._error("Error info: " + errStr);
    }

    private String _getCellName(TBaseEntity item) {
        if (item instanceof TCategory) {
            return " * " + item.getDisplayName() + " [" + ((TCategory) item).getDisplayCount() + "]";
        } else {
            return item.getDisplayName();
        }
    }

}
