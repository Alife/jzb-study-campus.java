package com.jzb.flow.test;

import com.jzb.flow.Event;
import com.jzb.flow.FinalFlowState;
import com.jzb.flow.FlowBase;
import com.jzb.sp.SPortManager;

public class PepeFlow extends FlowBase {

    public PepeFlow() {
        super("PepeFlow");
    }

    protected void createStates() {
        addStateSingleTrans(new PepeAlert1State("PepeTest1_State"), EvStPair.get(Event.EV_ID_OK, "SUBFLOW_STATE"));
        addStateSingleTrans(new SPortManager("SUBFLOW_STATE"), EvStPair.get(EV_ID_FLOW_OK, "PepeTest2_State"));
        addStateSingleTrans(new PepeAlert2State("PepeTest2_State"), EvStPair.get(Event.EV_ID_OK, FINAL_STATE_OK));
        addStateSingleTrans(new FinalFlowState(FINAL_STATE_OK, Event.EV_ID_OK), EvStPair.NULL_EV_ST_PAIR);
    }

    protected String getInitialStateName() {
        return "PepeTest1_State";
    }

}
