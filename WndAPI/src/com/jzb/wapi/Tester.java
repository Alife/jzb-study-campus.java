/**
 * 
 */
package com.jzb.wapi;

import com.JWinAPI.JWinAPI;
import com.JWinAPI.MBConstants;

/**
 * @author n000013
 * 
 */
public class Tester {

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
            Tester me = new Tester();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
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
        KKFuti.kkfuti();

        JWinAPI wapi = new JWinAPI();
        wapi.doMessageBox("JWinAPI DLL has been loaded successfully!", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);
   
    }
    
}
