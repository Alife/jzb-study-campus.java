/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.readers;

import java.io.File;
import java.util.ArrayList;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.model.BOImplementation;
import com.jzb.model.BusinessOperation;

/**
 * @author PS00A501
 */
public class OI_Loader extends Impl_Loader {

    protected OI_Loader() {
        super();
    }

    public OI_Loader(File compFile) throws Exception {
        super(compFile);
    }

    public boolean acceptByContent(String sample) {
        return sample.indexOf("<internalOperation") >= 0;
    }

    public Impl_Loader createLoader(File compFile) throws Exception {
        return new OI_Loader(compFile);
    }

    protected void parseDOM(Document doc, ArrayList list) throws Exception {

        String oi_name = m_xp.evaluate("/internalOperation/@name", doc);

        BOImplementation this_OI = new BOImplementation(getALName(), oi_name, "OI_init", "OI_init#1");
        
        this_OI.addReferencedBO_GID_list(getRefBOList(doc));
        this_OI.addUsedBOI_GID_list(getRefBOI_ID_List_COMPONENTS(doc));
        this_OI.addUsedBOI_GID_list(getRefBOI_ID_List_SUBOIS(doc));

        list.add(this_OI);
    }

    private String getALName() {
        String path = getCompFile().getParentFile().getAbsolutePath();
        int n = path.indexOf("Applications");
        return path.substring(n + 13);
    }

    private ArrayList getRefBOList(Document doc) throws Exception {

        ArrayList list = new ArrayList();

        NodeList nl = (NodeList) m_xp.evaluate("/internalOperation/stateList/FacadeComponentState", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            String facade = m_xp.evaluate("@facade", nl.item(n));
            String facadeComponent = m_xp.evaluate("@facadeComponent", nl.item(n));
            String methodID = m_xp.evaluate("@methodID", nl.item(n));
            list.add(BusinessOperation.newInstance(facade , facadeComponent , "??", methodID));
        }

        return list;
    }

    private ArrayList getRefBOI_ID_List_COMPONENTS(Document doc) throws Exception {

        ArrayList list = new ArrayList();

        NodeList nl = (NodeList) m_xp.evaluate("/internalOperation/stateList/ComponentState", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            String package_n = m_xp.evaluate("@package", nl.item(n));
            String componentName = m_xp.evaluate("@componentName", nl.item(n));
            String methodID = m_xp.evaluate("@methodID", nl.item(n));
            list.add(BOImplementation.calcID(package_n, componentName, "??", methodID));
        }

        return list;
    }

    private ArrayList getRefBOI_ID_List_SUBOIS(Document doc) throws Exception {

        ArrayList list = new ArrayList();

        NodeList nl = (NodeList) m_xp.evaluate("/internalOperation/stateList/OIState", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            String alName=m_xp.evaluate("@referenceApplication", nl.item(n));
            String oiName=m_xp.evaluate("@internalOperation", nl.item(n));
            list.add(BOImplementation.calcID(alName, oiName, "OI_init", "OI_init#1"));
        }

        return list;
    }
}