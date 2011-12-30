/**
 * 
 */
package com.isb.patch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is an implementation for the ILogger interface that stores the tracing info
 * inside a file in the file system.
 * 
 * @author PS00A501
 * 
 */
public class FileLogger implements ILogger {

    private PrintStream      m_ps;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");

    /**
     * Class constructor
     * 
     * @param outFile
     *            file's name to be used as log storage
     */
    public FileLogger(String outFile) {

        try {
            m_ps = new PrintStream(new FileOutputStream(outFile, true));
        } catch (FileNotFoundException ex) {
            m_ps = System.out;
        }
    }

    /**
     * This method close the output file used as log storage
     */
    public void close() {
        if (m_ps != null)
            m_ps.close();
    }

    /**
     * Store the object's string representation passed as parameter.
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
            m_ps.println(sb);
        }
    }

    /**
     * Store the object's string representation passed as parameter as well as the exception occurred.
     * Timestamp and thread name is added also.
     * 
     * @see com.isb.patch.ILogger#debug(java.lang.Object, java.lang.Throwable)
     */
    public void debug(Object trace, Throwable th) {
        synchronized (sdf) {
            debug(trace);
            th.printStackTrace(m_ps);
            Throwable e = th;
            while (e instanceof UpdateException) {
                e = ((UpdateException) th).getCause();
                m_ps.println("Cause:");
                e.printStackTrace(m_ps);
            }
        }
    }

}
