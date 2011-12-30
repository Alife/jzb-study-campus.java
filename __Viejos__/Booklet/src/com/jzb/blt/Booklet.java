/**
 * 
 */
package com.jzb.blt;

import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.flow.NullFlow;
import com.jzb.flow.XMLFlow;
import com.jzb.j2me.util.BaseMidlet;

/**
 * @author PS00A501
 * 
 */
public class Booklet extends BaseMidlet {

    private Flow m_mainFlow;

    /**
     * 
     */
    public Booklet() {
        super();
    }

    /**
     * @see com.jzb.j2me.util.BaseMidlet#init()
     */
    protected void init() throws Exception {
        Flow parent = new NullFlow() {

            public void eventFired(Event ev) {
                destroyApp(true);
            }
        };

        m_mainFlow = XMLFlow.parse(parent, "MainFlow", "MainFlow");
    }

    /**
     * @see com.jzb.j2me.util.BaseMidlet#start()
     */
    protected void start() throws Exception {
        m_mainFlow.activate(null);
    }
}
