/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import com.jzb.futil.FileExtFilter;

/**
 * @author n000013
 * 
 */
public class MaxVersion {

    private Hashtable<String, ArrayList<File>> m_filesData = new Hashtable<String, ArrayList<File>>();

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
            MaxVersion me = new MaxVersion();
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

        File baseFolder = new File("W:\\iphone\\_FINISHED_\\");
        _processFolder(baseFolder);

        for (ArrayList<File> list : m_filesData.values()) {
            if (list.size() > 1) {
                for (File f : list) {
                    System.out.println(f);
                }
                System.out.println();
            }
        }
    }

    private String _getBaseName(File afile) {

        String baseName = afile.getName();
        int pos1, pos2;

        pos1 = baseName.lastIndexOf('[');
        if (pos1 > 0) {
            pos2 = baseName.lastIndexOf(']');

            if (pos2 > 0) {
                baseName = baseName.substring(pos1, pos2);
                return baseName;
            }
        }

        baseName = afile.getName().substring(4);
        int pos = baseName.indexOf("_V");
        if (pos > 0)
            baseName = baseName.substring(0, pos);

        return baseName;
    }

    private void _processFile(File afile) throws Exception {

        String baseName = _getBaseName(afile);

        ArrayList<File> list = m_filesData.get(baseName);
        if (list == null) {
            list = new ArrayList<File>();
            m_filesData.put(baseName, list);
        }

        list.add(afile);

    }

    private void _processFolder(File afolder) throws Exception {

        for (File afile : afolder.listFiles(new FileExtFilter(true, "ipa"))) {
            if (afile.isDirectory()) {
                _processFolder(afile);
            } else {
                _processFile(afile);
            }
        }
    }
}
