package com.jzb.flow;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import com.jzb.j2me.util.MidletDisplay;

public abstract class AlertState extends Alert implements IState, CommandListener {

    private String         m_name;
    private IEventListener m_myListener;

    private Command        m_doneCmd;

    public AlertState(String stateName, String title) {
        super(title);
        m_name = stateName;
        m_doneCmd = getDoneCommand();
        addCommand(m_doneCmd);
        setCommandListener(this);
        setType(AlertType.CONFIRMATION);
        setTimeout(Alert.FOREVER);
    }

    public final void commandAction(Command cmd, Displayable disp) {
        if (cmd instanceof EventCommand) {
            fireEvent(new Event(((EventCommand) cmd).getEventID()));
        } else {
            Event ev = _commandAction(cmd, disp);
            fireEvent(ev);
        }
    }

    protected Event _commandAction(Command cmd, Displayable disp) {
        if (m_doneCmd.equals(cmd))
            return new Event(Event.EV_ID_OK);
        else
            return null;
    }

    public AlertState(String stateName, String title, String alertText, Image alertImage, AlertType alertType) {
        super(title, alertText, alertImage, alertType);
        m_name = stateName;
    }

    protected Command getDoneCommand() {
        return new EventCommand(Event.EV_ID_OK, "Ok", Command.OK, 1);
    }

    public String getName() {
        return m_name;
    }

    public void setEventListener(IEventListener listener) {
        m_myListener = listener;
    }

    protected void fireEvent(Event ev) {
        if (m_myListener != null && ev != null) {
            m_myListener.eventAction(ev, getName());
        }
    }

    public void activate() {
        MidletDisplay.get().setCurrent(this);
    }

    public void signalException(Throwable th) {
        fireEvent(new Event(Event.EV_ID_FATAL_ERROR, th));
    }

}
