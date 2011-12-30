/**
 * 
 */
package com.jzb.nc;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class DelEmptyFolder {

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
            DelEmptyFolder me = new DelEmptyFolder();
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

        final File baseFolder = new File("f:\\");
        int count = _processFolder(baseFolder);
        if (count == 0) {
            if (!baseFolder.delete()) {
                System.out.println("Error deleting empty folder: " + baseFolder);
            }
        }

    }

    private int _processFolder(File folder) throws Exception {

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                int count = _processFolder(f);
                if (count == 0) {
                    System.out.println("Deleting empty folder: " + f);
                    if (!f.delete()) {
                        System.out.println("** Error deleting empty folder: " + f);
                    }
                }
            }
        }

        return folder.listFiles().length;

    }

    

}
