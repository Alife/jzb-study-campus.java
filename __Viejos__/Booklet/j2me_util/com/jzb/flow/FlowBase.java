/**
 * 
 */
package com.jzb.flow;

import java.util.Hashtable;

import com.jzb.j2me.util.MidletDisplay;
import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
public abstract class FlowBase implements Flow {

    private class EventRunner implements Runnable {

        private Event m_firedEvent;

        public EventRunner(Event ev) {
            m_firedEvent = ev;
        }

        public void run() {
            processFiredEvent(m_firedEvent);
        }
    }
    private String    m_name;
    private Hashtable m_states      = new Hashtable();
    private Hashtable m_transitions = new Hashtable();
    private State     m_currentState;
    private State     m_initialState;
    private Flow      m_owner;

    private Hashtable m_data        = new Hashtable();

    public FlowBase() throws Exception {
        this(null, null);
    }

    public FlowBase(Flow owner, String name) throws Exception {
        m_owner = owner;
        m_name = name;
        createStates();
        m_initialState = getInitialState();
        if (m_initialState == null)
            throw new Exception("No initial state has been defined in flow '" + getName() + "'");
        m_currentState = m_initialState;
    }

    public void activate(Event ev) {
        m_initialState.activate(ev);
    }

    /**
     * @see com.jzb.flow.State#deactivate()
     */
    public void deactivate() {
    }

    /**
     * @see com.jzb.flow.Flow#fireEvent(com.jzb.flow.Event)
     */
    public void eventFired(Event ev) {
        ev.getName();
        MidletDisplay.callSerially(new EventRunner(ev));
    }

    /**
     * @see com.jzb.flow.Flow#getData(java.lang.String)
     */
    public Object getData(String id) {
        return m_data.get(id);
    }

    public String getName() {
        return m_name;
    }

    public State getState(String name) {
        return (State) m_states.get(name);
    }

    public void init(Flow owner, String name, Properties props) throws Exception {
        m_owner = owner;
        m_name = name;
    }

    /**
     * @see com.jzb.flow.Flow#setData(java.lang.String, java.lang.Object)
     */
    public void setData(String id, Object data) {
        m_data.put(id, data);
    }

    protected void addState(State newState) {
        if (m_states.isEmpty()) {
            m_initialState = newState;
        }
        m_states.put(newState.getName(), newState);
    }

    protected void addTransition(String origStateName, String eventName, String destStateName) throws Exception {

        if ((getState(origStateName) == null && !ANY_STATE_NAME.equals(origStateName)) || getState(destStateName) == null)
            throw new Exception("Error adding transition. Both states, '" + origStateName + "' and '" + destStateName + "', have to be created (origin state can be '" + ANY_STATE_NAME + "')");

        String key = origStateName + "." + eventName;
        m_transitions.put(key, destStateName);
    }

    protected abstract void createStates() throws Exception;

    protected void endFlow(Event ev) {
        if (m_owner != null) {
            Event ev2 = new Event(ev.getName(), this, ev.getData());
            m_owner.eventFired(ev2);
        }
    }

    protected State getInitialState() {
        return m_initialState;
    }

    private State findDestinationState(Event ev) {

        String trName;
        String stateName;

        trName = ev.getSender().getName() + "." + ev.getName();
        stateName = (String) m_transitions.get(trName);

        if (stateName == null) {
            trName = ANY_STATE_NAME + "." + ev.getName();
            stateName = (String) m_transitions.get(trName);
        }

        if (stateName != null)
            return (State) m_states.get(stateName);
        else
            return null;
    }

    private void processFiredEvent(Event ev) {

        if (ev.getSender() instanceof FinalState) {
            endFlow(ev);
        } else {
            State nextState = findDestinationState(ev);
            if (nextState != null) {
                if (m_currentState != null)
                    m_currentState.deactivate();
                m_currentState = nextState;
                m_currentState.activate(ev);
            } else {
                System.out.println("No destination state found, in flow '" + m_name + "', for event '" + ev.getName() + "' and sender '" + ev.getSender().getName() + "'");
            }
        }
    }
}
