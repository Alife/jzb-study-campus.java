/**
 * 
 */
package com.jzb.flickr.xmlbean;

import java.lang.reflect.Method;

/**
 * @author n000013
 * 
 */
public class JavaBeanUtils {

    public static Object getPropValue(Object jb, String propName) throws Exception {

        int pos = propName.indexOf('.');
        if (pos != -1) {
            String firstName = propName.substring(0, pos);
            String nextName = propName.substring(pos + 1);
            Object subJB = getPropValue(jb, firstName);
            return getPropValue(subJB, nextName);
        } else {
            String methodName = "get" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
            Method m = jb.getClass().getMethod(methodName);
            Object result = m.invoke(jb);
            return result;
        }
    }

    public static void setPropValue(Object jb, String propName, Object value) throws Exception {

        int pos = propName.indexOf('.');
        if (pos != -1) {
            String firstName = propName.substring(0, pos);
            String nextName = propName.substring(pos + 1);
            Object subJB = getPropValue(jb, firstName);
            setPropValue(subJB, nextName, value);
        } else {
            String methodName = "set" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
            Method m;
            if (value == null)
                m = _findMethod(jb, methodName);
            else
                m = jb.getClass().getMethod(methodName, value.getClass());
            m.invoke(jb, value);
        }
    }

    private static Method _findMethod(Object obj, String name) throws Exception {

        Method result = null;

        Method ms[] = obj.getClass().getMethods();
        if (ms != null) {
            for (Method m1 : ms) {
                if (m1.getName().equals(name)) {
                    if (result != null)
                        throw new NoSuchMethodException("Method '" + name + "' not unique in '" + obj.getClass().getName() + "' for null assignment");
                    result = m1;
                }
            }
        }

        if (result == null)
            throw new NoSuchMethodException("Method '" + name + "' not found in '" + obj.getClass().getName() + "'");

        return result;
    }
}
