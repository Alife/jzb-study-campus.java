/**
 * 
 */

import java.io.IOException;
import java.net.URL;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.OtherContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.maps.FeatureEntry;
import com.google.gdata.data.maps.FeatureFeed;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.data.maps.MapFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;
import com.jzb.util.Des3Encrypter;

/**
 * @author n63636
 * 
 */
public class TestGMaps2 {

    public static FeatureEntry createFeature(MapsService myService) throws ServiceException, IOException {

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

    public static void getFeatures(MapsService myService) throws ServiceException, IOException {

        // Get a feature feed for a specific map
        final URL featureFeedUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b5a757bcd80415f84/full");
        FeatureFeed featureFeed = myService.getFeed(featureFeedUrl, FeatureFeed.class);

        System.out.println("Features of the Map:");

        for (int i = 0; i < featureFeed.getEntries().size(); i++) {
            FeatureEntry entry = featureFeed.getEntries().get(i);

            // Print the title of the feed itself.
            System.out.println("\nTitle: " + entry.getTitle().getPlainText());
            System.out.println("Self Link: " + entry.getSelfLink().getHref());

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

    public static void getMapInfo(MapsService myService) throws ServiceException, IOException {

        // Request the self link on a particular map
        // Note that you should replace the URL to a self link of
        // a particular map
        final URL mapSelfUrl = new URL("http://maps.google.com/maps/feeds/maps/212026791974164037226/full/0004b5a757bcd80415f84");
        MapEntry map = myService.getEntry(mapSelfUrl, MapEntry.class);

        System.out.println("\t" + map.getSelfLink().getHref());
        System.out.println(map.getTitle().getPlainText());
        System.out.println(map.getSummary().getPlainText());
    }

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
            TestGMaps2 me = new TestGMaps2();
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

    public static void printFeatureInfo(MapsService myService) throws ServiceException, IOException {

        // Get a feature entry from its self URL (returned in the feature feed)
        final URL featureEntryUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b5a757bcd80415f84/full/0004b5a758bb312533d26");

        FeatureEntry entry = myService.getEntry(featureEntryUrl, FeatureEntry.class);

        // Print out the KML
        try {
            System.out.println(entry.getCanEdit());
            System.out.println(entry.getEdited());
            System.out.println(entry.getEditLink().getHref());
            System.out.println(entry.getUpdated());
            System.out.println(entry.getTitle());

            XmlBlob kml = ((OtherContent) entry.getContent()).getXml();
            System.out.println(kml.getBlob());
        } catch (NullPointerException e) {
            System.out.println("Null Pointer Exception");
        }
    }

    public static void printUserMaps(MapsService myService) throws ServiceException, IOException {

        // Request the default metafeed
        final URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/default/full");
        MapFeed resultFeed = myService.getFeed(feedUrl, MapFeed.class);

        // Print the title of the feed itself.
        System.out.println(resultFeed.getTitle().getPlainText());

        // Iterate through the feed entries (maps) and print out info
        // for each map
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
            MapEntry entry = resultFeed.getEntries().get(i);
            System.out.println(entry.getTitle().getPlainText());
            System.out.println("  Summary: " + entry.getSummary().getPlainText());
            System.out.println("  Self Link: " + entry.getSelfLink().getHref() + "\n");
        }
    }

    public static FeatureEntry updateFeature(MapsService myService) throws ServiceException, IOException {

        // Specify the KML as a String
        String kmlStr = "<Placemark xmlns=\"http://www.opengis.net/kml/2.2\">" + "<name>Otro Nombre 4</name><description>kk de la vaca</description>" + "<Point>"
                + "<coordinates>-87.72613826475604,41.91504663195118,0</coordinates>" + "</Point></Placemark>";

        // Request the default metafeed
        // Replace userID, mapID and featureID with appropriate values for your map
        final URL featureEditUrl = new URL("http://maps.google.com/maps/feeds/features/212026791974164037226/0004b5a757bcd80415f84/full/0004b5a758bb312533d26");
        FeatureEntry featureEntry = myService.getEntry(featureEditUrl, FeatureEntry.class);
        // featureEntry.setTitle(new PlainTextConstruct("Otro Nombre 2"));
        featureEntry.setSummary(new PlainTextConstruct("que sera esto?"));

        // KML is simply XML so we'll use the XmlBlob object to store it
        XmlBlob kml = new XmlBlob();
        kml.setBlob(kmlStr);

        // Set the KML for this feature
        // Note that the KML should only include one <Placemark> entry
        featureEntry.setKml(kml);

        return myService.update(featureEditUrl, featureEntry);
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

        MapsService myService = new MapsService("yourCo-yourAppName-v1");
        myService.setUserCredentials(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

        // System.out.println("----------------------------------------------------------------------");
        // printUserMaps(myService);

        // System.out.println("----------------------------------------------------------------------");
        // getMapInfo(myService);

        // System.out.println("----------------------------------------------------------------------");
        // getFeatures(myService);

        System.out.println("----------------------------------------------------------------------");
        printFeatureInfo(myService);

        System.out.println("----------------------------------------------------------------------");
        updateFeature(myService);

        System.out.println("----------------------------------------------------------------------");
        printFeatureInfo(myService);

        System.out.println("----------------------------------------------------------------------");
        createFeature(myService);
    }
}
