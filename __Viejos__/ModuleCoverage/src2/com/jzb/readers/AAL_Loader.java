/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.readers;

import java.io.File;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.jzb.model.BOImplementation;


/**
 * @author PS00A501
 */
public class AAL_Loader extends Base_CompL_Loader {

    protected AAL_Loader() {
        super();
    }

    public AAL_Loader(File compFile) throws Exception {
        super(compFile);
    }

    protected String getNamePrefix() {
        return "AAL_";
    }

    protected String getSampleContent() {
        return "<appAdapterComponent";
    }
    
    public Impl_Loader createLoader(File compFile) throws Exception {
        return new AAL_Loader(compFile);
    }


    protected void parseMethod(BOImplementation this_Comp, Document doc, Node methodNode) throws Exception {

        String type = m_xp.evaluate("methodImplementation/@type", methodNode);
        if (type.equals("delegateImplementation")) {
            String targetField = m_xp.evaluate("methodImplementation/@targetField", methodNode);
            Node targetFieldNode = (Node) m_xp.evaluate("/appAdapterComponent/componentFieldList/componentCallField[@name='" + targetField + "']", doc, XPathConstants.NODE);
            String calledComponentName = m_xp.evaluate("@calledComponentName", targetFieldNode);
            String calledComponentPackage = m_xp.evaluate("@calledComponentPackage", targetFieldNode);
            String calledMethodID =m_xp.evaluate("methodImplementation/@targetAccess", methodNode);
            this_Comp.addUsedBOI_GID(BOImplementation.calcID(calledComponentPackage, calledComponentName, "??", calledMethodID));
        } else if (type.equals("mapperImplementation")) {
            String targetField = m_xp.evaluate("methodImplementation/@targetField", methodNode);
            Node targetFieldNode = (Node) m_xp.evaluate("/appAdapterComponent/componentFieldList/operationCallField[@name='" + targetField + "']", doc, XPathConstants.NODE);
            String OI_AL = m_xp.evaluate("@calledApplication", targetFieldNode);
            String OI_name = m_xp.evaluate("@calledOeperation", targetFieldNode);
            this_Comp.addUsedBOI_GID(BOImplementation.calcID(OI_AL, OI_name, "OI_init", "OI_init#1"));
        } else {
            System.out.println("UN AAL QUE LLAMA A ALGO DESCONOCIDO ----> " + type);
        }
    }

    
}
