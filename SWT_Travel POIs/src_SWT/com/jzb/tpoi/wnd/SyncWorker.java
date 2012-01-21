/**
 * 
 */
package com.jzb.tpoi.wnd;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.tpoi.srvc.GMapService;
import com.jzb.tpoi.srvc.SyncService;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class SyncWorker {

    private IProgressMonitor m_monitor;

    public SyncWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    public void syncAllMaps(final String usr, final String pwd) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("SyncWorker - Start execution");
                    _syncAllMaps(usr, pwd);
                    Tracer._info("SyncWorker - End execution");

                    m_monitor.processingEnded(false, "OK");
                } catch (Throwable th) {
                    Tracer._error("Error executing SyncWorker", th);
                    m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "RenameWorker").start();
    }

    private void _syncAllMaps(final String usr, final String pwd) throws Exception {
        GMapService.inst.login(usr, pwd);
        SyncService.inst.syncAllMaps();
    }
}
