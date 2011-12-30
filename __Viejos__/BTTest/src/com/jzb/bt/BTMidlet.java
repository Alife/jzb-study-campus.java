package com.jzb.bt;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.jzb.flow.Event;
import com.jzb.flow.IEventListener;
import com.jzb.j2me.util.MidletDisplay;
import com.jzb.sp.SPortManager;

public class BTMidlet extends MIDlet implements IEventListener {

    private SPortManager m_flow;

    public BTMidlet() {
        m_flow = new SPortManager("SPortManager");
        m_flow.setEventListener(this);
        MidletDisplay.set(this);
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        notifyDestroyed();
    }

    protected void pauseApp() {
    }

    protected void startApp() throws MIDletStateChangeException {
        m_flow.activate();
    }

    public void eventAction(Event ev, String senderName) {

        String text;

        if (Event.EV_ID_FATAL_ERROR.equals(ev.getID())) {
            Throwable th = (Throwable) ev.getData();
            th.printStackTrace();
            text = "Error [" + th.getClass() + "]: " + th.getMessage();
        } else if (Event.EV_ID_OK.equals(ev.getID())) {
            byte buffer[]=m_flow.getReceivedData();
            if(buffer!=null)
                text = "received: " + new String(buffer);
            else
                text = "received: <[* nothing *]>";
        } else {
            text = "Event = " + ev.getID();
        }

        Form frm = new Form("Application Finished!");
        frm.append(text);
        MidletDisplay.get().setCurrent(frm);
    }

    public void showTextMessage(String text) {
        Display disp = Display.getDisplay(this);
        Alert alert = new Alert("Message", text, null, AlertType.INFO);
        alert.setTimeout(Alert.FOREVER);
        disp.setCurrent(alert);
    }

    public void showErrorMessage(Throwable th) {
        String text = "Error [" + th.getClass() + "] Caught: " + th.getMessage();
        showErrorMessage(text);
    }

    public void showErrorMessage(String text) {
        Display disp = Display.getDisplay(this);
        Alert alert = new Alert("Message", text, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        disp.setCurrent(alert);
    }

}
