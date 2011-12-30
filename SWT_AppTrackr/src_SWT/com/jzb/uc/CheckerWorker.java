/**
 * 
 */
package com.jzb.uc;

import java.io.File;
import java.util.ArrayList;

import com.jzb.at.IPAData;
import com.jzb.at.UpdateChecker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class CheckerWorker {

    private IProgressMonitor m_monitor;

    public CheckerWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    private File _getFolder(String name) throws Exception {

        File folder = name == null ? null : new File(name);
        if (folder != null && !folder.isDirectory()) {
            throw new Exception("Base folder is not correct: '" + name + "'");
        } else {
            return folder;
        }
    }

    public void checkUpdates(final String IPAsFolder, final boolean debug) {
        new Thread(new Runnable() {

            public void run() {
                try {

                    Tracer._info("CheckWorker - Start execution");
                    String html=_checkUpdates(_getFolder(IPAsFolder),debug);
                    Tracer._info("CheckWorker - End execution");

                    m_monitor.processingEnded(false, html);
                } catch (Throwable th) {
                    Tracer._error("Error executing IPA UpdateWorker", th);
                    m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "UpdateWorker").start();
    }

    private String _checkUpdates(final File IPAsFolder, final boolean debug) throws Exception {

        UpdateChecker uc=new UpdateChecker(IPAsFolder, debug); 
        ArrayList<IPAData> list = uc.checkUpdates();
        return uc.getUpdateInfoInHTML(list);
    }


}
