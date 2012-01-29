/**
 * 
 */
package com.jzb.tpoi.gg;

import java.util.ArrayList;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.srvc.GMapService;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.tpoi.srvc.SyncService;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class MapSyncWorker {

    public interface INotification {

        public void mapSynced(TMap map);
    }

    private IProgressMonitor m_monitor;

    public MapSyncWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    public void syncMap(final String baseFolder, final TMap map, final INotification sink) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("MapSyncWorker - Start execution");
                    _syncMap(baseFolder, map, sink);
                    Tracer._info("MapSyncWorker - End execution");

                    if (m_monitor != null)
                        m_monitor.processingEnded(false, "OK");
                } catch (Throwable th) {
                    Tracer._error("Error executing InitWorker", th);
                    if (m_monitor != null)
                        m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "RenameWorker").start();
    }

    private TMap _searchMap(ArrayList<TMap> maps, String name) {
        for (TMap map : maps) {
            if (map.getName().equals(name)) {
                return map;
            }
        }
        return null;
    }

    private void _syncMap(final String baseFolder, final TMap lmap, final INotification sink) throws Exception {

        if (!GMapService.inst.isLoggedIn()) {
            GMapService.inst.login(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));
        }

        ArrayList<TMap> maps = GMapService.inst.getUserMapsList();
        TMap rmap = _searchMap(maps, "@vtest");
        if (rmap != null) {
            GMapService.inst.readMapData(rmap);
        }

        ModelService.inst._setBaseFolder(baseFolder);
        ModelService.inst.updateMap(lmap);
        SyncService.inst.syncMaps(lmap, rmap);

        sink.mapSynced(lmap);

    }

}
