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
public class Facade_Loader extends Impl_Loader {

    public Facade_Loader() {
    }
    
    public Facade_Loader(File compFile) throws Exception {
        super(compFile);
    }
    
    /**
     * @see com.jzb.readers.Impl_Loader#acceptByContent(java.lang.String)
     */
    public boolean acceptByContent(String sample) {
        return sample.indexOf("<facade ") >= 0;
    }

    /**
     * @see com.jzb.readers.Impl_Loader#createLoader(java.io.File)
     */
    public Impl_Loader createLoader(File compFile) throws Exception {
        return new Facade_Loader(compFile);
    }

    /**
     * @see com.jzb.readers.Impl_Loader#parseDOM(org.w3c.dom.Document, java.util.ArrayList)
     */
    protected void parseDOM(Document doc, ArrayList list) throws Exception {
        
        
        String facadeName=m_xp.evaluate("/facade/@name",doc);
        
        NodeList nl = (NodeList) m_xp.evaluate("/*/facadeComponentReferenceList/facadeComponentReference", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            
            String packageName=m_xp.evaluate("@package", nl.item(n));
            String compName = m_xp.evaluate("@name", nl.item(n));
            EntityRegistry.getInstance().addFacadeRef(facadeName, packageName+"."+compName);
        }
    }

}
