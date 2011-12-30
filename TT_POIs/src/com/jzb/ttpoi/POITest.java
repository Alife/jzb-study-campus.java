/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.ConversionUtil;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.OV2FileWriter;

/**
 * @author n63636
 * 
 */
public class POITest {

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
            POITest me = new POITest();
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

        // The icon should be 22x22x4 bitmap format.

        File kmlFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_");
        File ov2Folder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_");

        _cleanOV2Folder(ov2Folder);

        System.out.println("---- Default categories mapping ---");
        System.out.println("Sin categoria --> "+TPOIData.UNDEFINED_CATEGORY);
        for (Map.Entry<String, String> entry : ConversionUtil.getDefaultParseCategories().entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
        System.out.println();

        for (File kmlFile : kmlFolder.listFiles()) {

            if (!kmlFile.getName().toLowerCase().endsWith(".kml"))
                continue;

            System.out.println("Processing map: " + kmlFile);
            int p1 = kmlFile.getName().lastIndexOf('.');
            String mapName = kmlFile.getName().substring(0, p1);

            TPOIFileData info = KMLFileLoader.loadFile(kmlFile);

            File ov2File = new File(ov2Folder, mapName + "\\" + mapName + "_ALL.ov2");
            ov2File.getParentFile().mkdirs();
            OV2FileWriter.saveFile(ov2File, info.getAllPOIs());

            if (info.getCategorizedPOIs().size() > 1) {
                for (Map.Entry<String, ArrayList<TPOIData>> entry : info.getCategorizedPOIs().entrySet()) {
                    String catName = entry.getKey();

                    if (catName.equals(TPOIData.UNDEFINED_CATEGORY))
                        ov2File = new File(ov2Folder, mapName + "\\" + mapName + ".ov2");
                    else
                        ov2File = new File(ov2Folder, mapName + "\\" + mapName + "_" + catName + ".ov2");

                    ov2File.getParentFile().mkdirs();
                    OV2FileWriter.saveFile(ov2File, entry.getValue());
                }
            }
        }

    }

    public void doIt2(String[] args) throws Exception {

        String str = "€$";

        for (char c : str.toCharArray()) {
            System.out.print((int) c + "," + c + ",");
        }
        System.out.println();

        ConversionUtil.getStrANSIValue(str);
    }

    private void _cleanOV2Folder(File ov2Folder) throws Exception {

        if (!ov2Folder.exists())
            return;

        for (File f : ov2Folder.listFiles()) {
            if (f.isFile() && f.getName().toLowerCase().endsWith(".ov2"))
                f.delete();
        }
        for (File f : ov2Folder.listFiles()) {
            if (f.isDirectory()) {
                _cleanOV2Folder(f);
                f.delete();
            }

        }
    }

}
