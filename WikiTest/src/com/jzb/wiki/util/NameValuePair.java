/**
 * 
 */
package com.jzb.wiki.util;

/**
 * @author n000013
 * 
 */
public class NameValuePair {

    private String m_name;
    private String m_value;

    public NameValuePair() {
        this("", "");
    }

    public NameValuePair(String name, String value) {
        m_name = name;
        m_value = value;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String value) {
        m_value = value;
    }

    public void parse(String text, char separator, char toFilter[]) {
        int p = text.indexOf(separator);
        if (p == -1)
            return;
        String name = text.substring(0, p);
        String value = text.substring(p + 1);
        if (toFilter != null)
            for (int n = 0; n < toFilter.length; n++) {
                name = name.replace(toFilter[n], ' ');
            }
        name=name.trim();
        if (toFilter != null)
            for (int n = 0; n < toFilter.length; n++) {
                value = value.replace(toFilter[n], ' ');
            }
        value=value.trim();
        m_name=name;
        m_value=value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "'"+m_name + "' = '" + m_value + "'";
    }
}
