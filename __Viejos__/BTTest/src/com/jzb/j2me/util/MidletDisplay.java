package com.jzb.j2me.util;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public final class MidletDisplay {

    private static Display m_display;

    public static void set(MIDlet theMidlet) {
        m_display = Display.getDisplay(theMidlet);
    }

    public static Display get() {
        return m_display;
    }
}
