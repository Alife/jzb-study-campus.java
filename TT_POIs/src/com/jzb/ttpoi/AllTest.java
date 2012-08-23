/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;

import com.jzb.ttpoi.util.FileTransform;
import com.jzb.ttpoi.util.KMLDownload;

/**
 * @author n63636
 * 
 */
public class AllTest {

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
            AllTest me = new AllTest();
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
        
        File kmlFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_");
        File ov2Folder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2s_");
        
        KMLDownload.downloadAllMaps(kmlFolder);
        FileTransform.transformAllKMLtoOV2(kmlFolder,ov2Folder, true);
    }
}
