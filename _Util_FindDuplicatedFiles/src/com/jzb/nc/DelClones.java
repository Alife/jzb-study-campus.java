/**
 * 
 */
package com.jzb.nc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author n63636
 * 
 */
public class DelClones {

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
            DelClones me = new DelClones();
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

        File dataFile = new File("C:\\WKSPs\\Consolidado\\_Util_FindDuplicatedFiles\\out\\pp.txt");

        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        while (br.ready()) {
            String line = br.readLine();
            if (line.length() == 0)
                continue;
            
            File f = new File(line);
            if (f.exists()) {
                //System.out.println("Deleting file: " + f);
                if (!f.delete()) {
                    System.out.println("Error deleting file: " + f);
                }
            } else {
                System.out.println("File doesn't exist: " + f);
            }

        }
        br.close();

    }
}
