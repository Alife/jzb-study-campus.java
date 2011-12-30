/**
 * 
 */
package com.jzb.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author n000013
 * 
 */
public class TabbedTracerImpl implements ITracer {

    private enum Level {
        DEBUG, ERROR, INFO, WARN
    };

    public TabItem           m_debugTab;
    public TabItem           m_errorTab;
    public TabItem           m_infoTab;
    public TabItem           m_warnTab;

    private SimpleDateFormat m_sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    public TabbedTracerImpl(TabItem debug, TabItem info, TabItem warn, TabItem error) {
        m_debugTab = debug;
        m_infoTab = info;
        m_warnTab = warn;
        m_errorTab = error;
    }

    public void _addTraceText(final Level level, final String msg) {

        final String threadName = Thread.currentThread().getName();

        Display.getDefault().asyncExec(new Runnable() {

            @SuppressWarnings("synthetic-access")
            public void run() {

                Text control = getText(level);

                String fullMsg = getLevelTxt(level) + " " + m_sdf.format(System.currentTimeMillis()) + " " + threadName + "\t- " + msg + "\r\n";

                control.append(fullMsg);

                int lc = control.getLineCount();
                control.setTopIndex(lc);
                setTabTitle(level, lc);
            }
        });
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_debug(java.lang.String)
     */
    public void _debug(String msg) {
        _addTraceText(Level.DEBUG, msg);
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_error(java.lang.String)
     */
    public void _error(String msg) {
        _addTraceText(Level.ERROR, msg);
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_error(java.lang.String, java.lang.Throwable)
     */
    public void _error(String msg, Throwable th) {
        StringWriter sw = new StringWriter();
        th.printStackTrace(new PrintWriter(sw));
        _addTraceText(Level.ERROR, msg);
        _addTraceText(Level.ERROR, sw.getBuffer().toString());
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_info(java.lang.String)
     */
    public void _info(String msg) {
        _addTraceText(Level.INFO, msg);
        // Si son diferentes lo saca por las dos pestañas
        if (getTab(Level.INFO) != getTab(Level.DEBUG))
            _addTraceText(Level.DEBUG, msg);
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_warn(java.lang.String)
     */
    public void _warn(String msg) {
        _addTraceText(Level.WARN, msg);
    }

    /**
     * @see com.jzb.flickr.xmlbean.ITracer#_warn(java.lang.String, java.lang.Throwable)
     */
    public void _warn(String msg, Throwable th) {
        StringWriter sw = new StringWriter();
        th.printStackTrace(new PrintWriter(sw));
        _addTraceText(Level.WARN, msg);
        _addTraceText(Level.WARN, sw.getBuffer().toString());
    }

    public void reset() {

        Display.getDefault().asyncExec(new Runnable() {

            @SuppressWarnings("synthetic-access")
            public void run() {
                setTabTitle(Level.DEBUG, 0);
                setTabTitle(Level.INFO, 0);
                setTabTitle(Level.WARN, 0);
                setTabTitle(Level.ERROR, 0);
                getText(Level.DEBUG).setText("");
                getText(Level.INFO).setText("");
                getText(Level.WARN).setText("");
                getText(Level.ERROR).setText("");
            }
        });

    }

    private String getLevelTxt(Level level) {
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

    private TabItem getTab(Level level) {
        switch (level) {
            case DEBUG:
                return m_debugTab;
            case INFO:
                return m_infoTab;
            case WARN:
                return m_warnTab;
            case ERROR:
                return m_errorTab;
            default:
                // by default we return "debug"
                return m_debugTab;

        }
    }

    private Text getText(Level level) {
        return (Text) getTab(level).getControl();
    }

    private void setTabTitle(Level level, int count) {
        String str;
        switch (level) {
            case DEBUG:
                str = "Debug";
                break;
            case INFO:
                str = "Info";
                break;
            case WARN:
                str = "Warning";
                break;
            case ERROR:
                str = "Error";
                break;
            default:
                str = "Unknown";
                break;
        }
        if (count > 0)
            str += "(" + count + ")";
        getTab(level).setText(str);
    }

}
