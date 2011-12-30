package com.jzb.ser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;

public class MenuItem {

    public static final String PARENT_NAME = "..";
    public static final String ROOT_NAME   = "root";

    private String             m_alias;
    private boolean            m_isFile;
    private Vector             m_items     = new Vector();
    private MenuItem           m_parent;

    private int                m_selected;
    private int                m_top;

    private int                m_fileRecordIndex;

    private static MenuItem    PARENT_MENU = new MenuItem(PARENT_NAME, false);

    public static MenuItem createRoot() {
        return new MenuItem(MenuItem.ROOT_NAME, false);
    }

    private MenuItem() {
    }

    public MenuItem(String alias, boolean isFile) {
        m_alias = alias;
        m_isFile = isFile;
    }

    private void setParent(MenuItem parent) {
        m_parent = parent;
    }

    public MenuItem getParent() {
        return m_parent;
    }

    public void addChild(MenuItem item) {
        item.setParent(this);
        item.m_items.insertElementAt(this, 0);
        m_items.addElement(item);
    }

    public String getAlias() {
        return m_alias;
    }

    public void setAlias(String alias) {
        m_alias = alias;
    }

    public String getFullName() {

        Vector parents = new Vector();
        MenuItem root = this;
        while (root.m_parent != null) {
            parents.insertElementAt(root.getAlias(), 0);
            root = root.m_parent;
        }

        String fn = "";
        for (int n = 0; n < parents.size(); n++) {
            if (n != 0)
                fn += ".";
            fn += parents.elementAt(n);
        }
        return fn;
    }

    public MenuItem getRootParent() {
        MenuItem root = this;
        while (root.m_parent != null) {
            root = root.m_parent;
        }
        return root;
    }

    public MenuItem getChild(int index) {
        return (MenuItem) m_items.elementAt(index);
    }

    public MenuItem getSelectedChild() {
        return (MenuItem) m_items.elementAt(m_selected);
    }

    public MenuItem getChild(String alias) {
        for (int n = 0; n < m_items.size(); n++) {
            MenuItem item = (MenuItem) m_items.elementAt(n);
            if (item.getAlias().equals(alias))
                return item;
        }
        return null;
    }

    public int getNumItems() {
        return m_items.size();
    }

    public boolean isFile() {
        return m_isFile;
    }

    public boolean isSubMenu() {
        return !m_isFile && !m_alias.equals(ROOT_NAME);
    }

    public int getSelected() {
        return m_selected;
    }

    public void setSelected(int selected) {
        m_selected = selected;
    }

    public int getTop() {
        return m_top;
    }

    public void setTop(int top) {
        m_top = top;
    }

    public String toString() {
        return m_alias + ", " + m_isFile;
    }

    public void writeExternal(DataOutputStream dos) throws Exception {
        dos.writeUTF(m_alias);
        dos.writeBoolean(m_isFile);
        dos.writeInt(m_fileRecordIndex);
        if (!m_isFile) {
            dos.writeInt(m_items.size());
            for (int n = 0; n < m_items.size(); n++) {
                MenuItem mi = getChild(n);
                if (mi == m_parent)
                    PARENT_MENU.writeExternal(dos);
                else
                    mi.writeExternal(dos);
            }
        }
    }

    public void readExternal(DataInputStream dis) throws Exception {
        m_alias = dis.readUTF();
        m_isFile = dis.readBoolean();
        m_fileRecordIndex = dis.readInt();
        m_selected = 0;
        m_top = 0;
        m_items.removeAllElements();
        if (!isFile()) {
            int size = dis.readInt();
            for (int n = 0; n < size; n++) {
                MenuItem mi = new MenuItem();
                mi.readExternal(dis);
                if (!mi.getAlias().equals(PARENT_MENU.getAlias())) {
                    addChild(mi);
                }
            }
        }
    }

    public int getFileRecordIndex() {
        return m_fileRecordIndex;
    }

    public void setFileRecordIndex(int fileRecordIndex) {
        m_fileRecordIndex = fileRecordIndex;
    }
}
