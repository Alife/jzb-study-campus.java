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
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class CollectionsMerger<T_Item extends TBaseEntity> {

    // ---------------------------------------------------------------------------------
    public static interface ISorter<T_Item> {

        public void sort(ArrayList<T_Item> list);
    }

    private ISorter<T_Item> m_sorter;

    // ---------------------------------------------------------------------------------
    public CollectionsMerger(ISorter<T_Item> sorter) {
        m_sorter = sorter;
    }

    // ----------------------------------------------------------------------------------------------------
    private boolean _mustUpdateAfterLocalCreate(T_Item i1, T_Item i2) {
        
        // Solo comprueba que las categorias terminan teniendo el mismo numero de subelementos
        if (i1 instanceof TCategory) {
            TCategory c1 = (TCategory) i1;
            TCategory c2 = (TCategory) i2;
            boolean eq1 = c1.getPoints().size() == c2.getPoints().size();
            boolean eq2 = c1.getSubCategories().size() == c2.getSubCategories().size();
            return eq1 && eq2;
        } else {
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    public void merge(TMap localMap, NMCollection<T_Item> locals, NMCollection<T_Item> deletedLocals, NMCollection<T_Item> remotes) {

        // ----------------------------------------------------------------------------------------------------
        // Pasos:
        // Busca elementos remotos nuevos a crear en local [o borrar en remoto]
        // Busca elementos locales nuevos a crear en remoto [o borrar en local]
        // Busca elementos existentes en ambos para actualizar [Quien depende de info de cambios]
        // [Debe ser el ultimo por cambios de dependencias contra algo nuevo que crean los anteriores]
        // [En teoria, lo creado en pasos previos deberia dar OK en este y no hacer nada]

        // ----------------------------------------------------------------------------------------------------
        // First order elements to create elements with dependencies properly
        // They won't contain any new element added or deleted to the initial collections during merging
        ArrayList<T_Item> sortedLocals = new ArrayList<T_Item>(locals.values());
        ArrayList<T_Item> sortedRemotes = new ArrayList<T_Item>(remotes.values());
        if (m_sorter != null) {
            m_sorter.sort(sortedLocals);
            m_sorter.sort(sortedRemotes);
        }

        // ----------------------------------------------------------------------------------------------------
        // Buscamos elementos remotos nuevos a crear en local [o borrar en remoto si fueron borrados]
        for (T_Item remoteEntity : sortedRemotes) {

            T_Item localEntity = locals.getById(remoteEntity.getId());

            // Solo procesa los nuevos
            if (localEntity != null) {
                continue;
            }

            // Que se hace dependera de si existio previamente en local y fue borrada
            localEntity = deletedLocals.getById(remoteEntity.getId());
            if (localEntity == null) {
                // create local entity from remote
                Tracer._debug("MERGE: Create local entity from remote: " + remoteEntity.getType() + " - " + remoteEntity.getName());
                T_Item newEntity = _createNewLocalEntity(localMap, remoteEntity);
                newEntity.mergeFrom(remoteEntity, false);
                newEntity.setSyncStatus(SyncStatusType.Sync_Create_Local);
                locals.add(newEntity);
                if(_mustUpdateAfterLocalCreate(newEntity,remoteEntity)) {
                    Tracer._warn("MERGE: Created local entity differs from the original remote and that must be updated: " + remoteEntity.getType() + " - " + remoteEntity.getName());
                    newEntity.setSyncStatus(SyncStatusType.Sync_Update_Remote);
                }
            } else {
                // It was deleted previously
                if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                    // Delete remote entity as it was not modified and local was deleted
                    // Put deleted entry back in order to mark it as "delete_remote"
                    Tracer._debug("MERGE: Delete remote entity as it was not modified and local was deleted: " + remoteEntity.getType() + " - " + remoteEntity.getName());
                    deletedLocals.remove(localEntity);
                    locals.add(localEntity);
                    localEntity.setSyncStatus(SyncStatusType.Sync_Delete_Remote);
                } else {
                    // Conflict: Local was deleted but remote was modified
                    // Put deleted entry back in order to recreate it entity from remote
                    // Local and remote changes are merged, so there is a remote update
                    Tracer._warn("MERGE: Deleted local entity recreated from remote because it was changed: " + localEntity.getType() + " - " + localEntity.getName());
                    deletedLocals.remove(localEntity);
                    locals.add(localEntity);
                    localEntity.setSyncStatus(SyncStatusType.Sync_Update_Remote);
                    localEntity.mergeFrom(remoteEntity, true);
                }
            }
        }

        // ----------------------------------------------------------------------------------------------------
        // Buscamos elementos locales nuevos a crear en remoto [o borrar en local si fueron borrados]
        for (T_Item localEntity : sortedLocals) {

            T_Item remoteEntity = remotes.getById(localEntity.getId());

            // Solo procesa los nuevos
            if (remoteEntity != null) {
                continue;
            }

            // Que se hace dependera de si se sincronizo previamente o es nueva
            if (localEntity.isLocal()) {
                // create new remote entity from local
                Tracer._debug("MERGE: Create new remote entity from local: " + localEntity.getType() + " - " + localEntity.getName());
                localEntity.setSyncStatus(SyncStatusType.Sync_Create_Remote);
            } else {
                if (localEntity.isChanged()) {
                    // conflict!!: Remote was deleted but local was modified
                    // Create new remote entity from local
                    Tracer._warn("Deleted remote entity recreated from local because it was changed: " + localEntity.getType() + " - " + localEntity.getName());
                    localEntity.setSyncStatus(SyncStatusType.Sync_Create_Remote);
                } else {
                    // Delete local entity as it wasn't modified and remote was deleted previously
                    Tracer._debug("MERGE: Delete local entity as it wasn't modified and remote was deleted previously: " + localEntity.getType() + " - " + localEntity.getName());
                    Tracer._debug("  ---> SYNC ITEM: Deleting local item: " + localEntity.getName());
                    localEntity.setSyncStatus(SyncStatusType.Sync_Delete_Local);
                    locals.remove(localEntity);
                    deletedLocals.add(localEntity);
                }
            }
        }

        // ----------------------------------------------------------------------------------------------------
        // Buscamos elementos locales y remotos que existan en ambos y difieran para ser actualizados [quien dependera de como esten]
        for (T_Item localEntity : sortedLocals) {

            T_Item remoteEntity = remotes.getById(localEntity.getId());

            // Solo procesa lo existente
            if (remoteEntity == null) {
                continue;
            }

            // Quien termina actualizado depende de la marca de modificado y los ETAGs
            if (!localEntity.isChanged()) {
                if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                    // Nothing to be done as both are equals
                    Tracer._debug("MERGE: Nothing to be done as both are equals: " + localEntity.getType() + " - " + localEntity.getName());
                    localEntity.setSyncStatus(SyncStatusType.Sync_OK);
                } else {
                    // Local entity gets updated from remote
                    Tracer._debug("MERGE: Local entity gets updated from remote: " + localEntity.getType() + " - " + localEntity.getName());
                    localEntity.mergeFrom(remoteEntity, false);
                    localEntity.setSyncStatus(SyncStatusType.Sync_Update_Local);
                }
            } else {
                if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                    // Update remote entity with local info
                    Tracer._debug("MERGE: Update remote entity with local info: " + localEntity.getType() + " - " + localEntity.getName());
                    localEntity.setSyncStatus(SyncStatusType.Sync_Update_Remote);
                } else {
                    // Conflict!!: Both entities where updated
                    // Local entity gets updated from remote. Local and remote changes are merged
                    // There is a remote update
                    Tracer._warn("Both, local and remote were changed: " + localEntity.getType() + " - " + localEntity.getName());
                    localEntity.mergeFrom(remoteEntity, true);
                    localEntity.setSyncStatus(SyncStatusType.Sync_Update_Remote);
                }
            }

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
