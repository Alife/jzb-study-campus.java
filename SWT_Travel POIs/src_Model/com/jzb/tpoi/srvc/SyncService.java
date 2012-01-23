/**
 * 
 */
package com.jzb.tpoi.srvc;

import com.jzb.tpoi.data.NMCollection;
import com.jzb.tpoi.data.SyncStatusType;
import com.jzb.tpoi.data.TBaseEntity;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TPoint;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class SyncService {

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

        // Trazas
        Tracer._debug("-- Maps Merge ----------------------------------------------------------");
        for (TBaseEntity ent : localDeletedMaps) {
            if (ent.getSyncStatus() == SyncStatusType.Sync_Delete_Local) {
                Tracer._debug("  ---> SYNC MAP: " + ent.getName() + "\t-\t DELETED");
            }
        }
        for (TBaseEntity ent : localMaps) {
            if (ent.getSyncStatus() != SyncStatusType.Sync_OK) {
                Tracer._debug("  ---> SYNC MAP: " + ent.getName() + "\t-\t" + ent.getSyncStatus());
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

                    // Crea una copia para poder borrar el local una vez cambie el ID por el del servidor
                    TMap prevLocalMap = new TMap();
                    prevLocalMap.updateId(map.getId());

                    // Lee la información del mapa y la actualiza para ser sincronizada
                    ModelService.inst.readMapData(map);
                    for (TPoint point : map.getPoints()) {
                        point.setSyncStatus(SyncStatusType.Sync_Create_Remote);
                    }
                    for (TCategory cat : map.getCategories()) {
                        cat.setSyncStatus(SyncStatusType.Sync_Create_Remote);
                    }

                    // Crea el mapa como nuevo en remoto y local.
                    // Dejandolo como "modificado" hasta que se actualice el remoto
                    GMapService.inst.createMap(map);
                    ModelService.inst.createMap(map);

                    // Borra el antiguo mapa local
                    ModelService.inst.markAsDeletedMap(prevLocalMap);

                    // Actualiza el remoto (para añadir puntos) y luego el local
                    GMapService.inst.updateMap(map);
                    ModelService.inst.updateMap(map);
                    break;

                case Sync_Delete_Remote:
                    GMapService.inst.deleteMap(map);
                    ModelService.inst.markAsDeletedMap(map);
                    break;

                case Sync_Update_Local:
                case Sync_Update_Remote:

                    // Lee la informacion de ambos mapas
                    TMap remoteMap = remoteMaps.getById(map.getId());
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

        // Luego mezcla los puntos
        CollectionsMerger<TPoint> pointMerger = new CollectionsMerger<TPoint>();
        pointMerger.merge(localMap, localMap.getPoints(), localMap.getDeletedPoints(), remoteMap.getPoints());

        // Cambia la sincronizacion remota si solo había cambios de categorias
        for (TPoint localPoint : localMap.getPoints()) {
            if (localPoint.getSyncStatus() == SyncStatusType.Sync_Update_Remote) {
                TPoint remotePoint = remoteMap.getPoints().getById(localPoint.getId());
                if (localPoint.infoEquals(remotePoint)) {
                    localPoint.setSyncStatus(SyncStatusType.Sync_OK);
                }
            }
        }

        // Por ultimo el ExtInfoPoint
        TPoint leip = localMap.getExtInfoPoint();
        TPoint reip = remoteMap.getExtInfoPoint();
        String xml = leip.getDescription();
        leip.assignFrom(reip);
        leip.setDescription(xml);
    }
}
