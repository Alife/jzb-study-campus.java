/**
 * 
 */
package com.jzb.flow;

import java.util.Vector;

import com.jzb.j2me.util.Prop;
import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
public class XMLFlow extends FlowBase {

    private static final FinalState s_DefFinalState = new FinalState(null, "DefFinalSt", Event.EV_OK);

    public static XMLFlow parse(Flow owner, String name, String xmlName) throws Exception {
        XMLFlow flow = new XMLFlow(owner, name);
        XMLFlowParser.parseFlow(flow, xmlName);
        return flow;
    }

    public XMLFlow() throws Exception {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     * @throws Exception
     */
    public XMLFlow(Flow owner, String name) throws Exception {
        super(owner, name);
    }

    public void init(Flow owner, String name, Properties props) throws Exception {
        super.init(owner, name, props);
        String xmlName = Prop.getMdtryProp(props, "file");
        XMLFlowParser.parseFlow(this, xmlName);
    }

    void initFlow(Vector states, Vector trans) throws Exception {
        for (int n = 0; n < states.size(); n++) {
            addState((State) states.elementAt(n));
        }

        for (int n = 0; n < trans.size(); n++) {
            String v[] = (String[]) trans.elementAt(n);
            addTransition(v[0], v[1], v[2]);
        }
    }

    /**
     * @see com.jzb.flow.FlowBase#createStates()
     */
    protected void createStates() throws Exception {
    }

    /**
     * @see com.jzb.flow.FlowBase#getInitialState()
     */
    protected State getInitialState() {
        return s_DefFinalState;
    }

}
