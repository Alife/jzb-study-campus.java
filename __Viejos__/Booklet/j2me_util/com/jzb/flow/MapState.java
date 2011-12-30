/**
 * 
 */
package com.jzb.flow;

/**
 * @author PS00A501
 * 
 */
public abstract class MapState extends BaseState {

    /**
     * 
     */
    public MapState() {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public MapState(Flow owner, String name) {
        super(owner, name);
    }

    /**
     * @see com.jzb.flow.BaseState#activate(com.jzb.flow.Event)
     */
    public void activate(Event ev) {
        _fireEvent(Event.EV_OK, doMap(ev));
    }

    /**
     * @see com.jzb.flow.State#deactivate()
     */
    public void deactivate() {
    }

    public abstract Object doMap(Event ev);

}
