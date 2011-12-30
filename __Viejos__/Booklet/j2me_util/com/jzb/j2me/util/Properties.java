/**
 * 
 */
package com.jzb.j2me.util;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author PS00A501
 * 
 */
public class Properties {

    private Hashtable m_props = new Hashtable();

    /**
     * 
     */
    public Properties() {
        super();
    }

    public void addProperty(String key, String value) {
        m_props.put(key, value);
    }

    public String getProperty(String key) {
        return (String) m_props.get(key);
    }

    public String removeProperty(String key) {
        return (String) m_props.remove(key);
    }

    public String setProperty(String key, String value) {
        return (String) m_props.put(key, value);
    }

    public int size() {
        return m_props.size();
    }

    public Enumeration getKeys() {
        return m_props.keys();
    }

    public void addAllProperties(Properties prop) {
        Enumeration e = prop.getKeys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            m_props.put(key, prop.getProperty(key));
        }
    }

}
