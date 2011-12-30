/**
 * 
 */
package com.jzb.gdocs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.extensions.LastModifiedBy;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.ServiceException;
import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Des3Encrypter;

/**
 * @author n63636
 * 
 */
public class Test {

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
            Test me = new Test();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    public void doIt(String[] args) throws Exception {
        init();
        //fetchEntryData();
        //downloadDocument("183gpBfqxF70CH9yAAYZ3q-ysLzlhcPvQ7k2Nyr9shYM","C:\\WKSPs\\Ganymede-Java\\GDocs\\docs\\example1.doc","doc");
        downloadDocument("0Arl8KaZcGPFPdEpRRGZJSE1kR3VTRDgwZk5UcTRGOFE","C:\\WKSPs\\Ganymede-Java\\GDocs\\docs\\example1.xls","xls");
        
    }

    private DocsService m_client;

    private void init() throws Exception {

        DefaultHttpProxy.setDefaultProxy();
        
        m_client = new DocsService("yourCo-yourAppName-v1");
        m_client.setUserCredentials(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));
    }

    private void fetchEntryData() throws Exception {

        URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full?showfolders=true");
        // URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full/-/folder?showfolders=true");
        // URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full/folder%3Aroot/contents/-/folder");

        while (feedUrl != null) {

            DocumentListFeed feed = m_client.getFeed(feedUrl, DocumentListFeed.class);

            for (DocumentListEntry entry : feed.getEntries()) {
                // System.out.println(entry.getTitle().getPlainText());
                if(entry.getTitle().getPlainText().toLowerCase().contains("puente junio"))
                printEntry(entry);
            }

            feedUrl = (feed.getNextLink() == null) ? null : new URL(feed.getNextLink().getHref());
        }
    }

    public void downloadDocument(String resourceId, String filepath, String format) throws Exception {

        URL url = new URL("https://docs.google.com/feeds/download/documents/Export?docID=" + resourceId + "&exportFormat=" + format);
        downloadFile(url, filepath);
    }

    private void downloadFile(URL exportUrl, String filepath) throws Exception {

        byte buffer[] = new byte[65536];
        
        MediaContent mc = new MediaContent();
        mc.setUri(exportUrl.toString());
        MediaSource ms = m_client.getMedia(mc);

        InputStream inStream = null;
        FileOutputStream outStream = null;

        try {
            inStream = ms.getInputStream();
            outStream = new FileOutputStream(filepath);

            int l;
            while ((l = inStream.read(buffer)) != -1) {
                outStream.write(buffer,0,l);
            }
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
        }
    }

    public void doIt2(String[] args) throws Exception {

        DefaultHttpProxy.setDefaultProxy();
        
        DocsService client = new DocsService("yourCo-yourAppName-v1");
        client.setUserCredentials(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

        URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full?showfolders=true");
        // URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full/-/folder?showfolders=true");
        // URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full/folder%3Aroot/contents/-/folder");

        while (feedUrl != null) {

            DocumentListFeed feed = client.getFeed(feedUrl, DocumentListFeed.class, "W/\"CEANSH0zcCt7ImA9Wx5QFEw.\"");
            // DocumentListFeed feed = client.getFeed(feedUrl, DocumentListFeed.class, new DateTime(new Date(2001,1,1)));
            System.out.println(feed.getEtag());

            for (DocumentListEntry entry : feed.getEntries()) {
                // System.out.println(entry.getTitle().getPlainText());
                printEntry(entry);
            }

            feedUrl = (feed.getNextLink() == null) ? null : new URL(feed.getNextLink().getHref());
        }

    }

    public void printEntry(DocumentListEntry entry) {
        String resourceId = entry.getResourceId();
        String docType = entry.getType();

        System.out.println("'" + entry.getTitle().getPlainText() + "' (" + docType + ")");
        System.out.println("  link to Google Docs: " + entry.getDocumentLink().getHref());
        System.out.println("  resource id: " + resourceId);
        System.out.println("  doc id: " + entry.getDocId());

        // print the parent folder the document is in
        if (!entry.getParentLinks().isEmpty()) {
            System.out.println("  Parent folders: ");
            for (Link link : entry.getParentLinks()) {
                System.out.println("    --" + link.getTitle() + " - " + link.getHref());
            }
        }

        // print the timestamp the document was last viewed
        DateTime lastViewed = entry.getLastViewed();
        if (lastViewed != null) {
            System.out.println("  last viewed: " + lastViewed.toUiString());
        }

        // print who made the last modification
        LastModifiedBy lastModifiedBy = entry.getLastModifiedBy();
        if (lastModifiedBy != null) {
            System.out.println("  updated by: " + lastModifiedBy.getName() + " - " + lastModifiedBy.getEmail());
        }

        // Files such as PDFs take up quota
        if (entry.getQuotaBytesUsed() > 0) {
            System.out.println("Quota used: " + entry.getQuotaBytesUsed() + " bytes");
        }

        // print other useful metadata
        System.out.println("  last updated: " + entry.getUpdated().toUiString());
        System.out.println("  viewed by user? " + entry.isViewed());
        System.out.println("  writersCanInvite? " + entry.isWritersCanInvite().toString());
        System.out.println("  hidden? " + entry.isHidden());
        System.out.println("  starred? " + entry.isStarred());
        System.out.println("  trashed? " + entry.isTrashed());
        System.out.println();
    }
}
