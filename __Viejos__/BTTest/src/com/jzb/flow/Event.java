package com.jzb.flow;

public class Event {

    public static final String EV_ID_OK          = "OK";
    public static final String EV_ID_CANCEL      = "CANCEL";
    public static final String EV_ID_ERROR       = "ERROR";
    public static final String EV_ID_FATAL_ERROR = "FATAL_ERROR";

    private String             m_id;
    private Object             m_data;

    public Event(String eventID) {
        this(eventID, null);
    }

    public Event(String eventID, Object data) {
        m_id = eventID;
        m_data = data;
    }

    public String getID() {
        return m_id;
    }

    public Object getData() {
        return m_data;
    }

}
