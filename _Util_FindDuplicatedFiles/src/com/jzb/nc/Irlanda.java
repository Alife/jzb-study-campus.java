/**
 * 
 */
package com.jzb.nc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n63636
 * 
 */
public class Irlanda {

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
            Irlanda me = new Irlanda();
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
        findCR2();
        findJPG();
    }

    private void findJPG() throws Exception {
        File jpgFolder = new File("E:\\_Backup_\\_Fotos_\\_Fotos_\\__SIN COLOCAR__\\Irlanda\\Fotos_Irlanda");
        processFolderJPG(jpgFolder);
    }
    private void processFolderJPG(File folder) throws Exception {
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                processFolderJPG(f);
            } else {
                if (f.getName().toLowerCase().endsWith(".jpg")) {
                    String imgName=getImgNameJPG(f);
                    File cr2File=cr2Files.get(imgName);
                    if(cr2File!=null) {
                        System.out.println(cr2File);
                    }
                }
            }
        }
    }

    private void findCR2() throws Exception {
        File cr2Folder = new File("E:\\discoc\\BK_IRLANDA-10-08-2009\\Irlanda\\Fotos_Irlanda");
        processFolderCR2(cr2Folder);
    }

    private HashMap<String, File> cr2Files = new HashMap<String, File>();

    private void processFolderCR2(File folder) throws Exception {
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                processFolderCR2(f);
            } else {
                if (f.getName().toLowerCase().endsWith(".cr2")) {
                    cr2Files.put(getImgNameCR2(f), f);
                }
            }
        }
    }

    private Pattern ptrnImg = Pattern.compile(".*(IMG_[0-9]*).*", Pattern.CASE_INSENSITIVE);

    private String getImgNameCR2(File f) {
        Matcher m = ptrnImg.matcher(f.getName());
        if (m.matches())
            return m.group(1);
        else
            return "kkkkk";
    }
    
    private String getImgNameJPG(File f) {
        Matcher m = ptrnImg.matcher(f.getName());
        if (m.matches())
            return m.group(1);
        else
            return "xxxxxx";
    }
}
