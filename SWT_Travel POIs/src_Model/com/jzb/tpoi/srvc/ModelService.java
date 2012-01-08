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
        Tracer._debug("ModelService - setBaseFolder: " + baseFolderName);

    }

    // ---------------------------------------------------------------------------------
    public void createMap(TMap map) throws Exception {
        Tracer._debug("ModelService - createMap: " + (map != null ? map.getId() : "null"));
        _saveMap(map);
    }

    // ---------------------------------------------------------------------------------
    public ArrayList<TMap> getUserMapsList() throws Exception {

        Tracer._debug("ModelService - getUserMaps (in)");

        ArrayList<TMap> list = new ArrayList<TMap>();

        for (File f : m_baseFolder.listFiles()) {
            if (f.getName().endsWith(MAP_HEADER_FILE_EXT)) {

                TMap map = new TMap();
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                map.readHeaderExternal(ois);
                ois.close();

                if (!map.isMarkedAsDeleted()) {
                    list.add(map);
                }
            }
        }

        Tracer._debug("ModelService - getUserMaps (out - " + list.size() + ")");
        return list;

    }

    // ---------------------------------------------------------------------------------
    public void markAsDeletedMap(TMap map) throws Exception {
        Tracer._debug("ModelService - markAsDeletedMap: " + (map != null ? map.getId() : "null"));
        if (map.isSynchronized()) {
            map.markedAsDeleted(true);
            _saveMap(map);
        } else {
            Tracer._debug("ModelService - markAsDeletedMap: Map was just local. So it'll be truly deleted");
            trulyDeleteMap(map);
        }
    }

    // ---------------------------------------------------------------------------------
    public void readMapData(TMap map) throws Exception {

        Tracer._debug("ModelService - readMapData: " + (map != null ? map.getId() : "null"));

        File mapDataFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_DATA_FILE_EXT);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mapDataFile));
        map.readBodyExternal(ois);
        ois.close();
    }

    // ---------------------------------------------------------------------------------
    public void trulyDeleteMap(TMap map) throws Exception {

        Tracer._debug("ModelService - trulyDeleteMap: " + (map != null ? map.getId() : "null"));

        File mapHeaderFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_HEADER_FILE_EXT);
        File mapDataFile = new File(m_baseFolder, _getMapBaseFName(map.getId()) + MAP_DATA_FILE_EXT);
        mapHeaderFile.delete();
        mapDataFile.delete();
    }

    // ---------------------------------------------------------------------------------
    public void updateMap(TMap map) throws Exception {

        Tracer._debug("ModelService - updateMap: " + (map != null ? map.getId() : "null"));
        _saveMap(map);
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
    private void _saveMap(TMap map) throws Exception {

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
}
