/**
 * 
 */
package com.jzb.wiki.dt;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author n63636
 * 
 */
public class TWikiItem {

    public static enum TYPE {
        DOCUMENT, HTMLCOMMENT, MACRO, MACRO_PARAM, PARAM, FUNCTION, LINK, PROPERTY, CATEGORY
    };

    private ArrayList<TWikiItem> m_children = new ArrayList<TWikiItem>();
    private String              m_name;
    private ArrayList<TWikiItem> m_params   = new ArrayList<TWikiItem>();
    private TYPE                m_type;
    private Object              m_value;

    public TWikiItem(TYPE myType) {
        m_type = myType;
    }

    public void addChild(TWikiItem child) {
        m_children.add(child);
    }

    public void addParam(TWikiItem param) {
        param.setType(TYPE.PARAM);
        m_params.add(param);
    }

    public TWikiItem getChild(int index) {
        return m_children.get(index);
    }

    /**
     * @return the children
     */
    public ArrayList<TWikiItem> getChildren() {
        return m_children;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    public TWikiItem getParam(int index) {
        return m_params.get(index);
    }

    /**
     * @return the params
     */
    public ArrayList<TWikiItem> getParams() {
        return m_params;
    }

    /**
     * @return the type
     */
    public TYPE getType() {
        return m_type;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return m_value;
    }

    public static void parseNameValue(TWikiItem item, String str, String separator) {

        int p1 = str.indexOf(separator);
        if (p1 > 0) {
            item.setName(str.substring(0, p1).trim());
            item.setValue(str.substring(p1 + separator.length()).trim());
        } else {
            item.setValue(str.trim());
        }
    }
    
    public void parseAndAddParam(String str) {
        TWikiItem param = new TWikiItem(TYPE.PARAM);
        parseNameValue(param,str,"=");
        addParam(param);
    }

    /**
     * @param children
     *            the children to set
     */
    public void setChildren(ArrayList<TWikiItem> children) {
        m_children = children;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Object value) {
        m_value = value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _innerToString("  ");
    }

    String _innerToString(String padding) {

        String cad = "";

        cad += padding + "type = '" + m_type + "'\n";

        if (m_name != null) {
            cad += padding + "name = '" + m_name + "'\n";
        }

        if (m_value != null) {
            cad += padding + "value = ";
            if (m_value instanceof TWikiItem) {
                cad += "\n" + ((TWikiItem) m_value)._innerToString(padding + padding);
            } else {
                cad += "'" + m_value + "'";
            }
            cad += "\n";
        }

        if (m_params.size() > 0) {
            cad += padding + "params = \n";
            for (TWikiItem param : m_params) {
                cad += param._innerToString(padding + padding) + "\n";
            }
        }

        if (m_children.size() > 0) {
            cad += padding + "children = \n";
            for (TWikiItem child : m_children) {
                cad += child._innerToString(padding + padding) + "\n";
            }
        }

        return cad;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(TYPE type) {
        m_type = type;
    }

    public boolean hasChildren() {
        return m_children.size() > 0;
    }

    public boolean hasParams() {
        return m_params.size() > 0;
    }

    public Collection<TWikiItem> getByType(TYPE type) {

        ArrayList<TWikiItem> items = new ArrayList<TWikiItem>();

        for (TWikiItem item : m_children) {

            if (item.getType() == type) {
                items.add(item);
            }

            if (item.hasChildren()) {
                items.addAll(item.getByType(type));
            }

        }

        for (TWikiItem param : m_params) {

            for (TWikiItem item : param.getChildren()) {

                if (item.getType() == type) {
                    items.add(item);
                }

                if (item.hasChildren()) {
                    items.addAll(item.getByType(type));
                }

            }
        }

        return items;
    }
}
