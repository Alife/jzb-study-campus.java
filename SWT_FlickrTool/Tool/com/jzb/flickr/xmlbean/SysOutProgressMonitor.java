/**
 * 
 */
package com.jzb.flickr.xmlbean;

import com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor;

/**
 * @author n000013
 * 
 */
public class SysOutProgressMonitor implements IProgressMonitor {

    private ITracer m_tracer;

    public SysOutProgressMonitor(ITracer tracer) {
        m_tracer = tracer;
    }

    /**
     * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#newTaskAdded(com.jzb.flickr.xmlbean.IActTask)
     */
    public void newTaskAdded(IActTask actTask, int queued) {
        m_tracer._debug("***> TASK SUBMITED (" + actTask + "). Total queued " + queued);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#processingEnded()
     */
    public void processingEnded(boolean failed) {
        m_tracer._debug("***> TASKS PROCESS FINISHED [Failed = "+failed+"]");
    }

    /**
     * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#taskEnded(com.jzb.flickr.xmlbean.IActTask, int)
     */
    public void taskEnded(IActTask actTask, int msToExecute, int queued) {
        m_tracer._debug("***> TASK FINISHED (" + msToExecute + " / " + actTask + "). Still queued " + queued);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#taskNumberUpdated(int)
     */
    public void taskNumberUpdated(int queued) {
        m_tracer._debug("***> ACTION TASK NUMBER: " + queued);
    }
}
