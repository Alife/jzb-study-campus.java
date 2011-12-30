/**
 * 
 */
package com.isb.patch;

/**
 * This interface has to be implemented by "Updating Steps" classes. Through it they will receive the command to execute their concrete tasks, will have the opportunity to update the task's progress
 * and will be informed that its work has been canceled.
 * 
 * @author PS00A501
 * 
 */
public interface IUpdatingStep {

    /**
     * Inner class used as call-back to inform the caller about the task's progress
     * 
     * @author PS00A501
     * 
     */
    public interface IProgessMonitor {

        /**
         * This method informs the caller that an exception has occurred.
         * 
         * @param ex
         *            The exception caught
         */
        public void notifyException(UpdateException ex);

        /**
         * This method informs the caller about the taks's progress
         * 
         * @param newPercentage
         *            The new value, within 0-100 range, for task's progress
         */
        public void updateProgress(int newPercentage);
    }

    /**
     * This method will be called to inform that the work has been canceled and has to stop executing as soon as possible.
     */
    public void canceled();

    /**
     * This method will be called to execute the step's current implementation. The implementation has to return TRUE if the execution was correct, and FALSE otherwise.
     * 
     * @param monitor
     *            The monitor call-back to be used
     * @param logger
     *            A logger to be used
     * 
     * @return TRUE if the execution was correct, and FALSE otherwise.
     */
    public boolean executeStep(IProgessMonitor monitor, ILogger logger);

    /**
     * This method has to return the step description to shown to the user
     * 
     * @return The description to be shown to the user
     */
    public String getDescription();

}
