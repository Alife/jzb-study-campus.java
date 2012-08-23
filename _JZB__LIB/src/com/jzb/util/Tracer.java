/**
 * 
 */
package com.jzb.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author n63636
 * 
 */
public abstract class Tracer {

    public enum Level {
        DEBUG, ERROR, INFO, WARN
    }

    private static Tracer          s_tracer   = new PStreamTracer();

    private StringBuffer           m_sbDebug  = new StringBuffer();
    private StringBuffer           m_sbError  = new StringBuffer();
    private StringBuffer           m_sbInfo   = new StringBuffer();
    private StringBuffer           m_sbWarn   = new StringBuffer();

    private volatile transient int m_cntDebug = 0;
    private int                    m_cntError = 0;
    private int                    m_cntInfo  = 0;
    private int                    m_cntWarn  = 0;

    private Timer                  m_flushTimer;

    private SimpleDateFormat       m_sdf      = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    private boolean                m_buffered;

    public Tracer(boolean buffered) {
        m_buffered = buffered;
        if (buffered) {
            m_flushTimer = new Timer("flushTimer", true);

            TimerTask flushTask = new TimerTask() {

                @Override
                public void run() {
                    Tracer.flush();
                }
            };

            m_flushTimer.schedule(flushTask, 2000, 2000);
        }
    }

    protected boolean _propagateLevels(Level l) {
        return false;
    }

    public static void _debug(Object msg) {
        if (msg != null)
            s_tracer.__debug(msg.toString());
        else
            s_tracer.__debug(null);
    }

    public static void _error(String msg) {
        if (msg != null)
            s_tracer.__error(msg.toString());
        else
            s_tracer.__error(null);
    }

    public static void _error(String msg, Throwable th) {
        if (msg != null)
            s_tracer.__error(msg.toString(), th);
        else
            s_tracer.__error(null, th);
    }

    public static void _info(String msg) {
        if (msg != null)
            s_tracer.__info(msg.toString());
        else
            s_tracer.__info(null);
    }

    public static void _warn(String msg) {
        if (msg != null)
            s_tracer.__warn(msg.toString());
        else
            s_tracer.__warn(null);
    }

    public static void _warn(String msg, Throwable th) {
        if (msg != null)
            s_tracer.__warn(msg.toString(), th);
        else
            s_tracer.__warn(null, th);
    }

    public static String getLevelText(Level level) {
        switch (level) {
            case DEBUG:
                return "D";
            case INFO:
                return "I";
            case WARN:
                return "W";
            case ERROR:
                return "E";
            default:
                return "U";
        }
    }

    public static Tracer getTracer() {
        return s_tracer;
    }

    public static void reset() {
        s_tracer._reset();
    }

    public static void flush() {
        s_tracer._flush();
    }

    public static void setTracer(Tracer tracer) {
        s_tracer = tracer;
    }

    protected void __debug(String msg) {
        _addTraceText(Level.DEBUG, msg);
    }

    protected void __error(String msg) {
        _addTraceText(Level.ERROR, msg);
        if (_propagateLevels(Level.ERROR)) {
            _addTraceText(Level.WARN, msg);
        }
    }

    protected void __error(String msg, Throwable th) {
        StringWriter sw = new StringWriter();
        if (th != null)
            th.printStackTrace(new PrintWriter(sw));
        _addTraceText(Level.ERROR, msg);
        _addTraceText(Level.ERROR, sw.getBuffer().toString());
        if (_propagateLevels(Level.ERROR)) {
            _addTraceText(Level.WARN, msg);
            _addTraceText(Level.WARN, sw.getBuffer().toString());
        }
    }

    protected void __info(String msg) {
        _addTraceText(Level.INFO, msg);
        if (_propagateLevels(Level.INFO)) {
            _addTraceText(Level.DEBUG, msg);
        }
    }

    protected void __warn(String msg) {
        _addTraceText(Level.WARN, msg);
    }

    protected void __warn(String msg, Throwable th) {
        StringWriter sw = new StringWriter();
        if (th != null)
            th.printStackTrace(new PrintWriter(sw));
        _addTraceText(Level.WARN, msg);
        _addTraceText(Level.WARN, sw.getBuffer().toString());
    }

    protected void _flush() {

        StringBuffer sb;

        for (Level level : Level.values()) {
            sb = _getStringBuffer(level);
            int len;
            synchronized (sb) {
                len = sb.length();
            }
            if (len > 0) {
                _showTraceText(level, sb);
                _resetBufferCount(level);
            }
        }

    }

    protected void _reset() {
    }

    protected abstract void _showTraceText(final Level level, final StringBuffer sb);

    private void _addTraceText(final Level level, final String msg) {

        String threadName = Thread.currentThread().getName();
        String fullMsg = Tracer.getLevelText(level) + " " + m_sdf.format(System.currentTimeMillis()) + " " + threadName + "\t- " + (msg != null ? msg : "null") + "\r\n";

        int counter;
        StringBuffer sb = _getStringBuffer(level);
        synchronized (sb) {
            sb.append(fullMsg);
            counter = _incBufferCount(level);
        }

        if (!m_buffered || counter >= 50) {
            _resetBufferCount(level);
            _showTraceText(level, sb);
        }
    }

    private synchronized int _incBufferCount(Level level) {
        switch (level) {
            case DEBUG:
                return ++m_cntDebug;
            case INFO:
                return ++m_cntInfo;
            case WARN:
                return ++m_cntWarn;
            case ERROR:
                return ++m_cntError;
            default:
                // by default we return "debug"
                return ++m_cntDebug;
        }
    }

    private synchronized void _resetBufferCount(Level level) {
        switch (level) {
            case DEBUG:
                m_cntDebug = 0;
            case INFO:
                m_cntInfo = 0;
            case WARN:
                m_cntWarn = 0;
            case ERROR:
                m_cntError = 0;
            default:
                // by default we return "debug"
                m_cntDebug = 0;
        }
    }

    private StringBuffer _getStringBuffer(Level level) {
        switch (level) {
            case DEBUG:
                return m_sbDebug;
            case INFO:
                return m_sbInfo;
            case WARN:
                return m_sbWarn;
            case ERROR:
                return m_sbError;
            default:
                // by default we return "debug"
                return m_sbDebug;
        }
    }

}
