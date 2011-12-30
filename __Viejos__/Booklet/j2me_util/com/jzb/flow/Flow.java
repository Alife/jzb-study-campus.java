/**
 * 
 */
package com.jzb.flow;

/**
 * @author PS00A501
 * 
 */
public interface Flow extends State {

    public static final String ANY_STATE_NAME = "*";

    public void eventFired(Event ev);

    public Object getData(String id);

    public State getState(String name);

    public void setData(String id, Object data);
}
