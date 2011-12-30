/**
 * 
 */
package com.jzb.flickr.xmlbean;

import com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor;

/**
 * @author n000013
 * 
 */
public class NullProgressMonitor implements IProgressMonitor {

    /**
     * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#newTaskAdded(com.jzb.flickr.xmlbean.IActTask)
     */
    public void newTaskAdded(IActTask actTask, int queued) {
    }

    /**
     * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#processingEnded()
     */
    public void processingEnded(boolean failed) {
    }

    /**
     * @see com.jzb.flickr.xmlbean.IActTaskManager.IProgressMonitor#taskEnded(com.jzb.flickr.xmlbean.IActTask, int)
     */
    public void taskEnded(IActTask actTask, int msToExecute, int queued) {
    }
}
