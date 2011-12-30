/**
 * 
 */
package com.jzb.flow;


import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
public interface IXMLInitializable {

    public void init(Flow owner, String name, Properties props) throws Exception;
}
