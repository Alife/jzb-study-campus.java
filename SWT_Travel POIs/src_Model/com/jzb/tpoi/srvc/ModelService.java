/**
 * 
 */
package com.jzb.tpoi.srvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.jzb.tpoi.data.TMap;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class ModelService {

    public static ModelService  inst                = new ModelService();
    private static final String MAP_DATA_FILE_EXT   = ".mapdata";
    private static final String MAP_HEADER_FILE_EXT = ".mapheader";

    private File                m_baseFolder;

    // ---------------------------------------------------------------------------------
    public void _setBaseFolder(String baseFolderName) {
        m_baseFolder = new File(baseFolderName);
        m_baseFolder.mkdirs();
        Tracer._debug("ModelService - setBaseFolder: " + baseFolderName);

    }

    // ---------------------------------------------------------------------------------
    public void createMap(TMap map) throws Exception {
        Tracer._debug("ModelService - createMap: " + (map != null ? map.getName() + " - " + map.getId() : "null"));
        _storeMapInfo(map);
    }

    // ---------------------------------------------------------------------------------
    public ArrayList<TMap> getUserMapsList(boolean alsoDeleted) throws Exception {

        Tracer._debug("ModelService - getUserMaps (in)");

        ArrayList<TMap> list = new ArrayList<TMap>();

        for (File f : m_baseFolder.listFiles()) {
            if (f.getName().endsWith(MAP_HEADER_FILE_EXT)) {

                TMap map = new TMap();
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                map.readHeaderExternal(ois);
                ois.close();

                if (!map.isMarkedAsDeleted() || alsoDeleted) {
                    list.add(map);
                }
            }
        }

        Tracer._debug("ModelService - getUserMaps (out - " + list.size() + ")");
        return list;

    }

    // ---------------------------------------------------------------------------------
    // Realmente no lo borra, lo marca como borrado para borrarlo al sincronizarlo
    public void markAsDeletedMap(TMap map) throws Exception {
        Tracer._debug("ModelService - markAsDeletedMap: " + (map != null ? map.getName() + " - " + map.getId() : "null"));
        if (map.isLocal()) {
            Tracer._debug("ModelService - markAsDeletedMap: Map was just local. So it'll be truly deleted");
            _trulyDeleteMap(map);
        } else {
            map.markedAsDeleted(true);
            _storeMapInfo(map);
        }
    }

    // ---------------------------------------------------------------------------------
    public void purgeDeleted() throws Exception {
        Tracer._debug("ModelService - purgeDeleted");
        ArrayList<TMap> deleted = getUserMapsList(true);
        for (TMap map : deleted) {
            if (map.isMarkedAsDeleted()) {
                _trulyDeleteMap(map);
            }
        }
    }

    // ---------------------------------------------------------------------------------
    public void readMapData(TMap map) throws Exception {

        Tracer._debug("ModelService - readMapData: " + (map != null ? map.getName() + " - " + map.getId() : "null"));

        File mapDataFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_DATA_FILE_EXT);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mapDataFile));
        map.clearInfo();
        map.readBodyExternal(ois);
        ois.close();
    }

    // ---------------------------------------------------------------------------------
    public void updateMap(TMap map) throws Exception {

        Tracer._debug("ModelService - updateMap: " + (map != null ? map.getName() + " - " + map.getId() : "null"));
        _storeMapInfo(map);
    }

    // ---------------------------------------------------------------------------------
    private String _getMapBaseFName(String mapID) {

        // http://maps.google.com/maps/feeds/features/userID/mapID/full
        if (mapID.contains("maps.google.com")) {
            // Remote map
            int p1 = 10 + mapID.indexOf("/features/");
            int p2 = 1 + mapID.indexOf("/", p1);
            int p3 = mapID.indexOf("/", p2);
            String userId = mapID.substring(p1, p2 - 1);
            String mapId = mapID.substring(p2, p3);
            return userId + "_@_" + mapId;
        } else {
            // Local map
            return mapID;
        }
    }

    // ---------------------------------------------------------------------------------
    private void _storeMapInfo(TMap map) throws Exception {

        ObjectOutputStream oos;

        File mapHeaderFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_HEADER_FILE_EXT);
        File mapDataFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_DATA_FILE_EXT);

        oos = new ObjectOutputStream(new FileOutputStream(mapDataFile));
        map.writeBodyExternal(oos);
        oos.close();

        oos = new ObjectOutputStream(new FileOutputStream(mapHeaderFile));
        map.writeHeaderExternal(oos);
        oos.close();

    }

    // ---------------------------------------------------------------------------------
    private void _trulyDeleteMap(TMap map) throws Exception {

        Tracer._debug("ModelService - trulyDeleteMap: " + (map != null ? map.getName() + " - " + map.getId() : "null"));

        File mapHeaderFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_HEADER_FILE_EXT);
        File mapDataFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_DATA_FILE_EXT);
        mapHeaderFile.delete();
        mapDataFile.delete();
    }
}
