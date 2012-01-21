/**
 * 
 */

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.google.api.gbase.client.FeedURLFactory;
import com.google.gdata.client.http.HttpGDataRequest;
import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.Content;
import com.google.gdata.data.Link;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.maps.FeatureEntry;
import com.google.gdata.data.maps.FeatureFeed;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.data.maps.MapFeed;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;
import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Des3Encrypter;

/**
 * @author n63636
 * 
 */
public class TestGMaps {

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
            TestGMaps me = new TestGMaps();
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

        DefaultHttpProxy.setDefaultProxy();

        // System.setProperty("java.util.logging.config.file", "C:\\JZarzuela\\_git_repos\\java-campus\\SWT_Travel POIs\\src_Model\\logging.properties");
        // LogManager.getLogManager().readConfiguration();

        MapsService myService = new MapsService("yourCo-yourAppName-v1");
        myService.setUserCredentials(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

        // System.out.println("----------------------------------------------------------------------");
        // getUserMaps(myService);

        // System.out.println("----------------------------------------------------------------------");
        // getMapInfo(myService);

        // System.out.println("----------------------------------------------------------------------");
        // updateMap(myService);

        // System.out.println("----------------------------------------------------------------------");
        // createMap(myService);

        // System.out.println("----------------------------------------------------------------------");
        // getFeatures(myService);

        // System.out.println("----------------------------------------------------------------------");
        // getFeatureInfo(myService);

        // System.out.println("----------------------------------------------------------------------");
        // updateFeature(myService);

        // System.out.println("----------------------------------------------------------------------");
        // getFeatureInfo(myService);

        // System.out.println("----------------------------------------------------------------------");
        // createFeature(myService);

        System.out.println("----------------------------------------------------------------------");
        batchUpdateMap(myService);

    }

    private void _dumpProperties(Object obj, String... tabspace) {

        String tabs;
        if (tabspace.length == 0)
            tabs = "";
        else
            tabs = tabspace[0];

        Method methods[] = obj.getClass().getMethods();
        for (Method m : methods) {

            if (!m.getName().startsWith("get") || m.getParameterTypes().length != 0)
                continue;

            if (m.getName().equals("getClass"))
                continue;

            System.out.print(tabs + m.getName() + " ");

            try {
                Object val = m.invoke(obj, (Object[]) null);

                if (val != null) {
                    System.out.print("-> ");

                    if (m.getName().equals("getLinks")) {
                        System.out.println("");
                        for (Link l : (List<Link>) val) {
                            System.out.println(tabs + "    " + l.getRel() + " - " + l.getTitle() + " - " + l.getHref());
                        }
                    } else if (val instanceof Content) {
                        System.out.println();
                        System.out.println(tabs + "==============================");
                        _dumpProperties(val, tabs + "    ");
                        System.out.println(tabs + "==============================");
                    } else if (val instanceof XmlBlob) {
                        System.out.println();
                        System.out.println(tabs + "------------------------------");
                        _dumpProperties(val, tabs + "    ");
                        System.out.println(tabs + "------------------------------");
                    } else if (val instanceof Link || val instanceof PlainTextConstruct) {
                        System.out.println();
                        _dumpProperties(val, tabs + "    ");
                    } else {
                        System.out.println(val.toString());
                    }
                } else {
                    System.out.println("-> NULL");
                }
            } catch (Throwable th) {
                System.out.println("#error#");
                // th.printStackTrace(System.err);
            }
        }

    }

