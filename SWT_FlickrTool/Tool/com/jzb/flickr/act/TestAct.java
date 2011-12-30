/**
 * 
 */
package com.jzb.flickr.act;

import java.util.ArrayList;
import java.util.Random;

import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class TestAct extends BaseAction {

    private boolean m_canRetry;

    private boolean m_failed;

    private boolean m_isList;

    private Object  m_item;

    private String  m_name = "NO_NAMED";

    private int     m_numElements;

    public TestAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        m_tracer._debug("EXECUTING TESTING ACTION '" + m_name + "'" + (m_isList ? "AS LIST" : "AS NORMAL") + " ACTION");

        Thread.sleep(200 + new Random(System.currentTimeMillis()).nextInt(600));

        if (m_failed) {
            throw new Exception("SIMULATING AN ERROR FROM TESTING ACTION");
        }

        if (m_isList) {
            ArrayList<Object> list = new ArrayList<Object>();
            for (int n = 0; n < m_numElements; n++) {
                list.add(new Object());
            }
            return list;
        } else {
            return m_item;
        }
    }

    /**
     * @return the canRetry
     */
    public String getCanRetry() {
        return m_canRetry ? "true" : "false";
    }

    /**
     * @return the failed
     */
    public String getFailed() {
        return m_failed ? "true" : "false";
    }

    /**
     * @return the list
     */
    public String getIsList() {
        return m_isList ? "true" : "false";
    }

    /**
     * @return the item
     */
    public Object getItem() {
        return m_item;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return the numElements
     */
    public String getNumElements() {
        return Integer.toString(m_numElements);
    }

    /**
     * @param canRetry
     *            the canRetry to set
     */
    public void setCanRetry(String canRetry) {
        m_canRetry = Boolean.parseBoolean(canRetry);
        ;
    }

    /**
     * @param fail
     *            the fail to set
     */
    public void setFailed(String failed) {
        m_failed = Boolean.parseBoolean(failed);
    }

    /**
     * @param list
     *            the list to set
     */
    public void setIsList(String isList) {
        m_isList = Boolean.parseBoolean(isList);
    }

    /**
     * @param item
     *            the item to set
     */
    public void setItem(Object item) {
        m_item = item;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @param numElements
     *            the numElements to set
     */
    public void setNumElements(String numElements) {
        m_numElements = Integer.parseInt(numElements);
    }
}
