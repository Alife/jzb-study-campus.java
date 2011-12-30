package com.jzb.flow;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.jzb.j2me.util.MidletDisplay;

public abstract class FormState extends Form implements IState, CommandListener {

    private String         m_name;
    private IEventListener m_myListener;

    public FormState(String stateName, String title) {
        super(title);
        m_name = stateName;
        createItems();
        setCommandListener(this);
    }

    protected abstract void createItems();

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

    public final void commandAction(Command cmd, Displayable disp) {

        if (cmd instanceof EventCommand) {
            fireEvent(new Event(((EventCommand) cmd).getEventID()));
        } else {
            Event ev = _commandAction(cmd, disp);
            fireEvent(ev);
        }
    }

    protected Event _commandAction(Command cmd, Displayable disp) {
        return null;
    }

}
