/**
 * 
 */
package com.jzb.test;

import java.io.File;
import java.util.HashMap;

import com.jzb.futil.FileUtils;
import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;

/**
 * @author n63636
 * 
 */
public class ReplicateCR2Tree {

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
            ReplicateCR2Tree me = new ReplicateCR2Tree();
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

        final HashMap<String, String> cr2Files = new HashMap<String, String>();

        final File baseFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2011_04-09-17_IMPACT-2011");
        final File jpgFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2011_04-09-17_IMPACT-2011\\_CR2_JPG_");
        final File treeFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2011_04-09-17_IMPACT-2011\\_CR2_TREE_");

        // ----------------------------------------------------------------------------------------------
        IFileProcessor fprocessor1 = new IFileProcessor() {

            /**
             * @see com.jzb.futil.IFileProcessor#setFolderIterator(com.jzb.futil.FolderIterator)
             */
            @Override
            public void setFolderIterator(FolderIterator fi) {
            }

            /**
             * @see com.jzb.futil.IFileProcessor#processFile(java.io.File, java.io.File)
             */
            @Override
            public void processFile(File f, File baseFolder) throws Exception {
                if (FileUtils.getExtension(f).equals("cr2")) {
                    String baseFiName = f.getName().substring(0, f.getName().length() - 4).toLowerCase();
                    String baseFdName = f.getParent().substring(baseFolder.getPath().length());
                    cr2Files.put(baseFiName, baseFdName);
                }
            }
        };

        FolderIterator fi = new FolderIterator(fprocessor1, baseFolder);
        fi.iterate();

        for (File jpgCr2File : jpgFolder.listFiles()) {
            String baseFiName = jpgCr2File.getName().substring(0, jpgCr2File.getName().length() - 8).toLowerCase();
            String treeFdName = cr2Files.get(baseFiName);
            if (treeFdName == null) {
                System.out.println("ERROR: CR2 equivalent file not found for: " + jpgCr2File);
            } else {
                File newFile = new File(treeFolder, treeFdName + "\\" + jpgCr2File.getName());
                newFile.getParentFile().mkdirs();
                if (!jpgCr2File.renameTo(newFile)) {
                    System.out.println("ERROR: File (" + jpgCr2File.getName() + ") couldn't be renamed to: " + newFile);
                }
            }
        }
    }
}
