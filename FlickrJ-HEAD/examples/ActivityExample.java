import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.activity.ActivityInterface;
import com.aetrion.flickr.activity.Event;
import com.aetrion.flickr.activity.Item;
import com.aetrion.flickr.activity.ItemList;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.util.IOUtilities;

/**
 * Demonstration of howto use the ActivityInterface.
 *
 * @author mago
 * @version $Id: ActivityExample.java,v 1.3 2008/07/05 22:19:48 x-mago Exp $
 */
public class ActivityExample {
    static String apiKey;
    static String sharedSecret;
    Flickr f;
    REST rest;
    RequestContext requestContext;
    Properties properties = null;

    public ActivityExample()
      throws ParserConfigurationException, IOException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/setup.properties");
            properties = new Properties();
            properties.load(in);
        } finally {
            IOUtilities.close(in);
        }
        f = new Flickr(
            properties.getProperty("apiKey"),
            properties.getProperty("secret"),
            new REST(0,5,5)
        );
        requestContext = RequestContext.getRequestContext();
        Auth auth = new Auth();
        auth.setPermission(Permission.READ);
        auth.setToken(properties.getProperty("token"));
        requestContext.setAuth(auth);
        Flickr.debugRequest = false;
        Flickr.debugStream = false;
    }

    public void showActivity() throws FlickrException, IOException, SAXException {
        ActivityInterface iface = f.getActivityInterface();
        ItemList list = iface.userComments(10, 0);
        for (int j = 0; j < list.size(); j++) {
            Item item = (Item) list.get(j);
            Flickr._trace("Item " + (j + 1) + "/" + list.size() + " type: " + item.getType());
            Flickr._trace("Item-id:       " + item.getId() + "\n");
            ArrayList events = (ArrayList) item.getEvents();
            for (int i = 0; i < events.size(); i++) {
                Flickr._trace("Event " + (i + 1) + "/" + events.size() + " of Item " + (j + 1));
                Flickr._trace("Event-type: " + ((Event) events.get(i)).getType());
                Flickr._trace("User:       " + ((Event) events.get(i)).getUser());
                Flickr._trace("Username:   " + ((Event) events.get(i)).getUsername());
                Flickr._trace("Value:      " + ((Event) events.get(i)).getValue() + "\n");
            }
        }
        ActivityInterface iface2 = f.getActivityInterface();
        list = iface2.userPhotos(50, 0, "300d");
        for (int j = 0; j < list.size(); j++) {
            Item item = (Item) list.get(j);
            Flickr._trace("Item " + (j + 1) + "/" + list.size() + " type: " + item.getType());
            Flickr._trace("Item-id:       " + item.getId() + "\n");
            ArrayList events = (ArrayList) item.getEvents();
            for (int i = 0; i < events.size(); i++) {
                Flickr._trace("Event " + (i + 1) + "/" + events.size() + " of Item " + (j + 1));
                Flickr._trace("Event-type: " + ((Event) events.get(i)).getType());
                if (((Event) events.get(i)).getType().equals("note")) {
                    Flickr._trace("Note-id:    " + ((Event) events.get(i)).getId());
                } else if (((Event) events.get(i)).getType().equals("comment")) {
                    Flickr._trace("Comment-id: " + ((Event) events.get(i)).getId());
                }
                Flickr._trace("User:       " + ((Event) events.get(i)).getUser());
                Flickr._trace("Username:   " + ((Event) events.get(i)).getUsername());
                Flickr._trace("Value:      " + ((Event) events.get(i)).getValue());
                Flickr._trace("Dateadded:  " + ((Event) events.get(i)).getDateadded() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        try {
            ActivityExample t = new ActivityExample();
            t.showActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
