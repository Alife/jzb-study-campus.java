/**
 * 
 */
package com.jzb.flow;

import com.jzb.j2me.util.MidletDisplay;
import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
public class FinalState extends BaseState {

    private String m_evName;

    /**
     * 
     */
    public FinalState() {
        this(null, null, null);
    }

    /**
     * 
     */
    public FinalState(Flow owner, String name, String evName) {
        super(owner, name);
        m_evName = evName;
    }

    /**
     * @see com.jzb.flow.State#activate()
     */
    public void activate(Event ev) {
        MidletDisplay.setCurrent(null);
        _fireEvent(m_evName, ev.getData());
    }

    /**
     * @see com.jzb.flow.BaseState#readProps(com.jzb.j2me.util.Properties)
     */
    protected void readProps(Properties props) throws Exception {
        m_evName = props.getProperty("event");
        if (m_evName == null) {
            throw new Exception("Property 'event' not set for FinalState");
        }
    }
}
