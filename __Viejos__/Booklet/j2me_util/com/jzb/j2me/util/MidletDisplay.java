package com.jzb.j2me.util;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public final class MidletDisplay {

    private static Display m_display;

    public static void callSerially(Runnable runable) {
        if (m_display.getCurrent() != null)
            m_display.callSerially(runable);
        else
            runable.run();
    }

    public static Display getDisplay() {
        return m_display;
    }

    public static void setCurrent(Displayable nextDisp) {
        m_display.setCurrent(nextDisp);
    }

    public static void setMidlet(MIDlet theMidlet) {
        m_display = Display.getDisplay(theMidlet);
    }
}
