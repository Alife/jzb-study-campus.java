/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author n63636
 * 
 */
public abstract class TMapFigure extends TMapElement {

    private String                     m_kmlBlob;
    private HashMap<String, TCategory> m_onwerCats = new HashMap<String, TCategory>();

    // ---------------------------------------------------------------------------------
    protected TMapFigure(EntityType type, TMap ownerMap) {
        super(type, ownerMap);
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
    public void setFromKmlBlob(String kmlBlob) throws Exception {

        setKmlBlob(kmlBlob);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(kmlBlob)));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        _parseFromKmlBlob(doc, xpath);

        touchAsUpdated();

    }

    // ---------------------------------------------------------------------------------
    /**
     * @param kmlBlob
     *            the kmlBlob to set
     */
    public void setKmlBlob(String kmlBlob) {
        m_kmlBlob = kmlBlob;
        touchAsUpdated();
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
        sb.append(ident).append("<kmlBlob><![CDATA[").append(m_kmlBlob != null ? m_kmlBlob : "").append("]]></kmlBlob>\n");
    }

    // ---------------------------------------------------------------------------------
    Collection<TCategory> _getOwnerCategories() {
        return m_onwerCats.values();
    }

    // ---------------------------------------------------------------------------------
    boolean _isOwnerCategory(TCategory cat) {
        if (cat != null)
            return m_onwerCats.containsKey(cat.getId());
        else
            return false;
    }

    // ---------------------------------------------------------------------------------
    TCategory _removeFromCategory(TCategory cat) {
        return m_onwerCats.remove(cat.getId());
    }

    // ---------------------------------------------------------------------------------
    void addToCategory(TCategory cat) {
        m_onwerCats.put(cat.getId(), cat);
    }

    // ---------------------------------------------------------------------------------
    protected void _parseFromKmlBlob(Document doc, XPath xpath) throws Exception {

        String val = xpath.evaluate("//name/text()", doc);
        setName(val);

        val = xpath.evaluate("//description/text()", doc);
        setDescription(val);
    }
}
