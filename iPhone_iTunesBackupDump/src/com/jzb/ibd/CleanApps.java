/**
 * 
 */
package com.jzb.ibd;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.jzb.ipa.plist.BinaryPListParser;
import com.jzb.ipa.plist.T_PLDict;

/**
 * @author jzarzuela
 * 
 */
public class CleanApps {

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
            CleanApps me = new CleanApps();
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

        File plistFile = new File("/Users/jzarzuela/Library/Application Support/MobileSync/Backup/2e1e42c63ba707a2ab1b9ffb95f9a1e7f38ecfc1/Manifest.plist");

        byte buffer[] = new byte[(int) plistFile.length()];
        FileInputStream fis = new FileInputStream(plistFile);
        fis.read(buffer);
        fis.close();

        T_PLDict dict = new BinaryPListParser().parsePList(buffer);
        dict = (T_PLDict) dict.getValue("Applications");
        ArrayList<String> al = dict.getKeys();

        File folder1 = new File("/Users/jzarzuela/Documents/iPhone/IPAs/iPhone");
        File folder2 = new File("/Users/jzarzuela/Documents/iPhone/IPAs/iPhone-iPad-mixtas");

        HashMap<String, File> filenames = new HashMap<String, File>();
        _searchFile(folder2, filenames);
        _searchFile(folder2, filenames);

        for (Entry<String, File> entry : filenames.entrySet()) {

            if(entry.getKey().contains("Blade")) {
                System.out.println("ya");
            }
            
            boolean found = false;

            for (String val : al) {
                if (entry.getKey().contains(val)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                //System.out.println(entry.getValue());
            }
        }


    }

    private void _searchFile(File folder, HashMap<String, File> filenames) throws Exception {

        for (File f : folder.listFiles()) {
            System.out.println(f);
            if (f.isDirectory()) {
                _searchFile(f, filenames);
            } else {
                /*
                if (f.getName().toLowerCase().endsWith(".ipa")) {
                    filenames.put(f.getName(), f);
                }
                */
            }
        }
    }
}
