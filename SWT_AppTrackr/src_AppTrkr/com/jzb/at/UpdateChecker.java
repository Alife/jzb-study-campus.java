/**
 * 
 */
package com.jzb.at;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.jzb.at.api.AppTrkrAPI;
import com.jzb.futil.FileUtils;
import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class UpdateChecker {

    private static final int CHUNK_SIZE = 5;
    private AppTrkrAPI       m_api;
    private File             m_IPAsFolder;

    public UpdateChecker(File IPAsFolder, boolean debug) {
        m_IPAsFolder = IPAsFolder;
        m_api = new AppTrkrAPI(debug);
    }

    public ArrayList<IPAData> checkUpdates() throws Exception {
        ArrayList<IPAData> list = _readIPAList();
        Tracer._debug("Searching updates:");
        _getUpdateInfo(list);
        return list;
    }

    private void _getUpdateChunk(List<IPAData> chunk) throws Exception {

        ArrayList<Long> ids = new ArrayList<Long>();
        ArrayList<String> bundles = new ArrayList<String>();

        for (IPAData data : chunk) {
            bundles.add(data.bundle);
        }

        JSONObject jsonObj = m_api.getItunesIDs(bundles.toArray(new String[0]));

        for (IPAData data : chunk) {
            long id = _parseLongSafely(jsonObj.getString(data.bundle));
            data.id = id;
            ids.add(data.id);
        }

        jsonObj = m_api.getUpdateInfo(ids.toArray(new Long[0]));

        JSONObject jsonVers = jsonObj.getJSONObject("versions");
        for (IPAData data : chunk) {
            String ver = jsonVers.getString(Long.toString(data.id));
            if (NameComposer.compareVersion(ver, data.version) > 0) {
                data.updated = true;
            }
            data.newVersion = ver;

            if (data.updated) {
                try {
                    data.appInfo = m_api.getAppDetails(data.id);
                    JSONObject links=m_api.getLinks(data.id);
                    if(links!=null) {
                        data.linksInfo = links.getJSONObject("links").getJSONArray(ver);
                    }
                } catch (Exception e) {
                    Tracer._error(e.getMessage());
                }
            }
        }

    }

    private void _getUpdateInfo(ArrayList<IPAData> list) {

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

    private long _parseLongSafely(String s) {
        try {
            return Long.parseLong(s);
        } catch (Throwable th) {
            return -1;
        }

    }

    private ArrayList<IPAData> _readIPAList() throws Exception {

        final ArrayList<IPAData> list = new ArrayList<IPAData>();

        IFileProcessor myProcessor = new IFileProcessor() {

            public void processFile(File f) throws Exception {
                if (FileUtils.getExtension(f).equals("ipa")) {
                    String fname = f.getName();
                    IPAData ipaData = new IPAData();
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

    public String getUpdateInfoInHTML(List<IPAData> list) throws Exception {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<html><head><title>Update Info</title></head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><body><table border=\"1\">");
        pw.println("<tr>");
        pw.println("<th>Icono</th>");
        pw.println("<th>Nombre</th>");
        pw.println("<th>V. Actual</th>");
        pw.println("<th>V. AppTrkr</th>");
        pw.println("<th>V. iTunes</th>");
        pw.println("<th>F. Modif.</th>");
        pw.println("<th>Links</th>");
        pw.println("<th>Novedades</th>");
        pw.println("</tr>");
        for (IPAData data : list) {
            if (data.appInfo != null) {
                pw.println("<tr>");
                pw.println("<td><img src=\"" + data.appInfo.getString("icon100") + "\"/></td>");
                pw.println("<td>" + data.appInfo.getString("name") + "</td>");
                pw.println("<td>" + data.version + "</td>");
                pw.println("<td>" + data.newVersion + "</td>");
                pw.println("<td>" + data.appInfo.getString("latest_version") + "</td>");
                pw.println("<td>" + data.appInfo.getString("release_date") + "</td>");

                if (data.linksInfo != null) {
                    pw.println("<td>");
                    pw.println("<ul>");
                    for(int n=0;n<data.linksInfo.length();n++) {
                        pw.println("<li>");
                        //pw.println("<a href=\""+data.linksInfo.getJSONObject(n).getString("url")+"\">"+data.linksInfo.getJSONObject(n).getString("shorthand")+" - "+data.linksInfo.getJSONObject(n).getString("cracker")+"</a>");
                        pw.println(data.linksInfo.getJSONObject(n).getString("url"));
                    }
                    pw.println("</ul>");
                    pw.println("</td>");
                    
                    pw.println("<td><pre>" + data.appInfo.getString("whatsnew") + "</pre></td>");
                } else {
                    pw.println("<td>-</td>");
                }

                pw.println("</tr>");
            } else if (data.updated) {
                pw.println("<tr>");
                pw.println("<td>-</td>");
                pw.println("<td>" + data.name + "</td>");
                pw.println("<td>X</td>");
                pw.println("<td>" + data.newVersion + "</td>");
                pw.println("<td>-</td>");
                pw.println("<td>-</td>");
                pw.println("<td>-</td>");
                pw.println("<td>No se pudo recuperar la informacion</td>");
                pw.println("</tr>");
            }
        }
        pw.println("</table></body></html>");

        return sw.toString();
    }

}
