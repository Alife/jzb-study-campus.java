/**
 * 
 */
package com.jzb.test;


import com.jzb.futil.FIParameters;
import com.jzb.futil.FolderIterator;
import com.jzb.futil.fprocs.DummyFileProcessor;

/**
 * @author n63636
 * 
 */
public class TestFI {

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
            TestFI me = new TestFI();
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

        String str="C:\\JZarzuela\\_Fotos_\\Cantabria";

        FIParameters params = new FIParameters(); 
        params.setFolderIncludeFilters(new String[] {".*203.*",".*340.*",".*101.*"});
        params.setFileExtensionFilters(new String[] {"c22","jpg"});
        params.setFileExcludeFilters(new String[] {".*62.*"});
        params.setFileIncludeFilters(new String[] {".*62.*"});
        
        FolderIterator fi = new FolderIterator(DummyFileProcessor.instance,params, str);

        fi.iterate();
    }

}
