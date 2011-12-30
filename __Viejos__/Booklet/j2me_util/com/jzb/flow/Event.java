/**
 * 
 */
package com.jzb.flow;

/**
 * @author PS00A501
 * 
 */
public class Event {

    public static final String EV_OK        = "ok";
    public static final String EV_CANCEL    = "cancel";
    public static final String EV_EXIT      = "exit";
    public static final String EV_BACK      = "back";
    public static final String EV_ERROR     = "error";
    public static final String EV_EXCEPTION = "exception";

    private String             m_name;
    private State              m_sender;
    private Object             m_data;

    public Event(String name, State sender) {
        this(name, sender, null);
    }

    public Event(String name, State sender, Object data) {
        m_name = name;
        m_sender = sender;
        m_data = data;
    }

    public Object getData() {
        return m_data;
    }

    public String getName() {
        return m_name;
    }

    public State getSender() {
        return m_sender;
    }

}
