/**
 * 
 */
package com.jzb.blt.serial;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.jzb.blt.Constants;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.flow.FormState;
import com.jzb.j2me.util.GlobalContext;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class SendDataState extends FormState implements CommandListener, SPortManager.ISendListener {

    private static final Command s_cmdCancel = new Command("Cancel", Command.CANCEL, 1);
    
    private Gauge                m_gauge;
    private StringItem           m_text;
    private int                  m_currentValue;
    private int                  m_maxValue;

    /**
     * 
     */
    public SendDataState() {
        this(null, null, null);
    }

    /**
     * @param owner
     * @param name
     * @param title
     */
    public SendDataState(Flow owner, String name, String title) {
        super(owner, name, title);
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (s_cmdCancel.equals(cmd)) {
            _getSportManager().cancel();
        }
    }

    /**
     * @see com.jzb.blt.serial.SPortManager.IReceiveListener#done(boolean)
     */
    public void done(boolean wasCanceled) {
        if (!wasCanceled)
            _fireEvent(Event.EV_OK);
        else {
            _fireEvent(Event.EV_CANCEL);
        }
    }


    /**
     * @see com.jzb.blt.serial.SPortManager.IReceiveListener#setFileName(java.lang.String)
     */
    public void setFileName(String name) {
        m_text.setText(name);
        setTotalDataSize(0);
    }

    /**
     * @see com.jzb.blt.serial.SPortManager.IReceiveListener#setTotalDataSize(int)
     */
    public void setTotalDataSize(int size) {
        m_maxValue = size;
        m_currentValue = 0;
        m_gauge.setValue(0);
        Thread.yield();
    }

    /**
     * @see com.jzb.blt.serial.SPortManager.IReceiveListener#signalException(java.lang.Throwable)
     */
    public void signalException(Throwable th) {
        _fireEvent(Event.EV_EXCEPTION, "Error sending data: " + Utils.getExceptionMsg(th));
    }


    public void updateSentAmount(int value) {
        int cGP = (100 * value) / m_maxValue;
        if (cGP > m_currentValue) {
            m_currentValue = cGP;
            m_gauge.setValue(m_currentValue);
        }
        Thread.yield();
    }

    /**
     * @see com.jzb.flow.FormState#_innerAfterPainting()
     */
    protected void _innerAfterPainting() {
        SPortManager spm = _getSportManager();
        spm.AsyncSendData(this);
    }

    /**
     * @see com.jzb.flow.FormState#createItems()
     */
    protected void createItems() {
        m_text = new StringItem("File Name:", "", Item.PLAIN);
        m_text.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        append(m_text);

        this.m_gauge = new Gauge("Total sent:", false, 100, 0);
        m_gauge.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_AFTER);
        append(m_gauge);

        addCommand(s_cmdCancel);
        setCommandListener(this);
    }

    /**
     * @see com.jzb.flow.FormState#innerActivate(com.jzb.flow.Event)
     */
    protected void innerActivate(Event ev) {
        m_currentValue = m_maxValue = 0;
        m_gauge.setValue(0);
        m_text.setText("");

        callAfterPainting();
    }

    private SPortManager _getSportManager() {
        SPortManager spm = (SPortManager) GlobalContext.getData(Constants.GC_SPORT_DATA);
        return spm;
    }
}
