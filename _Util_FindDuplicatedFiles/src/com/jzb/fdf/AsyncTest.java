/**
 * 
 */
package com.jzb.fdf;

import java.nio.file.FileSystems;

import com.jzb.util.Tracer;
import com.jzb.util.Tracer.Level;

/**
 * @author jzarzuela
 * 
 */
public class AsyncTest {

    // ----------------------------------------------------------------------------------------------------
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
            AsyncTest me = new AsyncTest();
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

        Tracer.setTracer(new AsyncTracer());
        Tracer.setLevelEnabled(Level.DEBUG, true);

        Tracer._info("Starting global processing");
        FolderProcessor.init("/Users/jzarzuela/Documents/java-Campus/_Util_FindDuplicatedFiles/out");
        FolderProcessor.spawnFolderProcessor(FileSystems.getDefault().getPath("/Users/jzarzuela/Downloads/_tmp_/_extracted_IPAs_"));
        FolderProcessor.awaitTermination();
        Tracer._info("Finished global processing");
        Tracer.flush();
    }

}
