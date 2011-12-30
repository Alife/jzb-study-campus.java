/**
 * 
 */
package com.jzb.kk;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class RenDir {

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
            RenDir me = new RenDir();
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

        File bf = new File("C:\\Users\\n63636\\Desktop\\doc_v\\Francia_2011\\Fotos");

        for (File folder : bf.listFiles()) {
            changeName(folder);
        }
    }

    private void changeName(File pfolder) throws Exception {

        for (File f : pfolder.listFiles()) {
            String nname = getNewName(f.getName());
            File nf = new File(pfolder, nname);
            System.out.println("Renaming to " + nf);
            if (!f.renameTo(nf)) {
                System.out.println("ERROR renaming folder: " + f);
            }
        }
    }
    
    private String getNewName(String name) throws Exception {
  
        return "X-"+name;
        
//        int p1=name.indexOf('-',3);
//        
//        String nname = name.substring(0,2)+name.substring(p1+1,p1+3)+"-"+name.substring(3,p1)+name.substring(p1+3);
//
//        return nname;
    }
}
