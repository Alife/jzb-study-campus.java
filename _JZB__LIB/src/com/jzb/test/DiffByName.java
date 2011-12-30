/**
 * 
 */
package com.jzb.test;

import java.io.File;
import java.util.Collection;

import com.jzb.futil.FolderIterator;
import com.jzb.futil.fprocs.DiffFileProcessor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class DiffByName {

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
            DiffByName me = new DiffByName();
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

        File baseFolder1 = new File("G:\\_Backup_\\_Fotos_\\_Fotos_\\Business_Jose\\2009_05_08-11_New York\\Organizadas");
        File baseFolder2 = new File("C:\\Users\\n63636\\Desktop\\Organizadas");

        DiffFileProcessor diffProcessor = new DiffFileProcessor();

        FolderIterator fi = new FolderIterator(diffProcessor, baseFolder1, baseFolder2);

        fi.iterate();

        Collection<File> singleFiles = diffProcessor.getSingleFiles();
        for (File f : singleFiles) {
            Tracer._debug(f.getAbsolutePath());
        }

    }

}
