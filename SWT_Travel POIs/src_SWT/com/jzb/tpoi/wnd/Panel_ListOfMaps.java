/**
 * 
 */
package com.jzb.tpoi.wnd;

import java.util.ArrayList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
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
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.srvc.ModelService;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class Panel_ListOfMaps extends Composite {

    private ArrayList<TMap> m_maps;
    private Table           m_mapsTable;
    private IMapPanelOwner  m_panelOwner;

    /**
     * Create the composite
     * 
     * @param parent
     * @param style
     */
    public Panel_ListOfMaps(IMapPanelOwner panelOwner, Composite parent, int style) {
        super(parent, style);
        m_panelOwner = panelOwner;
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        setLayout(borderLayout);

        final Composite composite_1 = new Composite(this, SWT.NONE);
        composite_1.setLayoutData(BorderLayout.NORTH);

        final Button btnAdd = new Button(composite_1, SWT.NONE);
        btnAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _addMap();
            }
        });
        btnAdd.setText("+");
        btnAdd.setBounds(5, 0, 25, 25);

        final Button btnDel = new Button(composite_1, SWT.NONE);
        btnDel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_mapsTable.getSelection().length > 0) {
                    _delMap();
                }
            }
        });
        btnDel.setText("-");
        btnDel.setBounds(30, 0, 25, 25);

        final Button btnEdit = new Button(composite_1, SWT.NONE);
        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_mapsTable.getSelection().length > 0) {
                    _editMap();
                }
            }
        });
        btnEdit.setText("...");
        btnEdit.setBounds(60, 0, 25, 25);

        final Button ppButton = new Button(composite_1, SWT.NONE);
        ppButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_mapsTable.getSelection().length > 0) {
                    _navigateTo();
                }
            }
        });
        ppButton.setText("->");
        ppButton.setBounds(90, 0, 25, 25);

        final Label mapsLabel = new Label(composite_1, SWT.NONE);
        mapsLabel.setText("User Map List [Points/Categories]");
        mapsLabel.setBounds(125, 5, 198, 15);

        final Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(new BorderLayout(0, 0));

        final TableViewer tableViewer = new TableViewer(composite, SWT.BORDER);
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(final DoubleClickEvent arg0) {
                if (m_mapsTable.getSelection().length > 0) {
                    _navigateTo();
                }
            }
        });
        m_mapsTable = tableViewer.getTable();
        m_mapsTable.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(final ControlEvent e) {
                m_mapsTable.getColumn(0).setWidth(m_mapsTable.getSize().x);
            }
        });
        m_mapsTable.setLayoutData(BorderLayout.CENTER);
        m_mapsTable.setLinesVisible(true);
        m_mapsTable.setHeaderVisible(false);

        final TableColumn newColumnTableColumn = new TableColumn(m_mapsTable, SWT.NONE);
        newColumnTableColumn.setWidth(200);
        newColumnTableColumn.setText("New column");
        //
    }

    /**
     * @param maps
     *            the maps to set
     */
    public void setMaps(ArrayList<TMap> maps) {
        m_maps = maps;
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                _refreshAllTableElements();
            }
        });
    }

    protected void _addCellForMap(TMap map) {
        TableItem item = new TableItem(m_mapsTable, SWT.NONE);
        item.setData(map);
        item.setText(_getCellName(map));
        // item.setImage(image);
    }

    protected void _addMap() {
        TMap map = new TMap();
        Dlg_BaseElementEditor dlg = new Dlg_BaseElementEditor(this.getShell());
        dlg.setEditingInfo(map, true, null);
        if (dlg.open() == 0) {
            try {
                map.touchAsUpdated();
                ModelService.inst.createMap(map);
                m_maps.add(map);
                _refreshAllTableElements();
            } catch (Throwable th) {
                _errorMsg("Error adding new map:\n    " + th.getMessage());
            }
        }
    }

    protected void _delMap() {
        TMap map = (TMap) m_mapsTable.getSelection()[0].getData();
        if (_areYouSure(map)) {
            try {
                ModelService.inst.markAsDeletedMap(map);
                m_maps.remove(map);
                _refreshAllTableElements();
            } catch (Throwable th) {
                _errorMsg("Error deleting map:\n    " + th.getMessage());
            }
        }
    }

    protected void _editMap() {
        TMap map = (TMap) m_mapsTable.getSelection()[0].getData();
        Dlg_BaseElementEditor dlg = new Dlg_BaseElementEditor(this.getShell());
        dlg.setEditingInfo(map, false, null);
        if (dlg.open() == 0) {
            try {
                map.touchAsUpdated();
                ModelService.inst.updateMap(map);
                _refreshAllTableElements();
            } catch (Throwable th) {
                _errorMsg("Error updating map:\n    " + th.getMessage());
            }
        }
    }

    protected void _navigateTo() {
        TMap map = (TMap) m_mapsTable.getSelection()[0].getData();
        map.toXmlString();
        m_panelOwner.navigateForward(map);
    }

    protected void _refreshAllTableElements() {

        ViewModelUtil.shortCollection(m_maps, BaseEntityComparationType.name);

        m_mapsTable.removeAll();

        for (final TMap map : m_maps) {
            _addCellForMap(map);
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
    }

    private String _getCellName(TMap map) {
        // Local or Remote map
        return map.getDisplayName() + (map.isLocal() ? " [L]" : " [R]");
    }

}
