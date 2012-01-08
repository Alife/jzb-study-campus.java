/**
 * 
 */
package com.jzb.tpoi.srvc;

import java.net.URL;
import java.util.ArrayList;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.HtmlTextConstruct;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.maps.FeatureEntry;
import com.google.gdata.data.maps.FeatureFeed;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.data.maps.MapFeed;
import com.jzb.tpoi.data.ExtendedInfo;
import com.jzb.tpoi.data.TDateTime;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TMapFigure;
import com.jzb.tpoi.data.TPoint;
import com.jzb.tpoi.data.TUnknown;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class GMapService {

    public static GMapService  inst = new GMapService();

    private static MapsService m_srvcClient;
    private static URL         m_URL_createUserMap;
    private static String      m_user;

    // ---------------------------------------------------------------------------------
    public GMapService() {

        m_srvcClient = new MapsService("yourCo-yourAppName-v1");
        m_srvcClient.setConnectTimeout(20000);
    }

    // ---------------------------------------------------------------------------------
    // Da de alta un nuevo mapa con la infomación pasada
    // OJO: El ID del mapa, puesto que es nuevo, se debe actualizar para que se pueda actualizar luego
    // Lo mismo pasa con los tiempos de creación y actualización
    public void createMap(TMap map) throws Exception {

        Tracer._debug("GMapService - createMap: " + (map != null ? map.getId() : "null"));
        
        // Create a MapEntry object
        MapEntry mapEntry = _fill_GMapEntry_From_Map(map);

        // Calls google service
        MapEntry newMapEntry = m_srvcClient.insert(_get_URL_CreateUserMap(), mapEntry);

        // Update map info from new entry created
        _fill_Map_From_GMapEntry(map, newMapEntry);
    }

    // ---------------------------------------------------------------------------------
    public void deleteMap(TMap map) throws Exception {

        Tracer._debug("GMapService - deleteMap: " + (map != null ? map.getId() : "null"));

        final URL deleteMapUrl = _get_URL_MapSelf(map);
        m_srvcClient.delete(deleteMapUrl);
    }

    // ---------------------------------------------------------------------------------
    // Retorna una lista de mapas "vacios" (sin 'features')
    public ArrayList<TMap> getUserMapsList() throws Exception {

        Tracer._debug("ModelService - getUserMaps (in)");
        
        ArrayList<TMap> mapList = new ArrayList<TMap>();

        // Request the default metafeed
        MapFeed resultFeed = _getUserMapFeeds();

        // Iterate through the feed entries (maps)
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
            MapEntry entry = resultFeed.getEntries().get(i);
            TMap map = new TMap();
            _fill_Map_From_GMapEntry(map, entry);
            mapList.add(map);
        }

        Tracer._debug("ModelService - getUserMaps (out - " + mapList.size() + ")");
        return mapList;
    }

    // ---------------------------------------------------------------------------------
    public void login(String user, String pwd) throws Exception {

        Tracer._debug("GMapService - login: " + user);
        
        m_URL_createUserMap = null;
        m_user = user;
        m_srvcClient.setUserCredentials(m_user, pwd);
    }

    // ---------------------------------------------------------------------------------
    // Rellena un TMap a partir de informacion de gmap
    public void readMapData(TMap map) throws Exception {

        Tracer._debug("GMapService - readMapData: " + (map != null ? map.getId() : "null"));

        // Se debe conservar el TS_Update ante cambios de elementos
        TDateTime updateTime = map.getTS_Updated();
        
        ArrayList<TPoint> elements = _getMapPoints(map);
        map.addPoints(elements);

        ExtendedInfo extInfo = ExtendedInfo.parseFromXml(map);
        map.setShortName(extInfo.getMapShortName());
        map.setIcon(extInfo.getMapIcon());
        map.addCategories(extInfo.getCategories());
        
        // Se debe restaurar el TS_Update despues de los cambios de elementos
        map.setTS_Updated(updateTime);
    }

    // ---------------------------------------------------------------------------------
    private TMapFigure _new_MapFigure_From_FeatureEntry(TMap map, FeatureEntry entry) throws Exception {

        String kmlBlob = entry.getKml().getBlob();
        if (kmlBlob.contains("</Point>")) {
            return new TPoint(map);
        } else {
            return new TUnknown(map);
        }
    }

    // ---------------------------------------------------------------------------------
    private MapEntry _fill_GMapEntry_From_Map(TMap map) {

        MapEntry mapEntry = new MapEntry();
        mapEntry.setTitle(new PlainTextConstruct(map.getName()));
        mapEntry.setSummary(new PlainTextConstruct(map.getDescription()));
        // Person author = new Person("Maps Developer", null, "example@gmail.com");
        // mapEntry.getAuthors().add(author);
        return mapEntry;
    }

    // ---------------------------------------------------------------------------------
    private void _fill_MapFigure_From_GFeatureEntry(TMapFigure element, FeatureEntry entry) throws Exception {

        element.updateId(entry.getEditLink().getHref());
        element.setName(entry.getTitle().getPlainText());
        element.setTS_Created(new TDateTime(entry.getPublished().getValue()));
        element.setFromKmlBlob(entry.getKml().getBlob());

        // OJO: Se debe leer el ultimo porque sino cambiaria al hacer "Sets" anteriores
        element.setTS_Updated(new TDateTime(entry.getUpdated().getValue()));
    }

    // ---------------------------------------------------------------------------------
    private void _fill_Map_From_GMapEntry(TMap map, MapEntry entry) throws Exception {

        map.updateId(entry.getFeatureFeedUrl().toString());
        map.setName(entry.getTitle().getPlainText());
        map.setDescription(_getXHTMLText(entry.getSummary()));
        map.setTS_Created(new TDateTime(entry.getPublished().getValue()));

        // OJO: Se debe leer el ultimo porque sino cambiaria al hacer "Sets" anteriores
        map.setTS_Updated(new TDateTime(entry.getUpdated().getValue()));
    }

    // ---------------------------------------------------------------------------------
    private URL _get_URL_CreateUserMap() throws Exception {
        if (m_URL_createUserMap == null) {
            _getUserMapFeeds();
        }
        return m_URL_createUserMap;
    }

    // ---------------------------------------------------------------------------------
    private URL _get_URL_MapSelf(TMap map) throws Exception {

        String id = map.getId();
        int p1 = 10 + id.indexOf("/features/");
        int p2 = 1 + id.indexOf("/", p1);
        int p3 = id.indexOf("/", p2);

        String userId = id.substring(p1, p2 - 1);
        String mapId = id.substring(p2, p3);
        return new URL("http://maps.google.com/maps/feeds/maps/" + userId + "/full/" + mapId);
    }

    // ---------------------------------------------------------------------------------
    // Retorna una lista de elementos (DE MOMENTO SE QUEDA SOLO CON LOS PUNTOS) del mapa indicado
    private ArrayList<TPoint> _getMapPoints(TMap map) throws Exception {

        ArrayList<TPoint> elements = new ArrayList<TPoint>();

        // Get a feature feed for a specific map
        URL featureFeedUrl = new URL(map.getId());
        FeatureFeed featureFeed = m_srvcClient.getFeed(featureFeedUrl, FeatureFeed.class);

        // Iterate through the feed entries (feature)
        for (int i = 0; i < featureFeed.getEntries().size(); i++) {
            FeatureEntry entry = featureFeed.getEntries().get(i);
            TMapFigure element = _new_MapFigure_From_FeatureEntry(map, entry);
            if (element instanceof TPoint) {
                _fill_MapFigure_From_GFeatureEntry(element, entry);
                elements.add((TPoint) element);
            }
        }

        return elements;
    }

    // ---------------------------------------------------------------------------------
    // Obtiene el feed con la lista de mapas y, de paso, la URL necesaria para crear mapas
    // Parece que es la unica forma de conseguir, si lo tienes ya, el "userID" que aparece
    // en todas las URLs
    private MapFeed _getUserMapFeeds() throws Exception {
        MapFeed resultFeed = m_srvcClient.getFeed(new URL("http://maps.google.com/maps/feeds/maps/default/owned"), MapFeed.class);
        m_URL_createUserMap = new URL(resultFeed.getEntryPostLink().getHref());
        return resultFeed;
    }

    // ---------------------------------------------------------------------------------
    private String _getXHTMLText(TextConstruct tc) {

        String val;

        if (tc instanceof HtmlTextConstruct) {
            val = ((HtmlTextConstruct) tc).getHtml();
        } else if (tc instanceof XhtmlTextConstruct) {
            val = ((XhtmlTextConstruct) tc).getXhtml().getFullText();
        } else {
            val = tc.getPlainText();
        }

        return val;

    }

}
