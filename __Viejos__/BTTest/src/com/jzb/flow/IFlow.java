package com.jzb.flow;

public interface IFlow extends IState {

    public static final String ANY_NAME           = "*";

    public static final String FINAL_STATE_OK     = "FINAL_OK";
    public static final String FINAL_STATE_CANCEL = "FINAL_CANCEL";
    public static final String FINAL_STATE_ERROR  = "FINAL_ERROR";

    public static final String EV_ID_FLOW_OK      = "EV_ID_FLOW_OK";
    public static final String EV_ID_FLOW_CANCEL  = "EV_ID_FLOW_CANCEL";
    public static final String EV_ID_FLOW_ERROR   = "EV_ID_FLOW_ERROR";

    public static class EvStPair {

        private String               m_event;
        private String               m_state;

        public static final EvStPair NULL_EV_ST_PAIR = new EvStPair("¬NoEvent¬", "¬NoState¬");

        public static EvStPair get(String ev, String st) {
            return new EvStPair(ev, st);
        }

        public EvStPair(String ev, String st) {
            m_event = ev;
            m_state = st;
        }

        public String getEvent() {
            return m_event;
        }

        public String getState() {
            return m_state;
        }
    }

    public void addStateSingleTrans(IState state, EvStPair ev_st_pair);

    public void addState(IState state, EvStPair ev_st_pairs[]);

    public void addAnyStateTransition(EvStPair ev_st_pair);

}
