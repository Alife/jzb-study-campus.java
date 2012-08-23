/**
 * 
 */
package com.jzb.nc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author jzarzuela
 * 
 */
public class CompFolders {

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
            CompFolders me = new CompFolders();
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

    // ------------------------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        File origFolder = new File("/Volumes/My Passport/_disco_videos_/Videos_DivX");
        File destFolder = new File("/Volumes/ELEMENTS/Videos_DivX");
        _compRecursive(origFolder, destFolder);
    }

    // ------------------------------------------------------------------------------------------------------------------
    private void _compRecursive(File origFolder, File destFolder) throws Exception {

        TreeSet<String> folderDiffs = new TreeSet<String>();

        ArrayList<File> subFolders = new ArrayList<File>();
        for (File f : origFolder.listFiles()) {
            if (f.getName().equals(".wd_tv"))
                continue;
            if (f.isDirectory()) {
                File subFolder = new File(destFolder, f.getName());
                if (subFolder.exists()) {
                    subFolders.add(f);
                } else {
                    folderDiffs.add(f.getName() + " ++");
                }
            }
        }
        for (File f : destFolder.listFiles()) {
            if (f.getName().equals(".wd_tv"))
                continue;
            if (f.isDirectory()) {
                File subFolder = new File(origFolder, f.getName());
                if (!subFolder.exists()) {
                    folderDiffs.add(f.getName() + " --");
                }
            }
        }

        HashMap<String, File> info1 = _readFolderInfo(origFolder);
        HashMap<String, File> info2 = _readFolderInfo(destFolder);
        TreeSet<String> fileDiffs = _compareInfos(info1, info2);

        if (fileDiffs.size() > 0 || folderDiffs.size() > 0) {
            System.out.println("\n---------------------------------------------------------------------------");
            System.out.println("Comparing:");
            System.out.println("   origFolder: " + origFolder);
            System.out.println("   destFolder: " + destFolder);
            System.out.println();

            for (String str : fileDiffs) {
                System.out.println(str);
            }
            
            for (String str : folderDiffs) {
                System.out.println(str);
            }
        }

        for (File f : subFolders) {
            File subFolder = new File(destFolder, f.getName());
            _compRecursive(f, subFolder);
        }

    }

    // ------------------------------------------------------------------------------------------------------------------
    private TreeSet<String> _compareInfos(HashMap<String, File> info1, HashMap<String, File> info2) throws Exception {

        TreeSet<String> diffs = new TreeSet<String>();

        for (Map.Entry<String, File> entry1 : info1.entrySet()) {
            if (info2.get(entry1.getKey()) == null) {
                diffs.add(entry1.getValue().getName() + " +");
            }
        }

        for (Map.Entry<String, File> entry2 : info2.entrySet()) {
            if (info1.get(entry2.getKey()) == null) {
                diffs.add(entry2.getValue().getName() + " -");
            }
        }

        return diffs;
    }

    // ------------------------------------------------------------------------------------------------------------------
    private HashMap<String, File> _readFolderInfo(File folder) throws Exception {

        HashMap<String, File> info = new HashMap<String, File>();

        for (File f : folder.listFiles()) {
            if (f.isFile() && !f.getName().contains(".DS_Store")) {
                info.put(_calcFileKey(f), f);
            }
        }

        return info;
    }

    // ------------------------------------------------------------------------------------------------------------------
    private String _calcFileKey(File file) throws Exception {
        return _fastCalcFileKey(file);
    }

    // ------------------------------------------------------------------------------------------------------------------
    private String _fastCalcFileKey(File file) throws Exception {
        String key = file.getName() + "#" + file.length();
        return key;
    }
}
