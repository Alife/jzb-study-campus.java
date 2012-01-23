/**
 * 
 */
package com.jzb.at;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.jzb.at.api.AppTrkrAPI;
import com.jzb.futil.FileUtils;
import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;
import com.jzb.util.Tracer;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;

/**
 * @author n63636
 * 
 */
public class GetPrice {

    private static final int SOCKET_TIMEOUT = 30000;
    private static final int CHUNK_SIZE     = 5;

    private File             m_IPAsFolder;
    private AppTrkrAPI       m_api;

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
            GetPrice me = new GetPrice();
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

        m_IPAsFolder = new File("C:\\JZarzuela\\iPhone\\IPAs");
        m_api = new AppTrkrAPI(false);
        ArrayList<IPAData> list = checkUpdates();

        m_IPAsFolder = new File("C:\\JZarzuela\\iPhone\\tomtom-others-V1.8");
        list.addAll(checkUpdates());


        System.out.println("---------------------------------------------------------------------");
        System.out.println("\tforIPad\tid\tname\tbundle\tversion\tlegal\tprice");

        for (IPAData ipaData : list) {
            System.out.print("\t" + ipaData.forIPad);
            System.out.print("\t" + ipaData.id);
            System.out.print("\t" + ipaData.name);
            System.out.print("\t" + ipaData.bundle);
            System.out.print("\t" + ipaData.version);
            System.out.print("\t" + ipaData.legal);
            System.out.print("\t" + ipaData.price);
            System.out.println();
        }

        // AppInfo appInfo = getAppInfo("380293530");
        // System.out.println(appInfo);

    }

    public ArrayList<IPAData> checkUpdates() throws Exception {
        ArrayList<IPAData> list = _readIPAList();
        Tracer._debug("Searching updates:");
        _getAppsInfo(list);
        return list;
    }

    private void _getAppsInfo(ArrayList<IPAData> list) {

        int index1 = 0;
        int numChunks = list.size() / CHUNK_SIZE;
        if (list.size() % CHUNK_SIZE > 0) {
            numChunks++;
        }
        int chunkIndex = 0;
        while (index1 < list.size()) {
            int index2 = index1 + CHUNK_SIZE;
            if (index2 > list.size()) {
                index2 = list.size();
            }
            List<IPAData> chunk = list.subList(index1, index2);
            index1 += CHUNK_SIZE;
            chunkIndex++;

            Tracer._debug("...Checking chunk[" + chunkIndex + "/" + numChunks + "]");
            try {
                _getUpdateChunk(chunk);
            } catch (Exception ex) {
                Tracer._error("Error checking chunk[" + chunkIndex + "/" + numChunks + "]", ex);
                for (int n = 0; n < chunk.size(); n++) {
                    ArrayList newChunk = new ArrayList();
                    newChunk.add(chunk.get(n));
                    try {
                        Tracer._debug("Error checking chunk[" + chunkIndex + "/" + numChunks + "] piece by piece [" + n + "]");
                        _getUpdateChunk(newChunk);
                    } catch (Exception ex1) {
                        Tracer._error("Error checking chunk[" + chunkIndex + "/" + numChunks + "] piece by piece [" + n + "]", ex);
                    }
                }
            }
        }
    }

    private void _getUpdateChunk(List<IPAData> chunk) throws Exception {

        ArrayList<String> bundles = new ArrayList<String>();
        for (IPAData data : chunk) {
            bundles.add(data.bundle);
        }
        JSONObject jsonObj = m_api.getItunesIDs(bundles.toArray(new String[0]));

        for (IPAData data : chunk) {
            String id = jsonObj.getString(data.bundle);
            data.id = _parseLongSafely(id);
            data.price = _getAppPrice(id);
        }

    }

    private long _parseLongSafely(String s) {
        try {
            return Long.parseLong(s);
        } catch (Throwable th) {
            return -1;
        }

    }

    private String _getAppPrice(String appID) throws Exception {

        String html, price = null;

        html = _getAppHTML(appID, "/es"); // En euros
        if (html != null) {
            price = _parseHTMLAppPrice(html);
        } else {
            Tracer._error("Error parsing HTML to read 'PRICE' in Euros for ID = : " + appID);
        }

        if (price == null) {
            html = _getAppHTML(appID, ""); // En dollars
            if (html != null) {
                price = _parseHTMLAppPrice(html);
            } else {
                Tracer._error("Error parsing HTML to read 'PRICE' in Dollars for ID = : " + appID);
            }
        }

        if (price == null) {
            Tracer._error("Error parsing HTML to read 'PRICE' for ID = : " + appID);
        }

        return price;
    }

    private String _getAppHTML(String appID, String country) throws Exception {

        // Tracer._debug("Getting app price from HTML for ID = " + appID);

        URL apiURL = new URL("http://itunes.apple.com");
        HTTPConnection con = new HTTPConnection(apiURL);
        con.setTimeout(SOCKET_TIMEOUT);
        HTTPResponse rsp = con.Get(country + "/app/the-app-name/id" + appID);

        if (rsp.getStatusCode() == 200) {
            return rsp.getText();
        } else {
            return null;
        }
    }

    private String _parseHTMLAppPrice(String html) {

        String price = null;

        int p1, p2 = -1;
        p1 = html.indexOf("class=\"price\">");
        if (p1 > 0) {
            p2 = html.indexOf("</", p1);
            if (p2 > 0) {
                price = html.substring(p1 + 14, p2);
            }
        }

        return price;
    }

    private ArrayList<IPAData> _readIPAList() throws Exception {

        final ArrayList<IPAData> list = new ArrayList<IPAData>();

        IFileProcessor myProcessor = new IFileProcessor() {

            public void processFile(File f, File baseFolder) throws Exception {
                if (FileUtils.getExtension(f).equals("ipa")) {
                    String fname = f.getName();
                    IPAData ipaData = new IPAData();
                    ipaData.forIPad = NameComposer.isIPadIPA(fname);
                    ipaData.name = NameComposer.parseName(fname);
                    ipaData.bundle = NameComposer.parsePkg(fname);
                    ipaData.version = NameComposer.parseVer(fname);
                    ipaData.legal = NameComposer.isLegalIPA(fname);
                    list.add(ipaData);
                }
            }

            public void setFolderIterator(FolderIterator fi) {
            }
        };
        FolderIterator fi = new FolderIterator(myProcessor, m_IPAsFolder);
        fi.iterate();

        return list;
    }
}
