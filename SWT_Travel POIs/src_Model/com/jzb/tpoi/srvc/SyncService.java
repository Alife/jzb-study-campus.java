/**
 * 
 */
package com.jzb.tpoi.srvc;

import java.util.ArrayList;

import com.jzb.tpoi.data.NMCollection;
import com.jzb.tpoi.data.SyncStatusType;
import com.jzb.tpoi.data.TBaseEntity;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TPoint;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class SyncService {

    // ---------------------------------------------------------------------------------
    private static class CollectionsMerger<T_Item extends TBaseEntity> {

        // ---------------------------------------------------------------------------------
        public void merge(TMap localMap, NMCollection<T_Item> locals, NMCollection<T_Item> deletedLocals, NMCollection<T_Item> remotes) {

            ArrayList<T_Item> _localsToDelete = new ArrayList<T_Item>();

            // Check Local list against Remote list
            for (T_Item localEntity : locals.values()) {

                T_Item remoteEntity = remotes.getById(localEntity.getId());

                if (remoteEntity == null) {

                    if (localEntity.isLocal()) {
                        // create new remote entity from local
                        localEntity.setSyncStatus(SyncStatusType.Sync_Create_Remote);
                    } else {
                        if (localEntity.isChanged()) {
                            // conflict!!: Remote was deleted but local was modified
                            // Create new remote entity from local
                            localEntity.setSyncStatus(SyncStatusType.Sync_Create_Remote);
                        } else {
                            // Delete local entity as remote was so and it's not modified
                            _localsToDelete.add(localEntity);
                        }
                    }

                } else {
                    if (localEntity.isChanged()) {
                        if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                            // Update remote entity with local info
                            localEntity.setSyncStatus(SyncStatusType.Sync_Update_Remote);
                        } else {
                            // Conflict!!: Both entities where updated
                            // Local entity gets updated from remote. Local changes are lost.
                            localEntity.assignFrom(remoteEntity);
                            localEntity.setSyncStatus(SyncStatusType.Sync_Update_Local);
                        }
                    } else {
                        if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                            // Nothing to be done as both are equals
                            localEntity.setSyncStatus(SyncStatusType.Sync_OK);
                        } else {
                            // Local entity gets updated from remote
                            localEntity.assignFrom(remoteEntity);
                            localEntity.setSyncStatus(SyncStatusType.Sync_Update_Local);
                        }
                    }
                }
            }

            // Check Remote list against Local list, just to detect new items
            for (T_Item remoteEntity : remotes.values()) {

                T_Item localEntity = locals.getById(remoteEntity.getId());

                if (localEntity != null) {
                    // Already done
                    continue;
                } else {
                    localEntity = deletedLocals.getById(remoteEntity.getId());
                    if (localEntity == null) {
                        // create local entity from remote
                        T_Item newEntity = _createNewLocalEntity(localMap, remoteEntity);
                        newEntity.assignFrom(remoteEntity);
                        newEntity.setSyncStatus(SyncStatusType.Sync_Create_Local);
                        locals.add(newEntity);
                    } else {
                        if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                            // Delete remote entity as it was not modified and local was deleted
                            T_Item newEntity = _createNewLocalEntity(localMap, remoteEntity);
                            newEntity.assignFrom(localEntity);
                            newEntity.setSyncStatus(SyncStatusType.Sync_Delete_Remote);
                            locals.add(newEntity);
                        } else {
                            // Conflict: Local was deleted but remote was modified
                            // Create local entity from remote
                            T_Item newEntity = _createNewLocalEntity(localMap, remoteEntity);
                            newEntity.assignFrom(remoteEntity);
                            newEntity.setSyncStatus(SyncStatusType.Sync_Create_Local);
                            locals.add(newEntity);
                        }
                    }
                }

            }

            // Delete local elements detected previously
            for (T_Item localEntity : _localsToDelete) {
                localEntity.setSyncStatus(SyncStatusType.Sync_Delete_Local);
                locals.remove(localEntity);
                deletedLocals.add(localEntity);
            }

        }

        // ---------------------------------------------------------------------------------
        private T_Item _createNewLocalEntity(TMap localMap, T_Item remoteEntity) {

            if (remoteEntity instanceof TMap) {
                return (T_Item) new TMap();
            } else if (remoteEntity instanceof TPoint) {
                return (T_Item) new TPoint(localMap);
            } else if (remoteEntity instanceof TCategory) {
                return (T_Item) new TCategory(localMap);
            } else {
                return null;
            }
        }
    }

    public static SyncService inst = new SyncService();

    // ---------------------------------------------------------------------------------
    public void syncAllMaps() throws Exception {

        NMCollection<TMap> localMaps = new NMCollection<TMap>(null);
        NMCollection<TMap> localDeletedMaps = new NMCollection<TMap>(null);
        NMCollection<TMap> remoteMaps = new NMCollection<TMap>(null);

        // Read both lists of maps
        for (TMap map : GMapService.inst.getUserMapsList()) {
            remoteMaps.add(map);
        }
        for (TMap map : ModelService.inst.getUserMapsList(true)) {
            if (map.isMarkedAsDeleted()) {
                localDeletedMaps.add(map);
            } else {
                localMaps.add(map);
            }
        }

        // Merge differences in local maps
        CollectionsMerger<TMap> merger = new CollectionsMerger<TMap>();
        merger.merge(null, localMaps, localDeletedMaps, remoteMaps);
        for (TBaseEntity ent : localDeletedMaps) {
            if (ent.getSyncStatus() == SyncStatusType.Sync_Delete_Local) {
                System.out.println(ent.getName() + "\t-\t DELETED");
            }
        }
        for (TBaseEntity ent : localMaps) {
            if (ent.getSyncStatus() != SyncStatusType.Sync_OK) {
                System.out.println(ent.getName() + "\t-\t" + ent.getSyncStatus());
            }
        }

        // Delete existing local maps marked to be deleted
        for (TBaseEntity ent : localDeletedMaps) {
            if (ent.getSyncStatus() == SyncStatusType.Sync_Delete_Local) {
                ModelService.inst.markAsDeletedMap((TMap) ent);
            }
        }

        // Iterate entities updating their status
        for (TBaseEntity ent : localMaps) {

            TMap map = (TMap) ent;

            switch (map.getSyncStatus()) {

                case Sync_Create_Local:
                    GMapService.inst.readMapData(map);
                    ModelService.inst.createMap(map);
                    break;

                case Sync_Create_Remote:

                    // Crea una copia para poder borrar el local
                    TMap prevLocalMap = new TMap();
                    prevLocalMap.updateId(map.getId());

                    // Lee la información del mapa y la actualiza para ser sincronizada
                    ModelService.inst.readMapData(map);
                    for (TPoint point : map.getPoints()) {
                        point.setSyncStatus(SyncStatusType.Sync_Create_Remote);
                    }

                    // Crea el mapa como nuevo en remoto y local.
                    // Dejandolo como "modificado" hasta que se actualice el remoto
                    GMapService.inst.createMap(map);
                    map.updateChanged(true);
                    ModelService.inst.createMap(map);

                    // Borra el antiguo mapa local
                    ModelService.inst.markAsDeletedMap(prevLocalMap);

                    // Actualiza el remoto
                    GMapService.inst.updateMap(map);
                    break;

                case Sync_Delete_Remote:
                    GMapService.inst.deleteMap(map);
                    ModelService.inst.markAsDeletedMap(map);
                    break;

                case Sync_Update_Local:
                case Sync_Update_Remote:

                    // Lee la informacion de ambos mapas
                    TMap remoteMap = (TMap) remoteMaps.getById(map.getId());
                    ModelService.inst.readMapData(map);
                    GMapService.inst.readMapData(remoteMap);

                    // Mezcla ambos mapas dejando el resultado en el local
                    _mergeMaps(map, remoteMap);

                    // Actualiza el remoto y luego el local
                    GMapService.inst.updateMap(map);
                    ModelService.inst.updateMap(map);
                    break;
            }

        }

        // Once synchronization is done "marked as deleted" items can be purged
        ModelService.inst.purgeDeleted();

    }

    // ---------------------------------------------------------------------------------
    private void _mergeMaps(TMap localMap, TMap remoteMap) {

        // Mezcla primero las Categorias
        CollectionsMerger<TCategory> catMerger = new CollectionsMerger<TCategory>();
        catMerger.merge(localMap, localMap.getCategories(), localMap.getDeletedCategories(), remoteMap.getCategories());
        
        // Y luego mezcla los puntos
        CollectionsMerger<TPoint> pointMerger = new CollectionsMerger<TPoint>();
        pointMerger.merge(localMap, localMap.getPoints(), localMap.getDeletedPoints(), remoteMap.getPoints());
    }

}
