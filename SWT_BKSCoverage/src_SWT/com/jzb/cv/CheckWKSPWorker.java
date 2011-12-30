/**
 * 
 */
package com.jzb.cv;

import java.io.File;

import com.jzb.bc.CoverageTester;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class CheckWKSPWorker {

    private IProgressMonitor m_monitor;

    public CheckWKSPWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    public void checkWKSP(final String WKSPFolder, final String storageFile, final boolean loadFromStorage) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("CheckWKSPWorker - Start execution");
                    _checkWKSP(_getFolder(WKSPFolder),_getFile(storageFile),loadFromStorage);
                    Tracer._info("CheckWKSPWorker - End execution");

                    m_monitor.processingEnded(false, "OK");
                } catch (Throwable th) {
                    Tracer._error("Error executing IPA CheckWKSPWorker", th);
                    m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "RenameWorker").start();
    }

    private void _checkWKSP(final File WKSPFolder, final File storageFile, final boolean loadFromStorage) throws Exception {
        
        CoverageTester tester= new CoverageTester();
        tester.checkWKSP(WKSPFolder, storageFile, loadFromStorage);
    }

    private File _getFolder(String name) throws Exception {

        File folder = name == null ? null : new File(name);
        if (folder != null && !folder.isDirectory()) {
            throw new Exception("Folder is not correct: '" + name + "'");
        } else {
            return folder;
        }

    }

    private File _getFile(String name) throws Exception {

        File f = name == null ? null : new File(name);
        if (f != null && !f.getParentFile().isDirectory() && (!f.exists() || (f.exists() && f.canWrite()))) {
            throw new Exception("File is not correct: '" + name + "'");
        } else {
            return f;
        }
    }
}
