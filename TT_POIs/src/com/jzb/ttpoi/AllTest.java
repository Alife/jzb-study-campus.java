/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.HashMap;

import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.util.FileTransform;
import com.jzb.ttpoi.util.KMLDownload;
import com.jzb.ttpoi.wl.ConversionUtil;
import com.jzb.ttpoi.wl.KMLFileLoader;

/**
 * @author n63636
 * 
 */
public class AllTest {

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
            AllTest me = new AllTest();
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
    public void doIt2(String[] args) throws Exception {

        File kmlFolder = new File("/Users/jzarzuela/Desktop/pp/_KMLs_");
        File ov2Folder = new File("/Users/jzarzuela/Desktop/pp/_OV2s_");

        KMLDownload.downloadAllMaps(kmlFolder);
        FileTransform.transformAllKMLtoOV2(kmlFolder, ov2Folder, true);
    }

    public void doIt(String[] args) throws Exception {
        
        File kmlFile = new File("/Users/jzarzuela/Desktop/pp/_KMLs_/BT_DEVOXX_2012.kml");
        File ov2Folder = new File("/Users/jzarzuela/Desktop/pp/_OV2s_");
        boolean nameSorted = true;
        
        /*
        ConversionUtil.getDefaultParseCategories().clear();
        System.out.println("--- Categories ---");
        TPOIFileData info = KMLFileLoader.loadFile(kmlFile);
        for(String cat:info.getCategories()) {
            System.out.println(cat);
        }
        */
/*
        HashMap<String, String> styleCatMap = ConversionUtil.getDefaultParseCategories();
        styleCatMap.put("blue-dot", "Interes");
        styleCatMap.put("ltblue-dot", "Interes");
        styleCatMap.put("cabs", "Varios");
        styleCatMap.put("green-dot", "Parques");
        styleCatMap.put("homegardenbusiness", "Varios");
        styleCatMap.put("hospitals", "Centro");
        styleCatMap.put("red-dot", "Centro");
        styleCatMap.put("shopping", "Varios");
        styleCatMap.put("tree", "Parques");
        styleCatMap.put("purple", "Alrededores");
        styleCatMap.put("purple-dot", "Alrededores");
        styleCatMap.put("yellow-dot", "Compras");
        styleCatMap.put("ylw-pushpin", "Compras");
        */
        FileTransform.transformKMLtoOV2(kmlFile, ov2Folder, nameSorted);
    }

}
