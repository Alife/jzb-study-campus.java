/**
 * 
 */
package com.jzb.tpoi.srvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    private Comparator<T_Item> m_comp;
    
    // ---------------------------------------------------------------------------------
    public CollectionsMerger(Comparator<T_Item> comp) {
        m_comp=comp;
    }
    
    // ---------------------------------------------------------------------------------
    public void merge(TMap localMap, NMCollection<T_Item> locals, NMCollection<T_Item> deletedLocals, NMCollection<T_Item> remotes) {

        ArrayList<T_Item> _localsToDelete = new ArrayList<T_Item>();

        // Check Local list against Remote list
        ArrayList<T_Item> locals2 = new ArrayList<T_Item>(locals.values());
        if (m_comp != null) {
            Collections.sort(locals2, m_comp);
        }
        for (T_Item localEntity : locals2) {

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
                        localEntity.mergeFrom(remoteEntity);
                        localEntity.setSyncStatus(SyncStatusType.Sync_Update_Local);
                    }
                } else {
                    if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                        // Nothing to be done as both are equals
                        localEntity.setSyncStatus(SyncStatusType.Sync_OK);
                    } else {
                        // Local entity gets updated from remote
                        localEntity.mergeFrom(remoteEntity);
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
                    newEntity.mergeFrom(remoteEntity);
                    newEntity.setSyncStatus(SyncStatusType.Sync_Create_Local);
                    locals.add(newEntity);
                } else {
                    if (localEntity.getSyncETag().equals(remoteEntity.getSyncETag())) {
                        // Delete remote entity as it was not modified and local was deleted
                        T_Item newEntity = _createNewLocalEntity(localMap, remoteEntity);
                        newEntity.mergeFrom(localEntity);
                        newEntity.setSyncStatus(SyncStatusType.Sync_Delete_Remote);
                        locals.add(newEntity);
                    } else {
                        // Conflict: Local was deleted but remote was modified
                        // Create local entity from remote
                        T_Item newEntity = _createNewLocalEntity(localMap, remoteEntity);
                        newEntity.mergeFrom(remoteEntity);
                        newEntity.setSyncStatus(SyncStatusType.Sync_Create_Local);
                        locals.add(newEntity);
                    }
                }
            }

        }

        // Delete local elements detected previously
        for (T_Item localEntity : _localsToDelete) {
            Tracer._debug("  ---> SYNC ITEM: Deleting local item: " + localEntity.getName());
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
