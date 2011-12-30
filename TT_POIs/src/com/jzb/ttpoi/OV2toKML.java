/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileWriter;
import com.jzb.ttpoi.wl.OV2FileLoader;

/**
 * @author n63636
 * 
 */
public class OV2toKML {

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
            OV2toKML me = new OV2toKML();
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
        
        File folder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\tmp");

        for (File ov2File : folder.listFiles()) {

            if (!ov2File.getName().toLowerCase().endsWith(".ov2"))
                continue;

            
            System.out.println("Processing OV2: " + ov2File);
            int p1 = ov2File.getName().lastIndexOf('.');
            String kmlName = ov2File.getName().substring(0, p1)+".kml";

            TPOIFileData info = OV2FileLoader.loadFile(ov2File);
            
            for(TPOIData poi:info.getAllPOIs()) {
                if(Character.isDigit(poi.getName().charAt(0))) {
                    int n=0;
                    while(Character.isDigit(poi.getName().charAt(n)) || Character.isWhitespace(poi.getName().charAt(n))) {
                        n++;
                    }
                    poi.setName(poi.getName().substring(n));
                    poi.toString();
                }
            }

            Comparator<TPOIData> comp = new Comparator<TPOIData>() {
                public int compare(TPOIData o1, TPOIData o2) {
                    return o1.getName().compareTo(o2.getName());
                }
                
            };
            
            Collections.sort(info.getAllPOIs(),comp);
            
            File kmlFile = new File(folder, kmlName);
            KMLFileWriter.saveFile(kmlFile, info);

        }


    }
}
