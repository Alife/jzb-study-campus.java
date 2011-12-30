/**
 * 
 */
package com.jzb.flow;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import com.jzb.j2me.util.MidletDisplay;
import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
public abstract class CanvasState extends Canvas implements State, CommandListener {

    private String m_name;
    private Flow   m_owner;

    /**
     * 
     */
    public CanvasState() {
        this(null, null);
    }

    /**
     * 
     */
    public CanvasState(Flow owner, String name) {
        super();
        m_owner = owner;
        m_name = name;
        setCommandListener(this);
    }

    /**
     * @see com.jzb.flow.State#activate()
     */
    public void activate(Event ev) {
        innerActivate(ev);
        MidletDisplay.setCurrent(this);
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (cmd instanceof EnumCommand) {
            EnumCommand ec = (EnumCommand) cmd;
            String ev = ec.getEvent();
            if (ev != null)
                _fireEvent(ev, getCmdEventData(ec));
        }
    }

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

    protected Object getCmdEventData(EnumCommand ec) {
        return null;
    }

    protected abstract void innerActivate(Event ev);

    /**
     * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
     */
    protected abstract void paint(Graphics arg0);

    protected void readProps(Properties props) throws Exception {
    }
}
