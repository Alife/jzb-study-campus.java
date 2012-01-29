/**
 * 
 */
package com.jzb.tpoi.gg;

import java.util.ArrayList;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.tpoi.data.GMercatorProjection;
import com.jzb.tpoi.data.SyncStatusType;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TCoordinates;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TPoint;
import com.jzb.tpoi.srvc.GMapService;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class InitializeWorker {

    public interface INotification {

        public void done();
    }

    private IProgressMonitor m_monitor;

    public InitializeWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    public void init(final String baseFolder1, final String baseFolder2, final INotification sink) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("MapSyncWorker - Start execution");
                    _init(baseFolder1, baseFolder2, sink);
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

    private TMap _createDefaultMapInfo1() {

        TCategory c;

        TMap amap = new TMap();
        amap.setName("@vtest");

        TPoint p1 = new TPoint(amap);
        p1.setName("P-1");
        p1.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(100), GMercatorProjection.YToLat(100)));
        amap.getPoints().add(p1);
        p1.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        TPoint p2 = new TPoint(amap);
        p2.setName("P-2");
        p2.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(170), GMercatorProjection.YToLat(100)));
        amap.getPoints().add(p2);
        p2.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        TPoint p3 = new TPoint(amap);
        p3.setName("P-3");
        p3.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(240), GMercatorProjection.YToLat(100)));
        amap.getPoints().add(p3);
        p3.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        TPoint p4 = new TPoint(amap);
        p4.setName("P-4");
        p4.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(310), GMercatorProjection.YToLat(100)));
        amap.getPoints().add(p4);
        p4.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        c = new TCategory(amap);
        c.setName("C-1");
        c.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(100), GMercatorProjection.YToLat(50)));
        amap.getCategories().add(c);
        c.getPoints().add(p1);
        c.getPoints().add(p2);
        c.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        c = new TCategory(amap);
        c.setName("C-2");
        c.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(170), GMercatorProjection.YToLat(50)));
        amap.getCategories().add(c);
        c.getPoints().add(p2);
        c.getPoints().add(p3);
        c.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        c = new TCategory(amap);
        c.setName("C-3");
        c.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(240), GMercatorProjection.YToLat(50)));
        amap.getCategories().add(c);
        c.getPoints().add(p3);
        c.getPoints().add(p4);
        c.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        return amap;
    }

    private TMap _createDefaultMapInfo2() {

        TCategory c;

        TMap amap = new TMap();
        amap.setName("@vtest");

        TPoint p1 = new TPoint(amap);
        p1.setName("P-1");
        p1.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(250), GMercatorProjection.YToLat(150)));
        amap.getPoints().add(p1);
        p1.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        c = new TCategory(amap);
        c.setName("C-1");
        c.setCoordinates(new TCoordinates(GMercatorProjection.XToLng(250), GMercatorProjection.YToLat(100)));
        amap.getCategories().add(c);
        c.getPoints().add(p1);
        c.setSyncStatus(SyncStatusType.Sync_Create_Remote);

        return amap;
    }

    private void _init(final String baseFolder1, final String baseFolder2, final INotification sink) throws Exception {

        ModelService.inst._setBaseFolder(baseFolder1);
        ArrayList<TMap> maps1 = ModelService.inst.getUserMapsList(false);
        TMap map1 = _searchMap(maps1, "@vtest");
        if (map1 != null) {
            ModelService.inst.markAsDeletedMap(map1);
            ModelService.inst.purgeDeleted();
        }

        ModelService.inst._setBaseFolder(baseFolder2);
        ArrayList<TMap> maps2 = ModelService.inst.getUserMapsList(false);
        TMap map2 = _searchMap(maps2, "@vtest");
        if (map2 != null) {
            ModelService.inst.markAsDeletedMap(map2);
            ModelService.inst.purgeDeleted();
        }

        if (!GMapService.inst.isLoggedIn()) {
            GMapService.inst.login(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));
        }

        ArrayList<TMap> maps = GMapService.inst.getUserMapsList();
        TMap rmap = _searchMap(maps, "@vtest");
        if (rmap != null) {
            GMapService.inst.deleteMap(rmap);
        }

        TMap defMap = _createDefaultMapInfo1();
        GMapService.inst.createMap(defMap);
        GMapService.inst.updateMap(defMap);

        ModelService.inst._setBaseFolder(baseFolder1);
        ModelService.inst.createMap(defMap);
        ModelService.inst.updateMap(defMap);

        ModelService.inst._setBaseFolder(baseFolder2);
        ModelService.inst.createMap(defMap);
        ModelService.inst.updateMap(defMap);

        sink.done();
    }

    private TMap _searchMap(ArrayList<TMap> maps, String name) {
        for (TMap map : maps) {
            if (map.getName().equals(name)) {
                return map;
            }
        }
        return null;
    }

}
