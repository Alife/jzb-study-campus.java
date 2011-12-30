/**
 * 
 */
package com.jzb.j2me.util;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * @author PS00A501
 * 
 */
public abstract class BaseMidlet extends MIDlet {

    private boolean m_initialized = false;

    /**
     * 
     */
    public BaseMidlet() {
        MidletDisplay.setMidlet(this);
    }

    /**
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    protected void destroyApp(boolean arg0) {
        notifyDestroyed();
    }

    protected abstract void init() throws Exception;

    /**
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    protected void pauseApp() {
        notifyPaused();
    }

    protected abstract void start() throws Exception;

    /**
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    protected void startApp() throws MIDletStateChangeException {

        String txtPhase = null;

        try {
            if (!m_initialized) {
                txtPhase = "initializing";
                init();
            }
            txtPhase = "starting";
            start();

        } catch (Throwable th) {
            th.printStackTrace();
            Utils.showException("Error " + txtPhase + " the application", th);
        }

    }

}
