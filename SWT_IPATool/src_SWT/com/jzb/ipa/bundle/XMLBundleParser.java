/**
 * 
 */
package com.jzb.ipa.bundle;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author n000013
 * 
 */
public class XMLBundleParser implements IBundleDataParser {

    public BundleData parse(byte[] buffer) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(new ByteArrayInputStream(buffer));

        BundleData bdata = new BundleData();

        bdata.name = _getKeyValue(doc, "CFBundleDisplayName");
        if (bdata.name == null || bdata.name.equals(""))
            bdata.name = _getKeyValue(doc, "CFBundleName");
        bdata.version = _getKeyValue(doc, "CFBundleVersion");
        bdata.minOSVersion = _getKeyValue(doc, "MinimumOSVersion");
        bdata.pkgID = _getKeyValue(doc, "CFBundleIdentifier");

        return bdata;
    }

    private String _getKeyValue(Node root, String keyName) throws Exception {

        XPath xpath = XPathFactory.newInstance().newXPath();
        Node node = (Node) xpath.evaluate(".//key[text()='" + keyName + "']", root, XPathConstants.NODE);
        if (node == null)
            return "";

        node = node.getNextSibling();
        while (node != null && node.getNodeType() != 1) {
            node = node.getNextSibling();
        }

        if (node != null && node.getNodeName().equals("string")) {
            return node.getFirstChild().getNodeValue();
        } else {
            return "";
        }
    }
}
