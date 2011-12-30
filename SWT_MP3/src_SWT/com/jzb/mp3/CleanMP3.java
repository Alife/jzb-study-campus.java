/**
 * 
 */
package com.jzb.mp3;

import java.io.File;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class CleanMP3 {

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
            CleanMP3 me = new CleanMP3();
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

        String mp3BasePath = "***C:\\JZarzuela\\MP3\\PENDING";
        int mbpLen = mp3BasePath.length();

        InfoLoader infoLoader = new InfoLoader(mp3BasePath);
        infoLoader.loadInfo();
        for (FileInfo fi : infoLoader.getFileInfo()) {
            
            if (!fi.file.exists() || fi.state==FileInfoState.PENDING)
                continue;
            
            String newFilePath = mp3BasePath + "\\..\\" + fi.state + fi.file.getAbsolutePath().substring(mbpLen);
            Tracer._debug("renaming file: +" + newFilePath);
            //Tracer._debug("renaming file: -" + fi.file.getAbsolutePath());
            File newFile = new File(newFilePath);
            if (!fi.file.getAbsolutePath().equalsIgnoreCase(newFilePath)) {
                newFile.getParentFile().mkdirs();
                if (!fi.file.renameTo(newFile)) {
                    Tracer._error("Error renaming file to: " + newFilePath);
                }
            }
        }

    }

    public void doIt2(String[] args) throws Exception {

        File basePath = new File("xxxC:\\JZarzuela\\MP3\\_OLD_");
        _proneEmptyDirs(basePath);
    }

    private void _proneEmptyDirs(File folder) {

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                if (f.listFiles().length == 0) {
                    f.delete();
                } else {
                    _proneEmptyDirs(f);
                    if (f.listFiles().length == 0) {
                        f.delete();
                    }
                }
            }
        }
    }
}
