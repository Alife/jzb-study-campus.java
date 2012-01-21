/**
 * 
 */
package com.jzb.tpoi.wnd;

import com.jzb.tpoi.data.TBaseEntity;

/**
 * @author n63636
 * 
 */
public interface IMapPanelOwner {

    public void navigateBackward();

    public void navigateForward(TBaseEntity entity);
}
