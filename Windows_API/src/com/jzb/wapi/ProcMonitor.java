/**
 * 
 */
package com.jzb.wapi;

import java.io.File;
import java.util.Map;

import com.jzb.pm.ProcessMonitor;

/**
 * @author n000013
 * 
 */
public class ProcMonitor {

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
            ProcMonitor me = new ProcMonitor();
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

        ProcessMonitor pm = new ProcessMonitor("c:\\", (Map)null, "notepad.exe", "c:\\p.txt");
        pm.start();
        Thread.currentThread().sleep(5000);
        pm.stop();

    }
}
