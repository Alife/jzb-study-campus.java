/**
 * 
 */
package com.jzb.blt.serial;

import com.jzb.blt.Constants;
import com.jzb.flow.Event;
import com.jzb.flow.FinalState;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.GlobalContext;

/**
 * @author PS00A501
 * 
 */
public class CloseConnectionState extends FinalState {

    /**
     * 
     */
    public CloseConnectionState() {
        this(null, null, null);
    }

    /**
     * @param owner
     * @param name
     * @param evName
     */
    public CloseConnectionState(Flow owner, String name, String evName) {
        super(owner, name, evName);
    }

    /**
     * @see com.jzb.flow.FinalState#activate(com.jzb.flow.Event)
     */
    public void activate(Event ev) {
        try {
            _getSportManager().disconnectSPort();
            super.activate(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, ex);
        }
    }

    private SPortManager _getSportManager() {
        SPortManager spm = (SPortManager) GlobalContext.getData(Constants.GC_SPORT_DATA);
        return spm;
    }

}
