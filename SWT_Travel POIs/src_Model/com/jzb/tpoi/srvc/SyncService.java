/**
 * 
 */
package com.jzb.tpoi.srvc;

import java.util.HashMap;

import com.jzb.tpoi.data.SyncStatusType;
import com.jzb.tpoi.data.TBaseEntity;
import com.jzb.tpoi.data.TMap;

/**
 * @author n63636
 * 
 */
public class SyncService {

    public static SyncService inst = new SyncService();

    // ---------------------------------------------------------------------------------
    public void compareCollections(HashMap<String, ? extends TBaseEntity> local, HashMap<String, ? extends TBaseEntity> remote) throws Exception {

        // Check Local list against Remote list
        for (TBaseEntity be1 : local) {
            TBaseEntity be2 = remote.getById(be1.getId());
            if (be2 == null) {
                if (be1.isMarkedAsDeleted()) {
                    be1.setSyncStatus(SyncStatusType.Sync_LcDelete);
                } else {
                    if (be1.isSynchronized()) {
                        be1.setSyncStatus(SyncStatusType.Sync_LcDelete);
                    } else {
                        be1.setSyncStatus(SyncStatusType.Sync_RmtCreate);
                    }
                }
            } else {
                // Check if and update is necessary based on Update_TS
                int comp = be1.getUpdated_TS().compareTo(be2.getUpdated_TS());
                if (comp == 0) {
                    // Nothing needed
                    be1.setSyncStatus(SyncStatusType.Sync_OK);
                } else if (comp < 0) {
                    // Local update is needed
                    be1.setSyncStatus(SyncStatusType.Sync_LcUpdate);
                } else {
                    // local map must be created
                    be1.setSyncStatus(SyncStatusType.Sync_RmtUpdate);
                }
            }
        }

        // Check Remote list against Local list, just to detect new items
        for (TBaseEntity be2 : remote) {
            TBaseEntity be1 = local.getById(be2.getId());
            if (be1 == null) {
                // new local Item to be added. Item is added as a new element to local collection
                be2.setSyncStatus(SyncStatusType.Sync_LcCreate);
                local.add(be2);
            }
        }
    }

    // ---------------------------------------------------------------------------------
    public void syncAllMaps() throws Exception {

        MapsEntityCollection<TMap> localMaps = ModelService.inst.getUserMaps();
        MapsEntityCollection<TMap> remoteMaps = GMapService.inst.getUserMaps();

        compareCollections(localMaps, remoteMaps);

        for (TMap map : localMaps) {
            switch (map.getSyncStatus()) {

                case Sync_LcCreate:
                    GMapService.inst.readMapData(map);
                    ModelService.inst.saveMap(map);
                    break;

                case Sync_RmtCreate:
                    ModelService.inst.readMapData(map);
                    GMapService.inst.createNewMap(map);
                    break;

                case Sync_LcDelete:
                    ModelService.inst.deleteMap(map);
                    break;

                case Sync_RmtDelete:
                    GMapService.inst.deleteMap(map);
                    // Una vez borrado el remoto puede borrar el marcado para borrar
                    ModelService.inst.deleteMap(map);
                    break;

                case Sync_LcUpdate:
                    // Sincronizar todos los elementos del mapa
                    break;

                case Sync_RmtUpdate:
                    // Sincronizar todos los elementos del mapa
                    break;
            }
        }

    }
}
