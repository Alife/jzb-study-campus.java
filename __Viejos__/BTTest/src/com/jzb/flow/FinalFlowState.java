package com.jzb.flow;

public class FinalFlowState implements IState {

    private String         m_name;
    private String         m_eventName;
    private IEventListener m_myListener;

    public FinalFlowState(String name, String eventName) {
        super();
        m_name = name;
        m_eventName = eventName;
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
        fireEvent(new Event(m_eventName));
    }

    public void signalException(Throwable th) {
        fireEvent(new Event(Event.EV_ID_FATAL_ERROR, th));
    }

}
