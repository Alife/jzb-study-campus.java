/**
 * 
 */
package com.jzb.flow;


/**
 * @author PS00A501
 * 
 */
public interface State extends IXMLInitializable {

    public void activate(Event ev);

    public void deactivate();

    public String getName();

}
