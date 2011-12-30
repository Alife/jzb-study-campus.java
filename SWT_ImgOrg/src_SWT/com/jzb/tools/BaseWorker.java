/**
 * 
 */
package com.jzb.tools;

import java.io.File;

/**
 * @author n000013
 * 
 */
public abstract class BaseWorker {

    protected interface ICallable {

        public void call(final File baseFolder) throws Exception;
    }

    protected boolean          m_justChecking;
    protected IProgressMonitor m_monitor;

    protected ITracer          m_tracer;

    public BaseWorker(boolean justChecking, ITracer tracer, IProgressMonitor monitor) {
        m_justChecking = justChecking;
        m_monitor = monitor;
        m_tracer = tracer;
    }

    protected void _makeCall(final String baseFolderStr, final ICallable callable) {

        try {

            final File baseFolder = baseFolderStr == null ? null : new File(baseFolderStr);
            if (baseFolder != null && !baseFolder.isDirectory())
                throw new Exception("Indicated folder is not correct: '" + baseFolder + "'");

            Runnable r = new Runnable() {

                public void run() {
                    try {

                        m_tracer._info("Processing started");

                        callable.call(baseFolder);

                        m_tracer._info("Processing finished");
                        m_monitor.processingEnded(false);
                    } catch (Throwable ex) {
                        m_tracer._error("Error in processing execution", ex);
                        m_monitor.processingEnded(true);
                    }
                }
            };

            new Thread(r, "WorkerThread").start();
        } catch (Throwable th) {
            m_tracer._error("Error in processing execution", th);
            m_monitor.processingEnded(true);
        }
    }

}
