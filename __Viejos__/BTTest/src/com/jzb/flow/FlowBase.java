package com.jzb.flow;

import java.util.Hashtable;

public abstract class FlowBase implements IFlow {

    private static final String NAMES_SEP              = "¬";
    private static final String FATAL_ERROR_STATE_NAME = "FATAL_ERROR_STATE";

    private String              m_name;

    private Hashtable           m_states               = new Hashtable();
    private Hashtable           m_transitions          = new Hashtable();

    private IState              m_currentState;

    private class ListenerView implements IEventListener {
        public void eventAction(Event ev, String senderStateName) {
            _eventAction(ev, senderStateName);
        };
    }

    private IEventListener m_listenerView = new ListenerView();
    private IEventListener m_myListener;

    public FlowBase(String name) {
        super();
        m_name = name;
        createStates();
        m_currentState = (IState) m_states.get(getInitialStateName());
        if (m_currentState == null)
            throw new NullPointerException("Initial state cannot be null in flow '" + m_name + "'");
    }

    protected IState getState(String stateName) {
        return (IState) m_states.get(stateName);
    }

    protected abstract void createStates();

    protected abstract String getInitialStateName();

    public String getName() {
        return m_name;
    }

    public void activate() {
        activateNextState(m_currentState);
    }

    public void signalException(Throwable th) {
        fireEvent(new Event(Event.EV_ID_FATAL_ERROR, th));
    }

    public void setEventListener(IEventListener listener) {
        m_myListener = listener;
    }

    protected void fireEvent(Event ev) {
        if (m_myListener != null && ev != null) {
            m_myListener.eventAction(ev, getName());
        }
    }

    public void addState(IState state, EvStPair ev_st_pairs[]) {

        state.setEventListener(m_listenerView);
        m_states.put(state.getName(), state);
        String stateName = state.getName();
        for (int n = 0; n < ev_st_pairs.length; n++) {
            addTransition(stateName, ev_st_pairs[n]);
        }
    }

    public void addStateSingleTrans(IState state, EvStPair ev_st_pair) {
        state.setEventListener(m_listenerView);
        m_states.put(state.getName(), state);
        addTransition(state.getName(), ev_st_pair);
    }

    public void addAnyStateTransition(EvStPair ev_st_pair) {
        String transName = ANY_NAME + NAMES_SEP + ev_st_pair.getEvent();
        m_transitions.put(transName, ev_st_pair.getState());
    }

    private void addTransition(String stateName, EvStPair ev_st_pair) {
        String transName = stateName + NAMES_SEP + ev_st_pair.getEvent();
        m_transitions.put(transName, ev_st_pair.getState());
    }

    private void _eventAction(Event ev, String senderStateName) {

        // check if the state already exists
        IState sender = (IState) m_states.get(senderStateName);
        if (sender == null)
            throw new NullPointerException("Sender state doesn't exist: '"+senderStateName+"'");

        // check if the event is a FATAL_ERROR
        if(Event.EV_ID_FATAL_ERROR.equals(ev.getID())) {
            fireEvent(ev);
            return;
        }
        
        // check if it is a FinalState to finish the flow or activate it
        if (sender instanceof FinalFlowState) {
            fireEvent(ev);
        } else {
            IState st = searchNextState(senderStateName, ev.getID());
            if (st != null) {
                activateNextState(st);
            } else {
                throw new NullPointerException("Next state not found: ev='" + ev.getID() + "', senderName='" + senderStateName + "'");
            }
        }

    }

    private void activateNextState(IState state) {
        if (state == null)
            throw new NullPointerException("Initial state cannot be null in flow '" + m_name + "'");
        m_currentState = state;
        m_currentState.activate();
        stateChanged(state);
    }

    protected void stateChanged(IState state) {
    }

    private IState searchNextState(String stateName, String evID) {

        String transName, nextStateName;

        // search for that specific pair
        transName = stateName + NAMES_SEP + evID;
        nextStateName = (String) m_transitions.get(transName);

        // if not found try with "ANY" event
        if (nextStateName == null) {
            transName = stateName + NAMES_SEP + ANY_NAME;
            nextStateName = (String) m_transitions.get(transName);
        }

        // if not found try with "ANY" state
        if (nextStateName == null) {
            transName = ANY_NAME + NAMES_SEP + evID;
            nextStateName = (String) m_transitions.get(transName);
        }

        // if not found try with "ANY" event and state
        if (nextStateName == null) {
            transName = ANY_NAME + NAMES_SEP + ANY_NAME;
            nextStateName = (String) m_transitions.get(transName);
        }

        if (nextStateName != null)
            return (IState) m_states.get(nextStateName);
        else
            return null;
    }

}
