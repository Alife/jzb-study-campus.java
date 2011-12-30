/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.readers;

import java.io.File;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author PS00A501
 */
public class DependentProjectsReader {

    private boolean m_findFacades;

    private boolean m_findImplementations;

    public DependentProjectsReader(boolean findFacades, boolean findImplementations) {
        m_findFacades = findFacades;
        m_findImplementations = findImplementations;
    }

    public HashSet getPrjSet(File rootPrj) throws Exception {
        HashSet prjSet = new HashSet();
        HashSet visited = new HashSet();
        parseXmlPath(rootPrj, prjSet, visited);
        return prjSet;
    }

    private void parseXmlPath(File rootPrj, HashSet prjSet, HashSet visited) throws Exception {

        if (visited.contains(rootPrj.getAbsolutePath()))
            return;

        visited.add(rootPrj.getAbsolutePath());

        File xmlPathFile = new File(rootPrj, ".xmlpath");
        if (!xmlPathFile.exists())
            return;

        if (!hasVegaNature(rootPrj))
            return;

        if(hasDesiredVegaNature(rootPrj))
            prjSet.add(rootPrj);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlPathFile);
        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList nodelist = (NodeList) xp.evaluate("/xmlpath/xmlpathEntries/xmlpathentry/@path", document, XPathConstants.NODESET);
        for (int n = 0; n < nodelist.getLength(); n++) {
            String prjPath = nodelist.item(n).getNodeValue();
            if (prjPath.startsWith("/") || prjPath.startsWith("\\"))
                prjPath = prjPath.substring(1);
            if (!prjSet.contains(prjPath)) {
                parseXmlPath(new File(rootPrj.getParent(), prjPath), prjSet, visited);
            }
        }
    }

    private boolean hasVegaNature(File rootPrj) throws Exception {

        File xmlProjectFile = new File(rootPrj, ".project");
        if (!xmlProjectFile.exists())
            return false;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlProjectFile);
        XPath xp = XPathFactory.newInstance().newXPath();
        if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.facadeNature']", document).length() > 0)
            return true;
        if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.blBlockNature']", document).length() > 0)
            return true;
        if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.laproject.laProjectNature']", document).length() > 0)
            return true;
        if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.plBlockNature']", document).length() > 0)
            return true;
        if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.assembly.assemblyNature']", document).length() > 0)
            return true;
        if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.globalsNature']", document).length() > 0)
            return true;
        return false;
    }

    private boolean hasDesiredVegaNature(File rootPrj) throws Exception {
        File xmlProjectFile = new File(rootPrj, ".project");
        if (!xmlProjectFile.exists())
            return false;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlProjectFile);
        XPath xp = XPathFactory.newInstance().newXPath();
        if (m_findFacades) {
            if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.facadeNature']", document).length() > 0)
                return true;
        }
        if (m_findImplementations) {
            if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.blBlockNature']", document).length() > 0)
                return true;
            if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.laproject.laProjectNature']", document).length() > 0)
                return true;
            if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.plBlockNature']", document).length() > 0)
                return true;
            if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.assembly.assemblyNature']", document).length() > 0)
                return true;
            if (xp.evaluate("/projectDescription/natures/nature[text()='com.isb.vega.model.component.globalsNature']", document).length() > 0)
                return true;
            
        }
        return false;
    }
}
