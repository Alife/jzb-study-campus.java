/**
 * 
 */
package com.isb.patch;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is an implementation for the ILogger interface that prints the tracing info
 * in the default output console.
 * 
 * @author PS00A501
 * 
 */
public class SysOutLogger implements ILogger {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");

    /**
     * Default constructor
     */
    public SysOutLogger() {
    }

    /**
     * Print the object's string representation passed as parameter.
     * Timestamp and thread name is added also.
     * 
     * @see com.isb.patch.ILogger#debug(java.lang.Object)
     */
    public void debug(Object trace) {
        synchronized (sdf) {
            StringBuffer sb = new StringBuffer();
            sb.append(sdf.format(new Date()));
            sb.append(" [");
            sb.append(Thread.currentThread().getName());
            sb.append("] ");
            sb.append(trace);
            System.out.println(sb);
        }
    }

    /**
     * Print the object's string representation passed as parameter as well as the exception occurred.
     * Timestamp and thread name is added also.
     * 
     * @see com.isb.patch.ILogger#debug(java.lang.Object, java.lang.Throwable)
     */
    public void debug(Object trace, Throwable th) {
        synchronized (sdf) {
            debug(trace);
            th.printStackTrace(System.out);
            Throwable e = th;
            while (e instanceof UpdateException) {
                e = ((UpdateException) th).getCause();
                System.out.println("Cause:");
                e.printStackTrace(System.out);
            }
        }
    }

}
