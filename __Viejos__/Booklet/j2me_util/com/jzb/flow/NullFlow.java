/**
 * 
 */
package com.jzb.flow;

import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
public abstract class NullFlow implements Flow {

    /**
     * 
     */
    public NullFlow() {
    }

    /**
     * @see com.jzb.flow.State#activate(com.jzb.flow.Event)
     */
    public void activate(Event ev) {
    }

    /**
     * @see com.jzb.flow.State#deactivate()
     */
    public void deactivate() {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.jzb.flow.Flow#eventFired(com.jzb.flow.Event)
     */
    public abstract void eventFired(Event ev);

    /**
     * @see com.jzb.flow.Flow#getData(java.lang.String)
     */
    public Object getData(String id) {
        return null;
    }

    /**
     * @see com.jzb.flow.State#getName()
     */
    public String getName() {
        return "NullFlow";
    }

    /**
     * @see com.jzb.flow.Flow#getState(java.lang.String)
     */
    public State getState(String name) {
        return null;
    }

    /**
     * @see com.jzb.flow.IXMLInitializable#init(com.jzb.flow.Flow, java.lang.String, com.jzb.j2me.util.Properties)
     */
    public void init(Flow owner, String name, Properties props) throws Exception {
    }

    /**
     * @see com.jzb.flow.Flow#setData(java.lang.String, java.lang.Object)
     */
    public void setData(String id, Object data) {
    }
}
