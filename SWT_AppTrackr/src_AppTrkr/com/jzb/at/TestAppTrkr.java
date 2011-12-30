/**
 * 
 */
package com.jzb.at;

import java.io.File;
import java.util.ArrayList;

/**
 * @author n63636
 * 
 */
public class TestAppTrkr {

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
            TestAppTrkr me = new TestAppTrkr();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(0);
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

        UpdateChecker uc=new UpdateChecker(new File("C:\\JZarzuela\\iPhone\\IPAs"),false); 
        ArrayList<IPAData> list = uc.checkUpdates();
        System.out.println(uc.getUpdateInfoInHTML(list));
    }


}
