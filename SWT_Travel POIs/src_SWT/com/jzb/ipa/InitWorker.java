/**
 * 
 */
package com.jzb.ipa;

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
public class InitWorker {

    private IProgressMonitor m_monitor;

    public InitWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    public void initMaps(final Panel_ListOfMaps mapListPanel) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("InitWorker - Start execution");
                    _initMaps(mapListPanel);
                    Tracer._info("InitWorker - End execution");

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

    private void _initMaps(final Panel_ListOfMaps mapListPanel) throws Exception {
        ModelService.inst._setBaseFolder("C:\\Users\\n63636\\Desktop\\xIPAs\\gmaps");
        ArrayList<TMap> maps = ModelService.inst.getUserMapsList();
        mapListPanel.setMaps(maps);
    }

}
