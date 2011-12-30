/**
 * 
 */
package com.jzb.wiki.pdp.item;

import java.util.ArrayList;

import com.jzb.wiki.pdp.IPdPItem;
import com.jzb.wiki.util.NameValuePair;

/**
 * @author n000013
 * 
 */
public abstract class PdPItem_Base implements IPdPItem {

    private NameValuePair[]     m_attrs    = new NameValuePair[0];
    private ArrayList<IPdPItem> m_subItems = new ArrayList<IPdPItem>();
    private String              m_value;

    public PdPItem_Base() {

    };

    /**
     * @see com.jzb.wiki.pdp.IPdPItem#getAttrs()
     */
    public NameValuePair[] getAttrs() {
        return m_attrs;
    }

    public java.util.ArrayList<IPdPItem> getSubItems() {
        return m_subItems;
    }

    /**
     * @see com.jzb.wiki.pdp.IPdPItem#getValue()
     */
    public String getValue() {
        return m_value;
    }

    public void setAttr(String name, String value) throws Exception {

        for (NameValuePair vp : m_attrs) {
            if (vp.getName().equals(name)) {
                vp.setValue(_cleanHTMLTags(_parseWikiURLs(value)));
                return;
            }
        }

        throw new Exception("Incorrect attribute '" + name + "' for Item '" + getTypeName() + "'");
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        m_value = _cleanHTMLTags(_parseWikiURLs(value));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("Item '" + getTypeName() + "' {\n");
        sb.append("   value = '" + (getValue() != null ? getValue() : "") + "'\n");

        for (NameValuePair vp : m_attrs) {
            sb.append("   attr '" + vp.getName() + "' = '" + (vp.getValue() != null ? vp.getValue() : "") + "'\n");
        }

        if (m_subItems.size() > 0)
            sb.append("\n--- Sub Items ---\n");
        for (IPdPItem si : m_subItems) {
            sb.append(si);
        }
        if (m_subItems.size() > 0)
            sb.append("-----------------\n\n");

        sb.append("}\n");

        return sb.toString();
    }

    /**
     * @param attrs
     *            the attrs to set
     */
    void setAttrs(NameValuePair[] attrs) {
        m_attrs = attrs;
    }

    public void addSubItem(IPdPItem item) {
        m_subItems.add(item);
    }

    private String _cleanHTMLTags(String value) {

        value = _replaceHTMLTags(value);
        
        for (;;) {
            int p1 = value.indexOf("<");
            if (p1 >= 0) {
                int p2 = 1 + value.indexOf('>', p1);
                value = value.substring(0, p1) + value.substring(p2);
            } else {
                break;
            }
        }

        return value.trim();
    }

    private static final String HTML_TAGS[] = { "<li", "<br" };
    private static final String REPLACE_TAGS[] = { "\n* ", "\n" };

    private String _replaceHTMLTags(String value) {

        for (int n=0;n<HTML_TAGS.length;n++) {
            int p1 = 0;
            int p2 = 0;
            for (;;) {
                p1 = value.indexOf(HTML_TAGS[n], p2);
                if (p1 < 0)
                    break;
                p2 = 1 + value.indexOf('>', p1);
                value = value.substring(0, p1) + REPLACE_TAGS[n] + value.substring(p2);
            }
        }
        return value.trim();
    }

    private String _parseWikiURLs(String value) {

        for (;;) {
            int p1 = value.indexOf("<a ");
            if (p1 >= 0) {
                int p2 = 1 + value.indexOf('>', p1);
                int p3 = value.indexOf("</a>", p2);
                value = value.substring(0, p1) + value.substring(p2, p3) + value.substring(4 + p3);
            } else {
                break;
            }
        }

        return value;
    }

    /**
     * @see com.jzb.wiki.pdp.IPdPItem#getAttrVal(int)
     */
    public String getAttrVal(int index) {
        return m_attrs[index].getValue();
    }

    /**
     * @see com.jzb.wiki.pdp.IPdPItem#getAttrVal(java.lang.String)
     */
    public String getAttrVal(String name) {
        for (NameValuePair vp : m_attrs) {
            if (vp.getName().equals(name))
                return vp.getValue();
        }
        return null;
    }
}