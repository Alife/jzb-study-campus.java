/**
 * 
 */
package com.jzb.blt.main;

import com.jzb.blt.StoreManager;
import com.jzb.flow.BaseState;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class ClearStorage extends BaseState {

    /**
     * 
     */
    public ClearStorage() {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public ClearStorage(Flow owner, String name) {
        super(owner, name);
    }

    /**
     * @see com.jzb.flow.BaseState#activate(com.jzb.flow.Event)
     */
    public void activate(Event ev) {
        try {
            StoreManager.deleteStorages(true);
            _fireEvent(Event.EV_OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error clearing storage:" + Utils.getExceptionMsg(ex));
        }
    }

}
