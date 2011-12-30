/**
 * Rigel Services Model Infrastructure, Version 1.0
 *
 * Copyright (C) 2002 ISBAN.
 * All Rights Reserved.
 *
 **/

package com.jzb.readers;

import java.io.File;
import java.util.ArrayList;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.model.EntityRegistry;

/**
 * @author PS00A501
 *
 */
public class Scenario_Loader extends Impl_Loader {

    public Scenario_Loader() {
    }
    
    public Scenario_Loader(File compFile) throws Exception {
        super(compFile);
    }
    
    /**
     * @see com.jzb.readers.Impl_Loader#acceptByContent(java.lang.String)
     */
    public boolean acceptByContent(String sample) {
        return sample.indexOf("<scenario ") >= 0;
    }

    /**
     * @see com.jzb.readers.Impl_Loader#createLoader(java.io.File)
     */
    public Impl_Loader createLoader(File compFile) throws Exception {
        return new Scenario_Loader(compFile);
    }

    /**
     * @see com.jzb.readers.Impl_Loader#parseDOM(org.w3c.dom.Document, java.util.ArrayList)
     */
    protected void parseDOM(Document doc, ArrayList list) throws Exception {
        
        
        //String scenarioName=m_xp.evaluate("/scenario/@name",doc);
        
        NodeList nl_als = (NodeList) m_xp.evaluate("/scenario/applicationsReferenceList/applicationReference", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl_als.getLength(); n++) {
            
            String alName=m_xp.evaluate("@name", nl_als.item(n));
            
            NodeList nl_ops = (NodeList) m_xp.evaluate("operationsReferenceList/operationReference", nl_als.item(n), XPathConstants.NODESET);
            for (int i = 0; i < nl_ops.getLength(); i++) {
            
                String opName=m_xp.evaluate("@name", nl_ops.item(i));
                EntityRegistry.getInstance().addScenarioRef(alName+"."+opName);
            }
        }
    }

}
