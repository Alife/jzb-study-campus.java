/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public abstract class XMLBase_Loader implements ILoader {

    protected DocumentBuilder m_builder;
    protected XPath           m_xp;
    private XPathExpression   m_xp_CompName;

    public XMLBase_Loader() {
    }

    public void load(File prjFolder, File resource) throws Exception {
        load(null, prjFolder, resource);
    }

    public void load(Object param, File prjFolder, File resource) throws Exception {

        Tracer._debug("Parsing VegaElement: " + resource);

        if (m_builder == null)
            _initXMLParser();
        m_builder.reset();
        Document document = m_builder.parse(resource);
        parseDOM(param, document, prjFolder, resource);
    }

    protected String _getFileName(File f) {
        if (f == null)
            return "*unknown*";

        int p1 = f.getName().lastIndexOf('.');
        if (p1 > 0)
            return f.getName().substring(0, p1);
        else
            return f.getName();
    }

    protected String _getVegaElementName(Document doc, File prjFolder, File resource) {

        // TODO: ¿cuál es el nombre bueno el del fichero o el del XML?

        String s1 = _getFileName(resource);
        String s2;
        try {
            s2 = m_xp_CompName.evaluate(doc.getDocumentElement());
        } catch (Exception ex) {
            Tracer._error("Evaluating Element Name XPathExpression", ex);
            s2 = null;
        }
        if (s2 != null && s1.equals(s2)) {
            return s1;
        } else {
            Tracer._debug("Element's name in File and XMLNode name don't match: '" + s1 + "' - '" + s2 + "', file:" + resource);
            return s1;
        }
    }

    protected abstract void _subinitXML() throws Exception;

    protected abstract void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception;

    private void _initXMLParser() throws Exception {

        m_xp = XPathFactory.newInstance().newXPath();

        m_xp_CompName = m_xp.compile("./@name");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        m_builder = factory.newDocumentBuilder();

        _subinitXML();
    }

}
