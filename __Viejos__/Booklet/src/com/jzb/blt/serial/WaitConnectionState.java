/**
 * 
 */
package com.jzb.blt.serial;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;

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
public class WaitConnectionState extends FormState implements CommandListener {

    private class InnerRunner implements SPortManager.IWaitConnetion {

        /**
         * 
         */
        public InnerRunner() {
        }

        /**
         * @see com.jzb.blt.serial.SPortManager.IWaitConnetion#done()
         */
        public void done(boolean wasCanceled) {
            if (!wasCanceled)
                _fireEvent(Event.EV_OK);
            else
                _fireEvent(Event.EV_CANCEL);
        }

        /**
         * @see com.jzb.blt.serial.SPortManager.IWaitConnetion#signalException(java.lang.Throwable)
         */
        public void signalException(Throwable th) {
            _fireEvent(Event.EV_EXCEPTION, "Error connecting serial port: " + Utils.getExceptionMsg(th));
        }

    }

    private static final Command s_cmdCancel = new Command("Cancel", Command.CANCEL, 1);

    /**
     * 
     */
    public WaitConnectionState() {
        this(null, null, null);
    }

    /**
     * 
     */
    public WaitConnectionState(Flow owner, String name, String title) {
        super(owner, name, title);
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (s_cmdCancel.equals(cmd)) {
            // IT DOESN'T WAIN UNTIL CONNECTION TIMEOUT
            _getSportManager().cancel();
            _fireEvent(Event.EV_CANCEL);
        }
    }

    /**
     * @see com.jzb.flow.FormState#_innerAfterPainting()
     */
    protected void _innerAfterPainting() {
        SPortManager spm = _getSportManager();
        spm.AsyncConnectSPort(new InnerRunner());
    }

    /**
     * @see com.jzb.flow.FormState#createItems()
     */
    protected void createItems() {
        Gauge gauge = new Gauge("\n\nWaiting for connection...", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
        gauge.setLayout(Item.LAYOUT_CENTER);
        append(gauge);
        addCommand(s_cmdCancel);
        setCommandListener(this);
    }

    /**
     * @see com.jzb.flow.FormState#innerActivate(com.jzb.flow.Event)
     */
    protected void innerActivate(Event ev) {
        callAfterPainting();
    }

    private SPortManager _getSportManager() {
        SPortManager spm = (SPortManager) GlobalContext.getData(Constants.GC_SPORT_DATA);
        if (spm == null) {
            spm = new SPortManager();
            GlobalContext.setData(Constants.GC_SPORT_DATA, spm);
        }
        return spm;
    }

}
