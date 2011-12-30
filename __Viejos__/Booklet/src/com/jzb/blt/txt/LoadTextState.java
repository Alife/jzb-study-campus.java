/**
 * 
 */
package com.jzb.blt.txt;

import com.jzb.blt.Constants;
import com.jzb.blt.mnu.MenuItem;
import com.jzb.flow.BaseState;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.GlobalContext;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class LoadTextState extends BaseState {

    /**
     * 
     */
    public LoadTextState() {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public LoadTextState(Flow owner, String name) {
        super(owner, name);
    }

    /**
     * @see com.jzb.flow.BaseState#activate(com.jzb.flow.Event)
     */
    public void activate(Event ev) {
        try {
            MenuItem mi = (MenuItem) ev.getData();
            TextData td = TextManager.loadTextData(mi.getAlias(), mi.getFileRecordIndex());
            GlobalContext.setData(Constants.GC_TEXT_DATA, td);
            _fireEvent(Event.EV_OK);
        } catch (Exception ex) {
            GlobalContext.setData(Constants.GC_TEXT_DATA, null);
            _fireEvent(Event.EV_EXCEPTION, Utils.getExceptionMsg(ex));
        }
    }

}
