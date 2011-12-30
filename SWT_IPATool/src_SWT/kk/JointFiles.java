/**
 * 
 */
package kk;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author n000013
 * 
 */
public class JointFiles {

    private HashMap<String, File> m_ipas = new HashMap<String, File>();

    private HashMap<String, File> m_jpgs = new HashMap<String, File>();

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
            JointFiles me = new JointFiles();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }
    public void _moveFiles() {
        
        for (Entry<String, File> entry : m_jpgs.entrySet()) {
            File ipa = m_ipas.get(entry.getKey());
            if(ipa==null) {
                System.out.println("Error, JPG without IPA: "+entry.getValue());
                continue;
            }
            
            if(!ipa.getParentFile().equals(entry.getValue().getParentFile())) {
                File newFile = new File(entry.getValue().getParentFile(), ipa.getName());
                if(!ipa.renameTo(newFile)) {
                    System.out.println("Error moving IPA file to "+newFile);
                }
            }
        }
    }

    public void _processFile(File afile) throws Exception {

        String name = afile.getName().toLowerCase();
        String baseName = name.substring(0, name.length() - 4);
        File prvFile = null;

        if (name.endsWith(".ipa")) {
            prvFile = m_ipas.put(baseName, afile);
        } else if (name.endsWith(".jpg")) {
            prvFile = m_jpgs.put(baseName, afile);
        }

        if (prvFile != null) {
            System.out.println("Error, file duplicated:");
            System.out.println("  " + prvFile);
            System.out.println("  " + afile);
        }
    }

    public void _processFolder(File afolder) throws Exception {

        System.out.println("Processing folder: " + afolder);
        for (File afile : afolder.listFiles()) {
            if (afile.isDirectory()) {
                _processFolder(afile);
            } else {
                _processFile(afile);
            }
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

        File baseFolder = new File("F:\\jb-apps\\_Apps");
        _processFolder(baseFolder);
        _moveFiles();

    }
}
