/**
 * 
 */
package com.jzb.blt.mnu;

import com.jzb.blt.Constants;
import com.jzb.flow.BaseState;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.GlobalContext;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class LoadMenuState extends BaseState {

    /**
     * 
     */
    public LoadMenuState() {
        this(null, null);
    }

    /**
     * @param name
     */
    public LoadMenuState(Flow owner, String name) {
        super(owner, name);
    }

    /**
     * @see com.jzb.flow.BaseState#activate()
     */
    public void activate(Event ev) {
        try {
            MenuItem root = (MenuItem) GlobalContext.getData(Constants.GC_MENU_DATA);
            if (root == null) {
                root = MenuManager.loadTreeModel();
                GlobalContext.setData(Constants.GC_MENU_DATA, root);
            }
            _fireEvent(Event.EV_OK);
        } catch (Exception ex) {
            GlobalContext.setData(Constants.GC_MENU_DATA, MenuItem.createRoot());
            _fireEvent(Event.EV_EXCEPTION, "Error loading menu: " + Utils.getExceptionMsg(ex));
        }
    }

}
