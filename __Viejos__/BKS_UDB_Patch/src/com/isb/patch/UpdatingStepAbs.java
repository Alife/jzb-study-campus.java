/**
 * 
 */
package com.isb.patch;

/**
 * Abstract base class for Updating Steps. It provides some convenience methods.
 * 
 * @author PS00A501
 * 
 */
public abstract class UpdatingStepAbs implements IUpdatingStep {

    /**
     * Inner class that will wrap the logger in order to add the Step name in every trace.
     * 
     * @author PS00A501
     * 
     */
    private class InnerLogger implements ILogger {

        private ILogger m_logger;

        /**
         * Class Constructor
         * 
         * @param logger
         *            Logger instance to be wrapped
         */
        public InnerLogger(ILogger logger) {
            m_logger = logger;
        }

        /**
         * Adds the current Step's name to every trace
         * 
         * @see com.isb.patch.ILogger#debug(java.lang.Object)
         */
        public void debug(Object trace) {
            StringBuffer sb = new StringBuffer();
            sb.append(getStepID());
            sb.append(" - ");
            sb.append(trace);
            m_logger.debug(sb);
        }

        /**
         * Adds the current Step's name to every trace
         * 
         * @see com.isb.patch.ILogger#debug(java.lang.Object, java.lang.Throwable)
         */
        public void debug(Object trace, Throwable th) {
            StringBuffer sb = new StringBuffer();
            sb.append(getStepID());
            sb.append(" - ");
            sb.append(trace);
            m_logger.debug(sb, th);
        }
    }

    private ILogger         m_logger;
    private IProgessMonitor m_monitor;
    private boolean         m_wasCanceled;

    /**
     * Default Constructor 
     */
    public UpdatingStepAbs() {
        m_wasCanceled = false;
    }

    /**
     * Mark the step as canceled
     *  
     * @see com.isb.patch.IUpdatingStep#canceled()
     */
    public void canceled() {
        m_wasCanceled = true;
    }

    /**
     * Stored the Logger and Monitor before executing the current step in order to
     * make them easily accessible.
     * Additionally, shows traces informing about the start, end and possible errors.
     * 
     * @see com.isb.patch.IUpdatingStep#executeStep(com.isb.patch.IUpdatingStep.IProgessMonitor, com.isb.patch.ILogger)
     */
    public boolean executeStep(final IProgessMonitor monitor, final ILogger logger) {

        boolean wasOK = true;

        if (!wasCanceled()) {
            m_logger = new InnerLogger(logger);
            m_monitor = monitor;
            try {
                m_logger.debug("Starting UpdatingStep execution: '" + getDescription() + "'");
                monitor.updateProgress(0);
                innerexecuteStep();
                monitor.updateProgress(100);
                m_logger.debug("Finished UpdatingStep execution: '" + getDescription() + "'");
            } catch (UpdateException ex) {
                m_logger.debug("Exception during UpdatingStep execution: '" + getDescription() + "'");
                monitor.notifyException(ex);
                wasOK = false;
            }
            m_monitor = null;
            m_logger = null;
        }

        return wasOK;
    }

    /**
     * @see com.isb.patch.IUpdatingStep#getDescription()
     */
    public abstract String getDescription();

    /**
     * @return the logger
     */
    protected ILogger getLogger() {
        return m_logger;
    }

    /**
     * @return the monitor
     */
    protected IProgessMonitor getMonitor() {
        return m_monitor;
    }

    /**
     * @return The Step name to be appended to traces generated
     */
    protected abstract String getStepID();

    /**
     * The actual Updating Step code comes here.
     * 
     * @throws UpdateException In something fails
     */
    protected abstract void innerexecuteStep() throws UpdateException;

    /**
     * @return the wasCanceled
     */
    protected boolean wasCanceled() {
        return m_wasCanceled;
    }

}
