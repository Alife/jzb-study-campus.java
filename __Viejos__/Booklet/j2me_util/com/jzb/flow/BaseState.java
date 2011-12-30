/**
 * 
 */
package com.jzb.flow;

import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
public abstract class BaseState implements State {

    private String m_name;
    private Flow   m_owner;

    /**
     * 
     */
    public BaseState() {
        this(null, null);
    }

    /**
     * 
     */
    public BaseState(Flow owner, String name) {
        m_owner = owner;
        m_name = name;
    }

    /**
     * @see com.jzb.flow.State#activate()
     */
    public abstract void activate(Event ev);

    /**
     * @see com.jzb.flow.State#deactivate()
     */
    public void deactivate() {
    }

    /**
     * @see com.jzb.flow.State#getName()
     */
    public String getName() {
        return m_name;
    }

    public Flow getOwner() {
        return m_owner;
    }

    /**
     * @see com.jzb.flow.IXMLInitializable#init(com.jzb.flow.Flow, java.lang.String, com.jzb.j2me.util.Properties)
     */
    public void init(Flow owner, String name, Properties props) throws Exception {
        m_owner = owner;
        m_name = name;
        readProps(props);
    }

    protected void _fireEvent(String evName) {
        _fireEvent(evName, null);
    }

    protected void _fireEvent(String evName, Object data) {
        if (m_owner != null) {
            Event ev = new Event(evName, this, data);
            m_owner.eventFired(ev);
        }
    }

    protected void readProps(Properties props) throws Exception {
    }
}