    private void batchUpdateMap(MapsService myService) throws ServiceException, IOException {

        // P-1
        // http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/0004b6dcc0c5982a33c95
        // P-2
        // http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/0004b6dcc0c5ae8feaf7a

        FeatureFeed ff1 = new FeatureFeed();
        FeatureEntry fe1 = new FeatureEntry();
        fe1.setId("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/0004b6dcc0c5982a33c95");

        BatchUtils.setBatchId(fe1, "P-1-Id");
        // BatchUtils.setBatchOperationType(fe1, BatchOperationType.QUERY);
        BatchUtils.setBatchOperationType(fe1, BatchOperationType.QUERY);

        ff1.getEntries().add(fe1);
        final URL batchMapUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/batch");
        FeatureFeed result = myService.batch(batchMapUrl, ff1);

        FeatureFeed ff2 = new FeatureFeed();
        for (FeatureEntry entry : result.getEntries()) {
            String batchId = BatchUtils.getBatchId(entry);
            BatchStatus status = BatchUtils.getBatchStatus(entry);
            System.out.println(batchId + " status (" + status.getReason() + ") " + status.getContent());

            // _dumpProperties(entry);

            FeatureEntry fe2 = new FeatureEntry(entry);
            fe2.setTitle(new PlainTextConstruct("P-1-retocado"));
            XmlBlob kml = new XmlBlob();
            kml.setBlob("<Placemark><name>P-1-retocado</name><description/><Style><IconStyle><Icon><href>http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png</href></Icon></IconStyle></Style><Point><coordinates>-5.812011,42.21244,0.0</coordinates></Point></Placemark>");
            fe2.setKml(kml);
            BatchUtils.setBatchId(fe2, "P-1-Id");
            BatchUtils.setBatchOperationType(fe2, BatchOperationType.UPDATE);
            fe2.setId("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/0004b6dcc0c5982a33c95");
            ff2.setEtag("pepe");
            System.out.println(fe2.isImmutable());
            ff2.getEntries().add(fe2);
            // _dumpProperties(fe2);
        }

        result = myService.batch(batchMapUrl, ff2);

        /*
         * 
         * final URL featureEntryUrl_P1 = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/0004b6dcc0c5982a33c95"); FeatureEntry entryP1 =
         * myService.getEntry(featureEntryUrl_P1, FeatureEntry.class); entryP1.setTitle(new PlainTextConstruct("P-1-retocado")); BatchUtils.setBatchOperationType(entryP1, BatchOperationType.UPDATE);
         * BatchUtils.setBatchId(entryP1, "P-1-Id");
         * 
         * final URL featureEntryUrl_P2 = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/0004b6dcc0c5ae8feaf7a"); FeatureEntry entryP2 =
         * myService.getEntry(featureEntryUrl_P2, FeatureEntry.class); entryP2.setTitle(new PlainTextConstruct("P-2-borrado")); BatchUtils.setBatchOperationType(entryP2, BatchOperationType.UPDATE);
         * BatchUtils.setBatchId(entryP2, "P-2-Id");
         * 
         * // FeatureEntry entryP3 = new FeatureEntry(); // entryP3.setTitle(new PlainTextConstruct("P-3-retocado")); // XmlBlob kml = new XmlBlob(); // kml.setBlob(
         * "<Placemark><name>P-3-new</name><description/><Style><IconStyle><Icon><href>http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png</href></Icon></IconStyle></Style><Point><coordinates>-5.812011,42.21244,0.0</coordinates></Point></Placemark>"
         * ); // entryP3.setKml(kml); // BatchUtils.setBatchOperationType(entryP3, BatchOperationType.INSERT); // BatchUtils.setBatchId(entryP3, "P-3-Id");
         * 
         * ArrayList<FeatureEntry> entries = new ArrayList<FeatureEntry>(); entries.add(entryP1); entries.add(entryP2); // entries.add(entryP3); FeatureFeed bfeed = new FeatureFeed();
         * bfeed.setEntries(entries); bfeed.setService(myService);
         * 
         * // getFeedBatchLink() final URL batchMapUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/batch"); FeatureFeed result =
         * myService.batch(batchMapUrl, bfeed);
         */
        for (FeatureEntry entry : result.getEntries()) {
            String batchId = BatchUtils.getBatchId(entry);
            BatchStatus status = BatchUtils.getBatchStatus(entry);
            System.out.println(batchId + " status (" + status.getReason() + ") " + status.getContent());
            if (BatchUtils.isSuccess(entry)) {
                _dumpProperties(entry);

            } else {
                System.err.println(batchId + " failed");
                // _dumpProperties(entry);
            }
        }
    }

    private FeatureEntry createFeature(MapsService myService) throws ServiceException, IOException {

        // Specify the KML as a String
        String kmlStr = "<Placemark xmlns=\"http://www.opengis.net/kml/2.2\">" + "<name>Aunt Joan's Ice Cream Shop</name>" + "<Point>"
                + "<coordinates>-87.74613826475604,41.90504663195118,0</coordinates>" + "</Point></Placemark>";

        // Use the feature feed's #post URL as the Edit URL for this map
        // Replace userID and mapID with appropriate values for your map

        final URL featureEditUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b5a757bcd80415f84/full");

        // Create a blank FeatureEntry object
        FeatureEntry featureEntry = new FeatureEntry();

        try {
            // KML is simply XML so we'll use the XmlBlob object to store it
            XmlBlob kml = new XmlBlob();
            kml.setBlob(kmlStr);

            // Set the KML for this feature
            // Note that the KML should only include one <Placemark> entry
            featureEntry.setKml(kml);

            // Set the tile for the feature. Each atom entry requires a feature
            // This feature title will be displayed within My Maps
            // featureEntry.setTitle(new PlainTextConstruct("Feature Title"));
        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getClass().getName());
        }

