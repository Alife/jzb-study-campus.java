/**
 * 
 */
package com.jzb.ttpoi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.data.maps.MapFeed;
import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Des3Encrypter;

/**
 * @author n63636
 * 
 */
public class KMLDownload {

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
            KMLDownload me = new KMLDownload();
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

        DefaultHttpProxy.setDefaultProxy();

        File kmlFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_");
        kmlFolder.mkdirs();

        HashMap<String, URL> mapList = _getMapLinks();
        for (Map.Entry<String, URL> entry : mapList.entrySet()) {
            _downloadMap(kmlFolder, entry.getKey(), entry.getValue());
        }
    }

    
    private void _downloadMap(File baseFolder, String mapName, URL link) throws Exception {
        System.out.println();
        System.out.println("Downloading map '" + mapName + "' from '" + link + "'");

        byte buffer[] = new byte[65536];
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(baseFolder, mapName + ".kml")));
        BufferedInputStream bis = new BufferedInputStream(link.openStream());
        for (;;) {
            int len = bis.read(buffer);
            if (len > 0) {
                bos.write(buffer, 0, len);
            } else {
                break;
            }
        }
        bis.close();
        bos.close();

    }

    private HashMap<String, URL> _getMapLinks() throws Exception {

        HashMap<String, URL> mapList = new HashMap<String, URL>();

        MapsService myService = new MapsService("listAllMaps");
        myService.setUserCredentials(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

        final URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/default/full");

        MapFeed resultFeed = myService.getFeed(feedUrl, MapFeed.class);
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {

            MapEntry entry = resultFeed.getEntries().get(i);
            String mapName = entry.getTitle().getPlainText();
            String selfLink = entry.getSelfLink().getHref();

            int p1 = selfLink.lastIndexOf("/maps/");
            int p2 = selfLink.indexOf("/full/", p1);

            String part1 = selfLink.substring(p1 + 6, p2);
            String part2 = selfLink.substring(p2 + 6);

            String mapURL = "http://maps.google.es/maps/ms?hl=es&ie=UTF8&vps=3&jsv=304e&oe=UTF8&msa=0&msid=" + part1 + "." + part2 + "&output=kml";

            System.out.println(mapName + " => " + mapURL);
            mapList.put(mapName, new URL(mapURL));
        }

        return mapList;
    }

}
