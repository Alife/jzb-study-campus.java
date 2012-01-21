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
import com.google.gdata.util.XmlBlob;
import com.jzb.tpoi.data.ExtendedInfo;
import com.jzb.tpoi.data.SyncStatusType;
import com.jzb.tpoi.data.TDateTime;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TMapFigure;
import com.jzb.tpoi.data.TPoint;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class GMapService {

    public static GMapService  inst           = new GMapService();

    private static String      m_logedUser_ID = null;
    private static MapsService m_srvcClient;
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

        Tracer._debug("GMapService - createMap: " + (map != null ? map.getName() + " - " + map.getId() : "null"));

        // ************************************************************************************
        // MIENTRAS ESTEMOS EN PRUEBAS!!!!!!!
        if (!map.getName().startsWith("@")) {
            return;
        }
        // ************************************************************************************

        MapEntry mapEntry = _fill_GMapEntry_From_Map(map);

        // First calc feed URL. For doing that user_id is needed and get maps list must be called first
        if (m_logedUser_ID == null) {
            getUserMapsList();
        }
        final URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/" + m_logedUser_ID + "/full");

        // Calls google service to create the new map
        MapEntry newMapEntry = m_srvcClient.insert(feedUrl, mapEntry);

        // Update map info from new entry created
        _fill_Map_From_GMapEntry(map, newMapEntry);

        // Maps are created as public by default. Changes it to private
        final URL editMapUrl = new URL(newMapEntry.getEditLink().getHref());
        com.google.gdata.data.extensions.CustomProperty customProperty = new com.google.gdata.data.extensions.CustomProperty();
        customProperty.setName("api_visible");
        customProperty.setValue("0");
        newMapEntry.addCustomProperty(customProperty);

        MapEntry updatedMapEntry = m_srvcClient.update(editMapUrl, newMapEntry);

        // Update map info from new entry updated
        _fill_Map_From_GMapEntry(map, updatedMapEntry);

    }

    // ---------------------------------------------------------------------------------
    public void deleteMap(TMap map) throws Exception {

        Tracer._debug("GMapService - deleteMap: " + (map != null ? map.getName() + " - " + map.getId() : "null"));

        // ************************************************************************************
        // MIENTRAS ESTEMOS EN PRUEBAS!!!!!!!
        if (!map.getName().startsWith("@")) {
            return;
        }
        // ************************************************************************************

        // NO SE PUEDE BORRAR UN MAPA LOCAL
        if (map.isLocal()) {
            return;
        }

        String str = "http://maps.google.com/maps/feeds/maps/" + map.getId().replace("#", "/full/");
        final URL deleteMapUrl = new URL(str);
        m_srvcClient.delete(deleteMapUrl);

    }

    // ---------------------------------------------------------------------------------
    // Retorna una lista de mapas "vacios" (sin 'features')
    public ArrayList<TMap> getUserMapsList() throws Exception {

        Tracer._debug("GMapService - getUserMaps (in)");

        ArrayList<TMap> mapList = new ArrayList<TMap>();

        // Request the default metafeed
        MapFeed resultFeed = m_srvcClient.getFeed(new URL("http://maps.google.com/maps/feeds/maps/default/owned"), MapFeed.class);

        // Gets UserID for the logged user that requested the list
        if (m_logedUser_ID == null) {
            _extract_ID_for_LogedUser(resultFeed);
        }

        // Iterate through the feed entries (maps)
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
            MapEntry entry = resultFeed.getEntries().get(i);
            TMap map = new TMap();
            _fill_Map_From_GMapEntry(map, entry);

            // ************************************************************************************
            // MIENTRAS ESTEMOS EN PRUEBAS!!!!!!!
            if (!map.getName().startsWith("@")) {
                continue;
            }
            // ************************************************************************************

            mapList.add(map);
        }

        Tracer._debug("GMapService - getUserMaps (out - " + mapList.size() + ")");
        return mapList;
    }

    // ---------------------------------------------------------------------------------
    public void login(String user, String pwd) throws Exception {

        Tracer._debug("GMapService - login: " + user);

        m_user = user;
        m_srvcClient.setUserCredentials(m_user, pwd);
        m_logedUser_ID = null;
    }

    // ---------------------------------------------------------------------------------
    // Rellena un TMap a partir de informacion de gmap
    public void readMapData(TMap map) throws Exception {

        Tracer._debug("GMapService - readMapData: " + (map != null ? map.getName() + " - " + map.getId() : "null"));

        // NO SE PUEDE PEDIR INFORMACION SOBRE UN MAPA LOCAL
        if (map.isLocal()) {
            return;
        }

        // Se debe conservar el TS_Update ante cambios de elementos
        TDateTime updateTime = map.getTS_Updated();

        // Borra info para leerla desde GMap
        map.clearInfo();

        // Lee las "map features" del GMap
        _readMapPoints(map);

        // Añade las caracteristicas adicionales
        ExtendedInfo.parseExtInfoFromXml(map);

        // Se debe restaurar el TS_Update despues de los cambios de elementos
        map.setTS_Updated(updateTime);

        // Pone todo como recien leido: Sin cambios, sin nada borrado, Sync_OK, etc.
        map.resetChanged();
    }

    // ---------------------------------------------------------------------------------
    public void updateMap(TMap map) throws Exception {

        Tracer._debug("GMapService - updateMap: " + (map != null ? map.getName() + " - " + map.getId() : "null"));

        // ************************************************************************************
        // MIENTRAS ESTEMOS EN PRUEBAS!!!!!!!
        if (!map.getName().startsWith("@")) {
            return;
        }
        // ************************************************************************************

        boolean allOK = true;
        
        ArrayList<TPoint> points = new ArrayList<TPoint>(map.getPoints().values());
        for (TPoint point : points) {

            System.out.println(point.getName() + " - " + point.getSyncStatus());

            switch (point.getSyncStatus()) {
                case Sync_Create_Remote:
                    allOK &= _gmap_update_CreateEntry(point);
                    break;
                case Sync_Delete_Remote:
                    allOK &= _gmap_update_DeleteEntry(point);
                    map.getPoints().remove(point);
                    break;
                case Sync_Update_Remote:
                    allOK &= _gmap_update_UpdateEntry(point);
                    break;
            }

        }

        // Si actualizo bien todos los puntos actualiza el mapa y resetea
        if (allOK) {
            // Actualiza ETAG y UpdateTime del mapa
            final URL mapSelfUrl = new URL("http://maps.google.com/maps/feeds/maps/" + map.getId().replace("#", "/full/"));
            MapEntry updatedMap = m_srvcClient.getEntry(mapSelfUrl, MapEntry.class);
            _fill_Map_From_GMapEntry(map, updatedMap);

            // Pone todo como recien leido: Sin cambios, sin nada borrado, Sync_OK, etc.
            map.resetChanged();
        }

    }

    // ---------------------------------------------------------------------------------
    private void _extract_ID_for_LogedUser(MapFeed resultFeed) {

        // Gets UserID for the logged user that requested the list
        String str = resultFeed.getEntryPostLink().getHref();

        // http://maps.google.com/maps/feeds/maps/<<user_id>>/full
        int p1 = str.lastIndexOf("/maps/");
        int p2 = str.lastIndexOf("/");
        if (p1 > 0 && p2 > 0) {
            m_logedUser_ID = str.substring(p1 + 6, p2);
        }
    }

    // ---------------------------------------------------------------------------------
    private String _extract_ID_for_Map(MapEntry entry) {

        // http://maps.google.com/maps/feeds/features/<<user_id>>/<<map_id>>/full
        String str = entry.getFeatureFeedUrl().toString();

        int p1 = 10 + str.indexOf("/features/");
        int p2 = 1 + str.indexOf("/", p1);
        int p3 = str.indexOf("/", p2);

        String userId = str.substring(p1, p2 - 1);
        String mapId = str.substring(p2, p3);

        return userId + "#" + mapId;
    }

    // ---------------------------------------------------------------------------------
    private String _extract_ID_for_MapFeature(FeatureEntry entry) {

        // http://maps.google.com/maps/feeds/features/<<user_id>>/<<map_id>>/full/<<feature_id>>
        String str = entry.getEditLink().getHref();

        int p1 = 6 + str.indexOf("/full/");
        String featureId = str.substring(p1);

        return featureId;
    }

    // ---------------------------------------------------------------------------------
    private FeatureEntry _fill_GFeatureEntry_From_MapFigure(TMapFigure element) throws Exception {

        FeatureEntry entry = new FeatureEntry();

        entry.setTitle(new PlainTextConstruct(element.getName()));
        // entry.setSummary(new PlainTextConstruct(summary));
        XmlBlob kml = new XmlBlob();
        kml.setBlob(element.refreshKmlBlob());
        entry.setKml(kml);

        return entry;
    }

    // ---------------------------------------------------------------------------------
    private MapEntry _fill_GMapEntry_From_Map(TMap map) {

        MapEntry mapEntry = new MapEntry();
        mapEntry.setTitle(new PlainTextConstruct(map.getName()));
        String summary = map.getDescription().trim();
        // Por algun tipo de problema, la descripcion no puede ir vacia
        if (summary.length() == 0) {
            summary = "Summary";
        }
        mapEntry.setSummary(new PlainTextConstruct(summary));
        // Person author = new Person("Maps Developer", null, "example@gmail.com");
        // mapEntry.getAuthors().add(author);
        return mapEntry;
    }

    // ---------------------------------------------------------------------------------
    private void _fill_Map_From_GMapEntry(TMap map, MapEntry entry) throws Exception {

        map.updateId(_extract_ID_for_Map(entry));
        map.setName(entry.getTitle().getPlainText());
        map.updateSyncETag(entry.getEtag());
        map.setDescription(_getXHTMLText(entry.getSummary()));
        map.setTS_Created(new TDateTime(entry.getPublished().getValue()));

        // OJO: Se debe leer el ultimo porque sino cambiaria al hacer "Sets" anteriores
        map.setTS_Updated(new TDateTime(entry.getUpdated().getValue()));
        map.updateChanged(false);
    }

    // ---------------------------------------------------------------------------------
    private void _fill_MapFigure_From_GFeatureEntry(TMapFigure element, FeatureEntry entry) throws Exception {

        element.updateId(_extract_ID_for_MapFeature(entry));
        element.setName(entry.getTitle().getPlainText());
        element.updateSyncETag(entry.getEtag());
        element.setTS_Created(new TDateTime(entry.getPublished().getValue()));
        element.assignFromKmlBlob(entry.getKml().getBlob());
        element.setSyncStatus(SyncStatusType.Sync_OK);

        // OJO: Se debe leer el ultimo porque sino cambiaria al hacer "Sets" anteriores
        element.setTS_Updated(new TDateTime(entry.getUpdated().getValue()));
        element.updateChanged(false);
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

    // ---------------------------------------------------------------------------------
    private boolean _gmap_update_CreateEntry(TPoint point) {

        try {
            String user_map_id = point.getOwnerMap().getId().replace('#', '/');

            FeatureEntry featureEntry = _fill_GFeatureEntry_From_MapFigure(point);
            final URL featureEditUrl = new URL("http://maps.google.com/maps/feeds/features/" + user_map_id + "/full");
            FeatureEntry newEntry = m_srvcClient.insert(featureEditUrl, featureEntry);

            _fill_MapFigure_From_GFeatureEntry(point, newEntry);

            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    private boolean _gmap_update_DeleteEntry(TPoint point) {

        try {
            String user_map_id = point.getOwnerMap().getId().replace('#', '/');

            final URL featureEditUrl = new URL("http://maps.google.com/maps/feeds/features/" + user_map_id + "/full/" + point.getId());

            m_srvcClient.delete(featureEditUrl);

            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    private boolean _gmap_update_UpdateEntry(TPoint point) {

        try {
            String user_map_id = point.getOwnerMap().getId().replace('#', '/');

            FeatureEntry featureEntry = _fill_GFeatureEntry_From_MapFigure(point);
            final URL featureEditUrl = new URL("http://maps.google.com/maps/feeds/features/" + user_map_id + "/full/" + point.getId());
            FeatureEntry updatedEntry = m_srvcClient.update(featureEditUrl, featureEntry);

            _fill_MapFigure_From_GFeatureEntry(point, updatedEntry);

            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    // Lee la lista de elementos (DE MOMENTO SE QUEDA SOLO CON LOS PUNTOS) del mapa indicado
    private void _readMapPoints(TMap map) throws Exception {

        // Get a features feed for a specific map
        String str = "http://maps.google.com/maps/feeds/features/" + map.getId().replace('#', '/') + "/full";
        URL feedsURL = new URL(str);
        FeatureFeed featureFeed = m_srvcClient.getFeed(feedsURL, FeatureFeed.class);

        // Iterate through the feed entries (feature)
        for (int i = 0; i < featureFeed.getEntries().size(); i++) {
            FeatureEntry entry = featureFeed.getEntries().get(i);

            // By the moment just TPoints are created
            String kmlBlob = entry.getKml().getBlob();
            if (kmlBlob.contains("</Point>")) {
                TPoint point = new TPoint(map);
                _fill_MapFigure_From_GFeatureEntry(point, entry);

                if (ExtendedInfo.isExtInfoPoint(point)) {
                    map.setExtInfoPoint(point);
                } else {
                    map.getPoints().add(point);
                }

            }

        }
    }

}
