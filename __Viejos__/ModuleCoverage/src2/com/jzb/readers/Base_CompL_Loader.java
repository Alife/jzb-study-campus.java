/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.readers;

import java.io.File;
import java.util.ArrayList;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jzb.model.BOImplementation;
import com.jzb.model.BusinessOperation;


/**
 * @author PS00A501
 */
public abstract class Base_CompL_Loader extends Impl_Loader {

    protected Base_CompL_Loader() {
        super();
    }

    public Base_CompL_Loader(File compFile) throws Exception {
        super(compFile);
    }

    protected abstract String getNamePrefix();
    protected abstract String getSampleContent();
    
    public boolean acceptByContent(String sample) {
        return sample.indexOf(getSampleContent()) >= 0;
    }

    protected final void parseDOM(Document doc, ArrayList list) throws Exception {

        NodeList nl = (NodeList) m_xp.evaluate("/*/methodList/method", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            
            int p=getCompFile().getName().toLowerCase().indexOf(".xmlvb");
            String comp_name=getCompFile().getName().substring(0,p);
            String packageName = getCompFile().getParentFile().getName();
            String methodID = m_xp.evaluate("@gid", nl.item(n));
            BOImplementation this_Comp = new BOImplementation(packageName,comp_name,"??",methodID);
            try {
                parseMethod(this_Comp, doc, nl.item(n));
            }
            catch(Exception ex) {
                System.out.println(getCompFile());
                throw ex;
            }

            NodeList facades = (NodeList) m_xp.evaluate("/*/implsList/implementation", doc, XPathConstants.NODESET);
            if(facades.getLength()>1) {
                throw new Exception("varias fachadas");
            }
            if(facades.getLength()>0) {
                String BO_package=m_xp.evaluate("@package",facades.item(0));
                String BO_Interface=m_xp.evaluate("@name",facades.item(0));
                String BO_method=m_xp.evaluate("@name", nl.item(n));
                this_Comp.setImplementedBO(BusinessOperation.searchInstance(BO_package+"."+BO_Interface, BO_method));
            }
            
            list.add(this_Comp);
        }
        
        
    }

    protected abstract void parseMethod(BOImplementation this_Comp, Document doc, Node methodNode) throws Exception;
}
