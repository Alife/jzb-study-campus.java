/**
 * 
 */
package com.jzb.kk.pp;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class HTMLReader {

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
            HTMLReader me = new HTMLReader();
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

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "fatal");
        System.setProperty("log4j.rootCategory", "FATAL");

         WebClient webClient = new WebClient();
        //WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_8, "cache.sscc.banesto.es", 8080);
        // DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
        // credentialsProvider.addProxyCredentials("username", "password");

        webClient.setJavaScriptEnabled(false);
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setThrowExceptionOnScriptError(false);
        webClient.setTimeout(10000);

        BBPageParser bbPageParser = new BBPageParser();
        Rect[] rects = _createRectangles();

        ArrayList<HtmlAnchor> OKList = new ArrayList<HtmlAnchor>();

        ArrayList<HtmlAnchor> mainList = new MainPageParser().parse(webClient, "http://www.chambresdhotes.org/english/");
        for (HtmlAnchor link1 : mainList) {

            String goodZones[] = { "Eure_Chambres_D_Hotesrigel" }; 
            boolean good = false;
            for (String zoneName : goodZones) {
                good |= link1.getHrefAttribute().contains(zoneName);
            }
            if (!good)
                continue;

            ArrayList<HtmlAnchor> zoneList = new ZonePageParser().parse(webClient, link1);
            for (HtmlAnchor link2 : zoneList) {
                BBInfo bbInfo = bbPageParser.parse(webClient, link2.getHrefAttribute());
                if (bbInfo.isOK) {
                    System.out.println("------ OK ------ B&B OK -> " + bbInfo.toExcel());
                    OKList.add(link2);
                    // if (isInRects(bbInfo, rects)) {
                    // System.out.println("!!!VALE!!");
                    // }
                }
            }
        }

        _printHTML(OKList);

        webClient.closeAllWindows();
    }

    private void _printHTML(ArrayList<HtmlAnchor> OKList) throws Exception {

        if (OKList.size() <= 0)
            return;

        File fout = new File("C:\\Users\\n63636\\Desktop\\bb.html");
        Tracer._info("Creating outpunt HTML file with links: " + fout);

        PrintStream out = new PrintStream(fout);
        out.println("<html><head></head><body>");
        for (HtmlAnchor link : OKList) {
            out.println("<a href=\""+link.getHrefAttribute()+"\">B&B - "+link.getTextContent().trim()+"</a><br>");
        }
        out.println("</body></html>");
        out.close();
    }

    private static class Rect {

        public double lat1, lng1, lat2, lng2;
        public String zone;

        public Rect(String zone, double lat1, double lng1, double lat2, double lng2) {

            if (lat1 > lat2) {
                this.lat1 = lat2;
                this.lat2 = lat1;
            } else {
                this.lat1 = lat1;
                this.lat2 = lat2;
            }

            if (lng1 > lng2) {
                this.lng1 = lng2;
                this.lng2 = lng1;
            } else {
                this.lng1 = lng1;
                this.lng2 = lng2;
            }

            this.zone = zone;
        }
    }

    private boolean isInRects(BBInfo bbInfo, Rect[] rects) {
        for (Rect r : rects) {
            if (bbInfo.gpsLat >= r.lat1 && bbInfo.gpsLat <= r.lat2 && bbInfo.gpsLng >= r.lng1 && bbInfo.gpsLng <= r.lng2) {
                bbInfo.zone = r.zone;
                return true;
            }
        }
        return false;
    }

    private Rect[] _createRectangles() {
        return new Rect[] {

        new Rect("Blois", 47.667, 1.016, 47.551, 1.419), new Rect("Amboise", 47.608, 0.824, 47.381, 1.072), new Rect("Tours", 47.440, 0.470, 47.317, 0.842) };

    }

}
