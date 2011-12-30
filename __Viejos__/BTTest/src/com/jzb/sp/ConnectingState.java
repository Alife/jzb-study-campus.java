package com.jzb.sp;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;

import com.jzb.flow.Event;
import com.jzb.flow.EventCommand;
import com.jzb.flow.FormState;

public class ConnectingState extends FormState {

    private Gauge m_gauge;

    public ConnectingState(String stateName, String title) {
        super(stateName, title);
    }

    protected void createItems() {

        this.m_gauge = new Gauge("\n\nWaiting for connection...", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
        m_gauge.setLayout(Item.LAYOUT_CENTER);
        append(m_gauge);

        addCommand(new EventCommand(Event.EV_ID_CANCEL, "Cancel", Command.CANCEL, 1));
    }

    public void moveGauge() {
        int n = m_gauge.getValue() + 1;
        if (n >= m_gauge.getMaxValue()) {
            n = 0;
        }
        m_gauge.setValue(n);
    }

    public void connectionEnded() {
        fireEvent(new Event(Event.EV_ID_OK));
    }
}
