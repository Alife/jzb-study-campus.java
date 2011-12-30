/**
 * 
 */
package com.jzb.j2me.util;

import java.util.Hashtable;

/**
 * @author PS00A501
 * 
 */
public class GlobalContext {

    private static Hashtable s_data = new Hashtable();

    public static Object getData(String name) {
        return s_data.get(name);
    }

    public static void setData(String name, Object value) {
        if (value != null)
            s_data.put(name, value);
        else
            s_data.remove(name);
    }
}
