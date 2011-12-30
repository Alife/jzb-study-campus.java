/**
 * 
 */
package com.isb.patch;

/**
 * This exception is used to store a message describing the error as well as a code to be shown to the user.
 * 
 * @author IS201105
 * 
 */
public class UpdateException extends Exception {

    private Throwable m_cause;
    private String    m_code;

    /**
     * Class Constructor
     * 
     * @param code
     *            String code to be shown to the user
     * 
     * @param message
     *            String message describing the exception
     */
    public UpdateException(String code, String message) {
        super(message);
        m_code = code;
    }

    /**
     * Class Constructor
     * 
     * @param code
     *            String code to be shown to the user
     * 
     * @param message
     *            String message describing the exception
     * 
     * @param cause
     *            Previous exception (non PatchException) that happened and that was wrapped by this one
     */
    public UpdateException(String code, String message, Throwable cause) {
        super(message);
        m_code = code;
        m_cause = cause;
    }

    /**
     * @return the cause
     */
    public Throwable getCause() {
        return m_cause;
    }

    /**
     * @return the String Code that was passed in the constructor
     */
    public String getCode() {
        return m_code;
    }

    /**
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        super.toString();
        String s = getClass().getName();
        String message = "code = " + m_code + ", message = " + getLocalizedMessage();
        if (message == null)
            message = "";
        return s + ": " + message;
    }

}
