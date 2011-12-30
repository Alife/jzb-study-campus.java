/**
 * 
 */
package com.jzb.j2me.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

/**
 * @author PS00A501
 * 
 */
public class MessageCanvas extends Alert implements IActivatable, CommandListener {

    public static interface Listener {

        public void done(boolean wasCanceled);
    }

    public static final int        OPTION_OK        = 0x0001;
    public static final int        OPTION_CANCEL    = 0x0002;

    public static final int        OPTION_OK_CANCEL = OPTION_OK | OPTION_CANCEL;
    private static final Command   CMD_OK           = new Command("Ok", Command.OK, 1);
    private static final Command   CMD_CANCEL       = new Command("Cancel", Command.CANCEL, 1);

    private MessageCanvas.Listener m_listener;

    /**
     * @param title
     */
    public MessageCanvas(String title, String text, int options) {
        this(title, text, options, null);
    }

    /**
     * @param title
     */
    public MessageCanvas(String title, String text, int options, MessageCanvas.Listener listener) {
        super(title);

        setTimeout(Alert.FOREVER);
        setString(text);
        m_listener = listener;

        if ((options & OPTION_OK) != 0) {
            addCommand(CMD_OK);
        }
        if ((options & OPTION_CANCEL) != 0) {
            addCommand(CMD_CANCEL);
        }
        setCommandListener(this);
    }

    public void activate() {
        MidletDisplay.setCurrent(this);
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (CMD_OK.equals(cmd) && m_listener != null) {
            m_listener.done(false);
        } else if (CMD_CANCEL.equals(cmd) && m_listener != null) {
            m_listener.done(true);
        }
    }

    public void setListener(MessageCanvas.Listener listener) {
        m_listener = listener;
    }

}
