/**
 * 
 */
package com.jzb.flow;

import javax.microedition.lcdui.Command;

/**
 * @author PS00A501
 * 
 */
public class EnumCommand extends Command {

    public static final EnumCommand ECMD_OK     = new EnumCommand(Command.OK, Event.EV_OK, "Ok", Command.OK, 1);
    public static final EnumCommand ECMD_CANCEL = new EnumCommand(Command.CANCEL, Event.EV_CANCEL, "Cancel", Command.CANCEL, 1);
    public static final EnumCommand ECMD_EXIT   = new EnumCommand(Command.EXIT, Event.EV_EXIT, "Exit", Command.EXIT, 1);
    public static final EnumCommand ECMD_BACK   = new EnumCommand(Command.BACK, Event.EV_BACK, "Back", Command.BACK, 1);

    private int                     m_code;
    private String                  m_event;

    /**
     * @param label
     * @param commandType
     * @param priority
     */
    public EnumCommand(int code, String label, int commandType, int priority) {
        super(label, commandType, priority);
        m_code = code;
    }

    /**
     * @param label
     * @param commandType
     * @param priority
     */
    public EnumCommand(int code, String event, String label, int commandType, int priority) {
        super(label, commandType, priority);
        m_code = code;
        m_event = event;
    }

    public int getCode() {
        return m_code;
    }

    public String getEvent() {
        return m_event;
    }
}
