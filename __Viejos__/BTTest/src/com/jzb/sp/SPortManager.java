package com.jzb.sp;

import java.io.InputStream;

import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;

import com.jzb.flow.Event;
import com.jzb.flow.FinalFlowState;
import com.jzb.flow.FlowBase;
import com.jzb.flow.IState;

public class SPortManager extends FlowBase {

    private static final String SN_CONNECT_CABLE = "SN_CONNECT_CABLE";
    private static final String SN_CONNECTING    = "SN_CONNECTING";
    private static final String SN_RECEIVE       = "SN_RECEIVE";

    private ConnectingState     m_connectingState;
    private ReceiveState        m_receiveState;

    private CommConnection      m_commCon;

    private boolean             m_connecting;

    private byte                m_buffer[];

    public SPortManager(String name) {
        super(name);
    }

    protected void createStates() {

        IState connectState = new ConnectCableState(SN_CONNECT_CABLE);
        m_connectingState = new ConnectingState(SN_CONNECTING, "Connecting");
        m_receiveState = new ReceiveState(SN_RECEIVE, "Receiving data");

        addStateSingleTrans(connectState, EvStPair.get(Event.EV_ID_OK, SN_CONNECTING));
        addStateSingleTrans(m_connectingState, EvStPair.get(Event.EV_ID_OK, SN_RECEIVE));
        addStateSingleTrans(m_receiveState, EvStPair.get(Event.EV_ID_OK, FINAL_STATE_OK));

        // from any state it goes to cancel state
        addAnyStateTransition(new EvStPair(Event.EV_ID_CANCEL, FINAL_STATE_CANCEL));

        // End states OK and CANCEL
        addStateSingleTrans(new FinalFlowState(FINAL_STATE_OK, Event.EV_ID_OK), EvStPair.NULL_EV_ST_PAIR);
        addStateSingleTrans(new FinalFlowState(FINAL_STATE_CANCEL, Event.EV_ID_CANCEL), EvStPair.NULL_EV_ST_PAIR);
    }

    protected String getInitialStateName() {
        return SN_CONNECT_CABLE;
    }

    public byte[] getReceivedData() {
        byte[] b=m_buffer;
        m_buffer=null;
        return b;
    }
    
    protected void stateChanged(IState state) {
        if (SN_CONNECTING.equals(state.getName())) {
            new Thread(new Runnable() {
                public void run() {
                    _connectSPort();
                }
            }).start();
        } else if (SN_RECEIVE.equals(state.getName())) {
            new Thread(new Runnable() {
                public void run() {
                    _receiveData();
                }
            }).start();
        }
    }

    private String getPort() {
        String ports = System.getProperty("microedition.commports");
        String port;
        int comma = ports.indexOf(',');
        if (comma > 0) {
            // Parse the first port from the available ports list.
            port = ports.substring(0, comma);
        } else {
            // Only one serial port available.
            port = ports;
        }
        return port;
    }

    private void _animateConnectingGauge() {
        new Thread(new Runnable() {
            public void run() {
                while (m_connecting) {
                    m_connectingState.moveGauge();
                    Thread.yield();
                }
            }
        }).start();
    }

    private void _connectSPort() {

        try {
            m_connecting = true;
            // _animateConnectingGauge();
            m_commCon = (CommConnection) Connector.open("comm:" + getPort(), Connector.READ_WRITE, true);
            // Thread.sleep(2000);
            m_connecting = false;
            m_connectingState.connectionEnded();
        } catch (Exception e) {
            m_connectingState.signalException(e);
        }

    }

    private void _receiveData() {
        try {
            InputStream is = m_commCon.openInputStream();
            //is=new KKIS();

            int size = is.read();
            m_receiveState.setTotalDataSize(size);

            m_buffer = new byte[size];

            for (int totalRead = 0; totalRead < size; totalRead++) {
                m_buffer[totalRead] = (byte) is.read();
                m_receiveState.updateReceivedAmount(totalRead);
            }

            is.close();

            m_receiveState.receptionEnded();

        } catch (Exception e) {
            m_connectingState.signalException(e);
        }

    }
}
