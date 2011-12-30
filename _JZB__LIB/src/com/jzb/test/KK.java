/**
 * 
 */
package com.jzb.test;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class KK {

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
            KK me = new KK();
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

        File folder = new File("F:\\disco_c\\Videos_DivX\\_Incoming\\Series\\pan am");
        for(File f:folder.listFiles()) {
            if(f.getName().toLowerCase().startsWith("pan")) {
                File newfile = new File(folder,"neverland_1.avi");
                if(!f.renameTo(newfile))
                    System.out.println("Error");
            }
        }
    }
}
