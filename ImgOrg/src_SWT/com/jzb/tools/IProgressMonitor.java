/**
 * 
 */
package com.jzb.tools;



/**
 * @author n000013
 *
 */
public interface IProgressMonitor {

    public void newTaskAdded(IActTask actTask, int queued);

    public void processingEnded(boolean failed);

    public void taskEnded(IActTask actTask, int msToExecute, int queued);
}
