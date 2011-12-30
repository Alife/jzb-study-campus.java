/**
 * 
 */


import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.NVPair;

/**
 * @author n63636
 * 
 */
public class Test3 {

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
            Test1 me = new Test1();
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
    public void doIt1(String[] args) throws Exception {

        RSSData rss = new RSSData("Title - test1", "Descr - test1", "http://localhost/link-test1/");

        @SuppressWarnings("deprecation")
        Date d1 = new Date(2010, 6, 14, 10, 8, 00);

        for (int n = 1; n < 4; n++) {
            rss.addItem("item " + n + " - title", "item " + n + " - description", d1);
        }

        for (int n = 4; n < 8; n++) {
            rss.addItem("item " + n + " - title", "item " + n + " - description", new Date());
        }

        PrintStream ps = new PrintStream(new File("C:\\JZarzuela\\wiki-wamp\\www\\rss-test1.xml"));
        RSSWriter writer = new RSSWriter(rss, ps);
        writer.write();
        ps.close();

    }

    public void doIt(String[] args) throws Exception {

        HTTPConnection.setProxyServer("cache.sscc.banesto.es", 8080);

        URL url = new URL("http://apptrackr.org/json.php");
        //URL url = new URL("http://www.google.es/");
        HTTPConnection con = new HTTPConnection(url);
        NVPair nvp = new NVPair("request", "{\"getMaxPages\":true,\"page\":1,\"category\":1,\"appsPerPage\":60,\"sort\":2,\"search\":\"\",\"deviceid\":1}");
        System.out.println(nvp.getValue());
        HTTPResponse rsp = con.Post(url.getFile(), new NVPair[] { nvp });
        if (rsp.getStatusCode() >= 300) {
            System.err.println("Received Error: " + rsp.getReasonLine());
            System.err.println(rsp.getText());
        } else {
            String text = rsp.getText();
            System.out.println(text);
        }

    }
}
