/**
 * 
 */
package com.jzb.kk.ai;

import java.io.BufferedReader;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author n63636
 * 
 */
public class FList {

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
            FList me = new FList();
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

        String curPath = "";
        TreeMap<String, FmInfo> infoList = new TreeMap<String, FmInfo>();

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\n63636\\Desktop\\doc_v\\p.txt"));
        while (br.ready()) {

            String str = br.readLine();
            if (str.startsWith(" Directorio")) {
                curPath = str.substring(18);
            } else if (str.length() > 30 && Character.isDigit(str.charAt(0)) && str.charAt(21) != '<') {
                FmInfo fi = _parseLine(str, curPath);
                if (fi.name.toLowerCase().endsWith(".srt")) {
                    if (!(fi.name.charAt(1) == 'x') && !Character.isDigit(fi.name.charAt(1))) {
                        FmInfo fii = infoList.get(fi.folder + "#" + fi.nameWOExt);
                        if (fii == null) {
                            infoList.put(fi.folder + "#" + fi.nameWOExt, fi);
                        } else {
                            if (!fii.name.startsWith("vos_")) {
                                fii.name = "vos_" + fii.name;
                            }
                        }
                    }
                } else {
                    FmInfo fii = infoList.put(fi.folder + "#" + fi.nameWOExt, fi);
                    if (fii != null) {
                        if (!fi.name.startsWith("vos_")) {
                            fi.name = "vos_" + fi.name;
                        }
                    }
                }
            }
        }
        br.close();

        PrintWriter pw = new PrintWriter("C:\\Users\\n63636\\Desktop\\doc_v\\p.cvs");
        for (FmInfo fi : infoList.values()) {
            pw.println(fi.folder + "\t" + fi.date + "\t" + fi.size + "\t" + fi.name + "\t");
        }
        pw.close();
    }

    private static class FmInfo {

        public String folder;
        public String name;
        public String date;
        public String size;

        public String nameWOExt;
    }

    private FmInfo _parseLine(String str, String folder) throws Exception {

        FmInfo fi = new FmInfo();
        fi.folder = folder.toLowerCase();
        fi.name = str.substring(36).toLowerCase();
        fi.date = str.substring(0, 10);
        fi.size = str.substring(20, 36).trim();

        int p1 = fi.name.lastIndexOf('.');
        if (p1 > 0) {
            fi.nameWOExt = fi.name.substring(0, p1);
        } else {
            fi.nameWOExt = fi.name;
        }
        return fi;
    }
}
