/**
 * 
 */
package com.jzb.blt.main;

import com.jzb.blt.Constants;
import com.jzb.flow.BaseState;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.GlobalContext;

/**
 * @author PS00A501
 * 
 */
public class ResetMenuState extends BaseState {

    /**
     * 
     */
    public ResetMenuState() {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public ResetMenuState(Flow owner, String name) {
        super(owner, name);
    }

    /**
     * @see com.jzb.flow.BaseState#activate(com.jzb.flow.Event)
     */
    public void activate(Event ev) {
        GlobalContext.setData(Constants.GC_MENU_DATA, null);
        _fireEvent(Event.EV_OK);
    }

}
