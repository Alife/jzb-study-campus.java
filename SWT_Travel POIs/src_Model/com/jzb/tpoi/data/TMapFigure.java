/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author n63636
 * 
 */
public abstract class TMapFigure extends TMapElement {

    private String m_kmlBlob;

    // ---------------------------------------------------------------------------------
    protected TMapFigure(EntityType type, TMap ownerMap) {
        super(type, ownerMap);
        m_kmlBlob = "<nothing/>";
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void mergeFrom(TBaseEntity other, boolean conflict) {

        super.mergeFrom(other, conflict);
        TMapFigure casted_other = (TMapFigure) other;

        m_kmlBlob = casted_other.m_kmlBlob;
    }

    // ---------------------------------------------------------------------------------
    public void assignFromKmlBlob(String kmlBlob) throws Exception {

        m_kmlBlob = kmlBlob != null ? kmlBlob : "<nothing/>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(kmlBlob)));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        _parseFromKmlBlob(doc, xpath);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the kmlBlob
     */
    public String getKmlBlob() {
        return m_kmlBlob;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#readExternal(java.io.ObjectInput)
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        m_kmlBlob = (String) in.readObject();
    }

    // ---------------------------------------------------------------------------------
    public String refreshKmlBlob() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(m_kmlBlob)));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        _updateKmlBlob(doc, xpath);

        // Regenera la informacion
        StringWriter sw = new StringWriter();

        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(sw);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(domSource, streamResult);

        m_kmlBlob = sw.getBuffer().toString();

        return m_kmlBlob;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#writeExternal(java.io.ObjectOutput)
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(m_kmlBlob);
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void xmlStringBody(StringBuffer sb, String ident) {
        super.xmlStringBody(sb, ident);
        sb.append(ident).append("<kmlBlob><![CDATA[").append(m_kmlBlob).append("]]></kmlBlob>\n");
    }

    // ---------------------------------------------------------------------------------
    protected String _cleanHTML(String val) {
        // Hay que limpiarlo????
        // &#39;
        // &ecirc;
        // &ntilde;
        // &iacute;
        val = val;
        return val;
    }

    // ---------------------------------------------------------------------------------
    protected void _parseFromKmlBlob(Document doc, XPath xpath) throws Exception {

        String val = xpath.evaluate("//name/text()", doc);
        setName(val);

        val = xpath.evaluate("//description/text()", doc);
        setDescription(_cleanHTML(val));
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param kmlBlob
     *            the kmlBlob to set
     */
    protected void _setKmlBlob(String kmlBlob) {
        m_kmlBlob = kmlBlob;
        // touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    protected void _updateKmlBlob(Document doc, XPath xpath) throws Exception {
    }
}
