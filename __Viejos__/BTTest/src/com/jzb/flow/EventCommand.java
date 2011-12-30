package com.jzb.flow;

import javax.microedition.lcdui.Command;

public class EventCommand extends Command {

    private String m_eventID;

    public EventCommand(String eventID, String label, int commandType, int priority) {
        super(label, commandType, priority);
        m_eventID = eventID;
    }

    public EventCommand(String eventID, String shortLabel, String longLabel, int commandType, int priority) {
        super(shortLabel, longLabel, commandType, priority);
        m_eventID = eventID;
    }

    public String getEventID() {
        return m_eventID;
    }

}
