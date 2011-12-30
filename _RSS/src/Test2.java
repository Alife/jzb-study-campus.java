/**
 * 
 */


import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jzb.util.DefaultHttpProxy;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.NVPair;

/**
 * @author n63636
 * 
 */
public class Test2 {

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
            Test2 me = new Test2();
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

        HashMap<String, IPAInfo> data = new HashMap<String, IPAInfo>();
        checkChanges(data);

    }

    private void checkChanges(HashMap<String, IPAInfo> data) throws Exception {

        int numPages = 0;
        int page = 1;
        do {
            System.out.println("Requesting info for page: " + page + " of " + numPages);
            JSONObject jo = getServerPage(page++);
            if (numPages == 0) {
                numPages = jo.getInt("numpages");
            }

            JSONArray pageInfo = jo.getJSONArray("pagedata");
            for (int n = 0; n < pageInfo.length(); n++) {
                JSONObject item = (JSONObject) pageInfo.get(n);
                IPAInfo currIPA = data.get(item.getString("id"));
                if (currIPA == null) {
                    putIPA(data, item);
                } else {
                    String lV = item.getString("latest_version");
                    String cV = currIPA.getLatestVersion();
                    if (!cV.equals(lV)) {
                        putIPA(data, item);
                    }
                }
            }

        } while (page <= numPages);

    }

    private void putIPA(HashMap<String, IPAInfo> data, JSONObject item) throws Exception {

        IPAInfo ipa = new IPAInfo();
        ipa.setId(item.getString("id"));
        ipa.setAddDate(item.getLong("add_date"));
        ipa.setCategory(item.getInt("category"));
        ipa.setDeviceid(item.getString("deviceid"));
        ipa.setIcon100(item.getString("icon100"));
        ipa.setIcon57(item.getString("icon57"));
        ipa.setIcon75(item.getString("icon75"));
        ipa.setLastModification(item.getLong("last_modification"));
        ipa.setLatestVersion(item.getString("latest_version"));
        ipa.setName(item.getString("name"));
        ipa.setSeller(item.getString("seller"));

        data.put(ipa.getId(), ipa);
    }

    private JSONObject getServerPage(int page) throws Exception {

        DefaultHttpProxy.setDefaultProxy();
        
        URL url = new URL("http://apptrackr.org");

        JSONObject rq = new JSONObject();
        rq.put("getMaxPages", true);
        rq.put("page", page);
        rq.put("category", 0);
        rq.put("appsPerPage", 60); // 15,30
        rq.put("sort", 3);
        rq.put("search", "");
        rq.put("deviceid", 0);

        HTTPConnection con = new HTTPConnection(url);
        NVPair params[] = { new NVPair("request", rq.toString()) };
        HTTPResponse rsp = con.Post("/json.php", params);
        JSONObject jo = new JSONObject(rsp.getText());
        if (!jo.getString("status").equalsIgnoreCase("OK")) {
            throw new Exception("Server response status was: " + jo.getString("status"));
        }

        return jo;
    }

    private void generateRSS() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String time_stamp = sdf.format(new Date());

        HashMap<String, IPAInfo> data = new HashMap<String, IPAInfo>();
        PrintStream ps = null;

        ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        ps.println("<rss version=\"2.0\">");
        ps.println("    <channel>");
        ps.println("        <title>Cambios en Apptrackr</title>");
        ps.println("        <link>http://localhost/Rss_AppTrackr_V2.0.xml</link>");
        ps.println("        <description>Descripcion del canal</description>");
        ps.println("        <lastBuildDate>" + time_stamp + "</lastBuildDate>");
        generateRSS_Item(time_stamp, ps);
        ps.println("    </channel>");
        ps.println("</rss>");
    }

    private void generateRSS_Item(String time_stamp, PrintStream ps) throws Exception {

        ps.println("<item>");
        ps.println("<lastBuildDate>" + time_stamp + "</lastBuildDate>");
        ps.println("<title>titulo de item 2</title>");
        ps.println("<description><![CDATA[");
        generateRSS_ItemDesc(ps);
        ps.println("<b>descripción</b> del item");
        ps.println("]]></description>");
        ps.println("</item>");
    }

    private void generateRSS_ItemDesc(PrintStream ps) throws Exception {

        ps.println("<b>descripción</b> del item");
    }
}
