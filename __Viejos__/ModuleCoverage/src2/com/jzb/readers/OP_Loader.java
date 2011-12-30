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
public class OP_Loader extends Impl_Loader {

    protected OP_Loader() {
        super();
    }

    public OP_Loader(File compFile) throws Exception {
        super(compFile);
    }

    public boolean acceptByContent(String sample) {
        return sample.indexOf("<presentationOperation") >= 0;
    }

    public Impl_Loader createLoader(File compFile) throws Exception {
        return new OP_Loader(compFile);
    }

    protected void parseDOM(Document doc, ArrayList list) throws Exception {

        String op_name = m_xp.evaluate("/presentationOperation/@name", doc);

        BOImplementation this_OP = new BOImplementation(getALName(), op_name, "OP_init", "OP_init#1");
        
        this_OP.addReferencedBO_GID_list(getRefBOList(doc));
        this_OP.addUsedBOI_GID_list(getRefBOI_ID_List_SUBOPS(doc));
        
        list.add(this_OP);
    }

    private String getALName() {
        String path = getCompFile().getParentFile().getAbsolutePath();
        int n = path.indexOf("Applications");
        return path.substring(n + 13);
    }

    private ArrayList getRefBOList(Document doc) throws Exception {

        ArrayList list = new ArrayList();

        NodeList nl = (NodeList) m_xp.evaluate("/presentationOperation/stateList/FacadeComponentState", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            String facade = m_xp.evaluate("@facade", nl.item(n));
            String facadeComponent = m_xp.evaluate("@facadeComponent", nl.item(n));
            String methodID = m_xp.evaluate("@methodID", nl.item(n));
            list.add(BusinessOperation.newInstance(facade , facadeComponent , "??", methodID));
        }

        return list;
    }

    private ArrayList getRefBOI_ID_List_SUBOPS(Document doc) throws Exception {

        ArrayList list = new ArrayList();

        NodeList nl = (NodeList) m_xp.evaluate("/presentationOperation/stateList/OPState", doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            String alName=m_xp.evaluate("@referenceApplication", nl.item(n));
            String oiName=m_xp.evaluate("@presentationOperation", nl.item(n));
            list.add(BOImplementation.calcID(alName, oiName, "OP_init", "OP_init#1"));
        }

        return list;
    }
}