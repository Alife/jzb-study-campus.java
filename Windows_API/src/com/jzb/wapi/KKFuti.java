/**
 * 
 */
package com.jzb.wapi;

import java.lang.reflect.Field;

/**
 * @author n000013
 * 
 */
public class KKFuti {

    public static void kkfuti() {

        Field field = null;
        Class clazz = null;
        Object original = null;
        boolean accessible = false;

        try {
            // Reset the "sys_paths" field of the ClassLoader to null.
            clazz = ClassLoader.class;
            field = clazz.getDeclaredField("sys_paths");
            accessible = field.isAccessible();
            if (!accessible)
                field.setAccessible(true);
            original = field.get(clazz);
            // Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value.
            field.set(clazz, null);
            System.setProperty("java.library.path", "C:\\WKSPs\\Consolidado\\WndAPI\\lib;" + System.getProperty("java.library.path"));
            // Change the value and load the library.
        } catch (Throwable th) {
            th.printStackTrace();
        } finally {
            try {
                // Revert back the changes.
                // field.set(clazz, original);
                field.setAccessible(accessible);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

}
