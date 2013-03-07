/**
 * 
 */
package com.jzb.kk;

import java.io.File;

/**
 * @author jzarzuela
 * 
 */
public class KKTest {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            KKTest me = new KKTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
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
        File baseFolder = new File("/");
        _testFolder(baseFolder);
    }

    static int count  = 0;
    static int count2 = 0;
    static boolean lastWasDot = true;

    private void _testFolder(File folder) {

        if (++count2 > 100) {
            count2 = 0;
            System.out.print(".");
            lastWasDot = true;
            if (++count > 300) {
                count = 0;
                System.out.println();
            }
        }

        try {
            for (File f : folder.listFiles()) {
                if (f.isDirectory()) {
                    _testFolder(f);
                } else {
                    if(_checkFile(f)) return;
                }
            }
        } catch (Throwable th) {
            // nothing
        }
    }

    private boolean _checkFile(File fin) {
        try {
            String fname = fin.getName().toLowerCase();
            if (fname.contains("") || fname.contains("")) {
                if(lastWasDot) System.out.println();
                System.out.println("***> " + fin.getAbsolutePath());
                lastWasDot = false;
                return true;
            }
        } catch (Throwable th) {
            // nothing
        }
        return false;
    }

}
