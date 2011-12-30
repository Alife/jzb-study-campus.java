/**
 * 
 */
package com.jzb.test;

import java.io.File;

import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;
import com.jzb.futil.fprocs.SplitByExtensionFileProcessor;

/**
 * @author n63636
 * 
 */
public class SplitByExt {

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
            SplitByExt me = new SplitByExt();
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

        File baseFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston");
        File splittedFolder = new File(baseFolder, "_CR2_");

        IFileProcessor fprocessor = new SplitByExtensionFileProcessor(splittedFolder,"cr2");

        FolderIterator fi = new FolderIterator(fprocessor, baseFolder);

        fi.iterate();
    }
}
