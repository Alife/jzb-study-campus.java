/**
 * 
 */
package com.jzb.kk.an;

import java.lang.annotation.Annotation;

import com.jzb.an.MSFComponent;

/**
 * @author n63636
 * 
 */
public class ANTest {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            ANTest me = new ANTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        Class clazz = Class.forName("com.jzb.kk.an.MyComponent");
        if(clazz.isAnnotationPresent(MSFComponent.class)) {
            System.out.println("This class is a MSComponent");
            MSFComponent an=(MSFComponent)clazz.getAnnotation(MSFComponent.class);
            System.out.println(an.biblia());
        }
    }
}
