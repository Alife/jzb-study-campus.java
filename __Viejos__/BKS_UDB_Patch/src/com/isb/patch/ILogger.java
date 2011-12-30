/**
 * 
 */
package com.isb.patch;

/**
 * Instances of this interface can be created and used to show tracing info inside the application.
 * 
 * @author PS00A501
 * 
 */
public interface ILogger {

    /**
     * This method prints or stores, depending on the current implementation, the parameter's String representation.
     * 
     * @param trace
     *            Object which its String representation has to be printed or stored
     */
    public void debug(Object trace);

    /**
     * This method prints or stores, depending on the current implementation, the parameter's String representation. Additionally it stores/prints the information about the exception passed.
     * 
     * @param trace
     *            Object which its String representation has to be printed or stored
     * 
     * @param th
     *            Exception which information is going to be printed or stored
     */
    public void debug(Object trace, Throwable th);
}
