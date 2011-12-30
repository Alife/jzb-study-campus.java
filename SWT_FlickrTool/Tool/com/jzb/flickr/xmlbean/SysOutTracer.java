/**
 * 
 */
package com.jzb.flickr.xmlbean;

import java.io.PrintStream;

/**
 * @author n000013
 * 
 */
public class SysOutTracer implements ITracer {

    PrintStream m_out = System.out;

    public SysOutTracer() {
    }

    public synchronized void _debug(String msg) {
        if (m_out == null)
            return;
        m_out.println("D " + Thread.currentThread() + "\t- " + msg);
    }

    public synchronized void _error(String msg) {
        if (m_out == null)
            return;
        m_out.println("E " + Thread.currentThread() + "\t- " + msg);
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_warn(java.lang.String, java.lang.Throwable)
     */
    public synchronized void _error(String msg, Throwable th) {
        if (m_out == null)
            return;
        m_out.println("E " + Thread.currentThread() + "\t- " + msg);
        th.printStackTrace(System.out);
    }

    public synchronized void _info(String msg) {
        if (m_out == null)
            return;
        m_out.println("I " + Thread.currentThread() + "\t- " + msg);
    }

    public synchronized void _warn(String msg) {
        if (m_out == null)
            return;
        m_out.println("W " + Thread.currentThread() + "\t- " + msg);
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_warn(java.lang.String, java.lang.Throwable)
     */
    public synchronized void _warn(String msg, Throwable th) {
        if (m_out == null)
            return;
        m_out.println("W " + Thread.currentThread() + "\t- " + msg);
        th.printStackTrace(System.out);
    }
}
