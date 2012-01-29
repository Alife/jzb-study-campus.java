/**
 * 
 */
package com.jzb.tpoi.gg;

import java.util.ArrayList;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class MapReaderWorker {

    public interface INotification {

        public void mapRead(TMap map);
    }

    private IProgressMonitor m_monitor;

    public MapReaderWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    public void readMap(final String baseFolder, final INotification sink) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("MapReaderWorker - Start execution");
                    _readMap(baseFolder, sink);
                    Tracer._info("MapReaderWorker - End execution");

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

    private void _readMap(final String baseFolder, final INotification sink) throws Exception {
        ModelService.inst._setBaseFolder(baseFolder);
        ArrayList<TMap> maps = ModelService.inst.getUserMapsList(false);
        TMap map = _searchMap(maps, "@vtest");
        if (map != null) {
            ModelService.inst.readMapData(map);
            sink.mapRead(map);
        }
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
