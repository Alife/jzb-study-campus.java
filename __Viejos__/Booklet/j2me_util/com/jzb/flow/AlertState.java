/**
 * 
 */
package com.jzb.flow;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import com.jzb.j2me.util.MidletDisplay;
import com.jzb.j2me.util.Prop;
import com.jzb.j2me.util.Properties;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class AlertState extends Alert implements State, CommandListener {

    public static final String   DYNAMIC_TEXT_ID = "DynamicText";

    private static final Command s_cmdOK         = new Command("Ok", Command.OK, 1);
    private static final Command s_cmdCancel     = new Command("Cancel", Command.CANCEL, 1);

    private String               m_name;
    private String               m_text;
    private boolean              m_isDynamic;
    private Flow                 m_owner;

    private Object               m_lastEventData;

    public AlertState() {
        this(null, null, false, "", "", false);
    }

    public AlertState(Flow owner, String name, boolean hasCancel, String title, String text) {
        this(owner, name, hasCancel, title, text, false);
    }

    public AlertState(Flow owner, String name, boolean hasCancel, String title, String text, boolean isDynamic) {

        super(title, text, Utils.getImgInfo(), AlertType.INFO);

        m_owner = owner;
        m_name = name;
        m_text = text;
        m_isDynamic = isDynamic;
        
        setTimeout(Alert.FOREVER);
        addCommand(s_cmdOK);
        if (hasCancel)
            addCommand(s_cmdCancel);
        setCommandListener(this);
    }

    /**
     * @see com.jzb.flow.State#activate()
     */
    public void activate(Event ev) {

        m_lastEventData = ev.getData();

        if (m_isDynamic) {
            String dynText = (String) m_owner.getData(getName() + "." + DYNAMIC_TEXT_ID);
            if (dynText == null) {
                Object obj = ev.getData();
                if (obj instanceof String) {
                    dynText = (String) obj;
                }
            }
            if (dynText != null) {
                setString(m_text + dynText);
            } else {
                setString(m_text);
            }
        }

        setImage(Utils.getImgInfo());
        
        MidletDisplay.setCurrent(this);
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (m_owner != null) {
            if (cmd.equals(s_cmdOK)) {
                Event ev = new Event(Event.EV_OK, this, m_lastEventData);
                m_owner.eventFired(ev);
            } else {
                Event ev = new Event(Event.EV_CANCEL, this, m_lastEventData);
                m_owner.eventFired(ev);
            }
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

    /**
     * @see com.jzb.flow.IXMLInitializable#init(com.jzb.flow.Flow, java.lang.String, Properties)
     */
    public void init(Flow owner, String name, Properties props) throws Exception {
        m_owner = owner;
        m_name = name;
        setTitle(Prop.getMdtryProp(props, "title"));
        setString(props.getProperty("text"));
        setImage(Utils.getImgInfo());
        m_isDynamic = Prop.getBoolProp(props, "dynamic");
        if (Prop.getBoolProp(props, "hasCancel"))
            addCommand(s_cmdCancel);
    }

}
