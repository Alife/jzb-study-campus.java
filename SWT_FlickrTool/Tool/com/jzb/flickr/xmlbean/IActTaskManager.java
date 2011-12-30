/**
 * 
 */
package com.jzb.flickr.xmlbean;

/**
 * @author n000013
 * 
 */
public interface IActTaskManager {

    public static interface IProgressMonitor {

        public void newTaskAdded(IActTask actTask, int queued);

        public void processingEnded(boolean failed);

        public void taskEnded(IActTask actTask, int msToExecute, int queued);
    }

    public void abortProcessing();

    public void submitActTask(IActTask actTask);

    public void waitForFinishing() throws Exception;
}
