package com.jzb.sp;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.jzb.flow.Event;
import com.jzb.flow.EventCommand;
import com.jzb.flow.FormState;

public class ReceiveState extends FormState {

    private Gauge      m_gauge;
    private StringItem m_text;

    public ReceiveState(String stateName, String title) {
        super(stateName, title);
    }

    protected void createItems() {

        this.m_gauge = new Gauge("\n\nTotal received:", false, 100, 0);
        m_gauge.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_AFTER);
        append(m_gauge);

        m_text = new StringItem("Text:", "", StringItem.PLAIN);
        m_text.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        append(m_text);

        addCommand(new EventCommand(Event.EV_ID_CANCEL, "Cancel", Command.CANCEL, 1));
    }

    public void moveGauge() {
        int n = m_gauge.getValue() + 1;
        if (n >= m_gauge.getMaxValue()) {
            n = 0;
        }
        m_gauge.setValue(n);
    }

    public void setTotalDataSize(int size) {
        m_gauge.setMaxValue(size);
    }

    public void updateReceivedAmount(int value) {
        m_gauge.setValue(value);
    }

    public void receptionEnded() {
        fireEvent(new Event(Event.EV_ID_OK));
    }

}
