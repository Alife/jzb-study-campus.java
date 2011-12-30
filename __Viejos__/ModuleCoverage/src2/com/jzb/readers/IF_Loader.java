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

import com.jzb.model.BusinessOperation;

/**
 * @author PS00A501
 *
 */
public class IF_Loader extends Impl_Loader {

    public IF_Loader() {
    }
    
    public IF_Loader(File compFile) throws Exception {
        super(compFile);
    }
    
    /**
     * @see com.jzb.readers.Impl_Loader#acceptByContent(java.lang.String)
     */
    public boolean acceptByContent(String sample) {
        return sample.indexOf("<facadeComponent") >= 0;
    }

    /**
     * @see com.jzb.readers.Impl_Loader#createLoader(java.io.File)
     */
    public Impl_Loader createLoader(File compFile) throws Exception {
        return new IF_Loader(compFile);
    }

    /**
     * @see com.jzb.readers.Impl_Loader#parseDOM(org.w3c.dom.Document, java.util.ArrayList)
     */
    protected void parseDOM(Document doc, ArrayList list) throws Exception {
        
        NodeList nl = (NodeList) m_xp.evaluate("/*/methodList/method", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            
            int p=getCompFile().getName().toLowerCase().indexOf(".xmlvb");
            String compName=getCompFile().getName().substring(0,p);
            String packageName = getCompFile().getParentFile().getName();
            String methodName=m_xp.evaluate("@name", nl.item(n));
            String methodID = m_xp.evaluate("@gid", nl.item(n));
            BusinessOperation this_Comp = BusinessOperation.newInstance(null, packageName+"."+compName, methodName, methodID);
            list.add(this_Comp);
        }
    }

}