        // Insert the feature entry using the #post URL
        return myService.insert(featureEditUrl, featureEntry);
    }

    private MapEntry createMap(MapsService myService) throws ServiceException, IOException {

        // Replace the following URL with your metafeed's POST (Edit) URL
        final URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/212026791974164037226/full");
        MapFeed resultFeed = myService.getFeed(feedUrl, MapFeed.class);
        URL mapUrl = new URL(resultFeed.getEntryPostLink().getHref());

        // Create a MapEntry object
        MapEntry myEntry = new MapEntry();
        myEntry.setTitle(new PlainTextConstruct("@@Test-2"));
        myEntry.setSummary(new PlainTextConstruct("Summary"));
        Person author = new Person("Maps Developer", null, "example@gmail.com");
        // myEntry.getAuthors().add(author);

        MapEntry newMapEntry = myService.insert(mapUrl, myEntry);

        System.out.println("new map = " + newMapEntry.getEditLink().getHref());

        return newMapEntry;
    }

    private void getFeatureInfo(MapsService myService) throws ServiceException, IOException {

        // Get a feature entry from its self URL (returned in the feature feed)
        final URL featureEntryUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b5a757bcd80415f84/full/0004b5a758bb312533d26");

        FeatureEntry entry = myService.getEntry(featureEntryUrl, FeatureEntry.class);

        // Print out the KML
        try {
            _dumpProperties(entry);
            // XmlBlob kml = ((OtherContent) entry.getContent()).getXml();
            // System.out.println(kml.getBlob());
        } catch (NullPointerException e) {
            System.out.println("Null Pointer Exception");
        }
    }

    private void getFeatures(MapsService myService) throws ServiceException, IOException {

        // Get a feature feed for a specific map
        final URL featureFeedUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full");
        FeatureFeed featureFeed = myService.getFeed(featureFeedUrl, FeatureFeed.class);

        System.out.println("Features of the Map:");

        for (int i = 0; i < featureFeed.getEntries().size(); i++) {
            FeatureEntry entry = featureFeed.getEntries().get(i);

            // Print the title of the feed itself.
            System.out.println("\nTitle: " + entry.getTitle().getPlainText());
            System.out.println("Self Link: " + entry.getSelfLink().getHref());
            System.out.println("ID: " + entry.getId());

            System.out.println("KML: \n");

            // Print out the KML
            try {
                // Note that the KML is set to type XmlBlob
                XmlBlob kml = entry.getKml();
                System.out.println(kml.getBlob());
            } catch (NullPointerException e) {
                System.out.println("Error: " + e.getClass().getName());
            }
        }
    }

    private void getMapInfo(MapsService myService) throws ServiceException, IOException {

        // Request the self link on a particular map
        // Note that you should replace the URL to a self link of
        // a particular map
        final URL mapSelfUrl = new URL("http://maps.google.com/maps/feeds/maps/212026791974164037226/full/0004b6dcbec20d9576aad");
        MapEntry map = myService.getEntry(mapSelfUrl, MapEntry.class);

        _dumpProperties(map);

        // System.out.println("\t" + map.getSelfLink().getHref());
        // System.out.println(map.getTitle().getPlainText());
        // System.out.println(map.getSummary().getPlainText());
    }

    private void getUserMaps(MapsService myService) throws ServiceException, IOException {

        // Request the default metafeed
        final URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/default/full");
        MapFeed resultFeed = myService.getFeed(feedUrl, MapFeed.class);
        System.out.println(resultFeed.getFeedBatchLink().getHref());
        // _dumpProperties(resultFeed);

        // Print the title of the feed itself.
        System.out.println(resultFeed.getTitle().getPlainText());

        // Iterate through the feed entries (maps) and print out info
        // for each map
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
            MapEntry entry = resultFeed.getEntries().get(i);
            System.out.println("\n------------------------------------------------------------------");
            // _dumpProperties(entry);
            System.out.println(entry.getTitle().getPlainText());
            System.out.println(entry.getEditLink().getHref());
            // for (Person p : entry.getAuthors()) {
            // System.out.println(p.getName());
            // System.out.println(p.getEmail());
            // System.out.println(p.getUri());
            // System.out.println();
            // }
        }
    }

    private FeatureEntry updateFeature(MapsService myService) throws ServiceException, IOException {

        // Specify the KML as a String
        String kmlStr = "<Placemark xmlns=\"http://www.opengis.net/kml/2.2\">" + "<name>Otro Nombre 4</name><description>kk de la vaca</description>" + "<Point>"
                + "<coordinates>-87.72613826475604,41.91504663195118,0</coordinates>" + "</Point></Placemark>";

        // Request the default metafeed
        // Replace userID, mapID and featureID with appropriate values for your map

        final URL featureEditUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b6dcbec20d9576aad/full/0004b6dcc0c5982a33c95");
        FeatureEntry featureEntry = myService.getEntry(featureEditUrl, FeatureEntry.class);
        featureEntry.setTitle(new PlainTextConstruct("Otro Nombre 2"));
        featureEntry.setSummary(new PlainTextConstruct("que sera esto?"));

        // KML is simply XML so we'll use the XmlBlob object to store it
        XmlBlob kml = new XmlBlob();
        kml.setBlob(kmlStr);

        // Set the KML for this feature
        // Note that the KML should only include one <Placemark> entry
        featureEntry.setKml(kml);

        FeatureEntry fe = myService.update(featureEditUrl, featureEntry);
        _dumpProperties(fe);
        return fe;
    }

    private MapEntry updateMap(MapsService myService) throws ServiceException, IOException {

        // Use the map entry's post (edit) URL
        final URL editMapUrl = new URL("http://maps.google.com/maps/feeds/maps/212026791974164037226/full/0004b6dcbec20d9576aad");

        // Create a MapEntry object and replace the title to 'Demo Map 2'
        MapEntry myEntry = new MapEntry();
        myEntry.setTitle(new PlainTextConstruct("@@Test-2-12"));
        return myService.update(editMapUrl, myEntry);
    }
}
