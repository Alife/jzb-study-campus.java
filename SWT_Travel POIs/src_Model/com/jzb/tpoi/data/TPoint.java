/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.jzb.tpoi.data.NMCollection.LinkedColl;

/**
 * @author n63636
 * 
 */
public class TPoint extends TMapFigure {

    public static final String      DEFAULT_GMAP_ICON_URL = "http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png";

    @LinkedColl(name = "points")
    private NMCollection<TCategory> m_categories          = new NMCollection<TCategory>(this);

    private TCoordinates            m_coordinates;

    private static long             s_idCounter           = Math.round(100L * Math.random());

    // ---------------------------------------------------------------------------------
    public String refreshKmlBlob() throws Exception {

        StringBuffer kmlStr = new StringBuffer();

        kmlStr.append("<Placemark><name>");
        kmlStr.append(getName());
        kmlStr.append("</name><description>");
        kmlStr.append(getDescription());
        kmlStr.append("</description>");
        if (getIcon() != null) {
            s_idCounter++;
            String styleID = "Style-" + System.currentTimeMillis() + "-" + s_idCounter;
            kmlStr.append("<Style id=\"").append(styleID).append("\"><IconStyle><Icon><href>");
            kmlStr.append(getIcon().getUrl());
            kmlStr.append("</href></Icon></IconStyle></Style>");
        } else {
            kmlStr.append("<Style/>");
        }
        kmlStr.append("<Point><coordinates>");
        if (getCoordinates() != null) {
            kmlStr.append(getCoordinates().toString());
        } else {
            kmlStr.append("0.0, 0.0, 0.0");
        }
        kmlStr.append("</coordinates></Point></Placemark>");

        return kmlStr.toString();
    }

    // ---------------------------------------------------------------------------------
    public TPoint(TMap ownerMap) {
        super(EntityType.Point, ownerMap);
        m_coordinates = new TCoordinates();
        setIcon(TIcon.createFromURL(DEFAULT_GMAP_ICON_URL));
        updateChanged(false);
    }

    // ---------------------------------------------------------------------------------
    // Copia los datos y las categorias
    @Override
    public void assignFrom(TBaseEntity other) {

        super.assignFrom(other);
        TPoint casted_other = (TPoint) other;

        m_coordinates = casted_other.m_coordinates;

        // Si la categoria en "other" no existe la crea desde la otra
        m_categories.clear();
        TMap myMap = getOwnerMap();
        for (TCategory cat : casted_other.m_categories) {
            TCategory myCat = myMap.getCategories().getById(cat.getId());
            if (myCat == null) {
                myCat = new TCategory(myMap);
                myCat.assignFrom(cat);
                myMap.getCategories().add(myCat);
            }
            m_categories.add(myCat);
        }
    }

    // ---------------------------------------------------------------------------------
    public String calcShortID() {

        String shortID;
        int p1 = getId().lastIndexOf('/');
        if (p1 > 0) {
            shortID = getId().substring(p1 + 1);
        } else {
            shortID = getId();
        }
        return shortID;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the categories
     */
    public NMCollection<TCategory> getCategories() {
        return m_categories;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the coordinates
     */
    public TCoordinates getCoordinates() {
        return m_coordinates;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TMapFigure#readExternal(java.io.ObjectInput)
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        m_coordinates = (TCoordinates) in.readObject();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param coordinates
     *            the coordinates to set
     */
    public void setCoordinates(TCoordinates coordinates) {
        m_coordinates = coordinates;
        touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TMapFigure#writeExternal(java.io.ObjectOutput)
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(m_coordinates);
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void xmlStringBody(StringBuffer sb, String ident) {
        super.xmlStringBody(sb, ident);
        sb.append(ident).append("<coordinates>").append(m_coordinates != null ? m_coordinates : "").append("</coordinates>\n");

        if (m_categories.size() <= 0) {
            sb.append(ident).append("<categories/>\n");
        } else {
            sb.append(ident).append("<categories>");
            boolean first = true;
            for (TCategory cat : m_categories) {
                if (!first)
                    sb.append(", ");
                sb.append(cat.getName());
                first = false;
            }
            sb.append("</categories>\n");
        }
    }

    // ---------------------------------------------------------------------------------
    protected void _updateKmlBlob(Document doc, XPath xpath) throws Exception {

        Node node = (Node) xpath.evaluate("/Placemark/name/text()", doc, XPathConstants.NODE);
        if (node != null)
            node.setNodeValue(getName());

        node = (Node) xpath.evaluate("/Placemark/description/text()", doc, XPathConstants.NODE);
        if (node != null)
            node.setNodeValue(getDescription());

        node = (Node) xpath.evaluate("/Placemark/Style/IconStyle/Icon/href/text()", doc, XPathConstants.NODE);
        if (node != null) {
            if (getIcon() != null) {
                node.setNodeValue(getIcon().getUrl());
            }
        }

        node = (Node) xpath.evaluate("/Placemark/Point/coordinates/text()", doc, XPathConstants.NODE);
        if (node != null)
            node.setNodeValue(getCoordinates().toString());

    }

    // ---------------------------------------------------------------------------------
    @Override
    protected void _parseFromKmlBlob(Document doc, XPath xpath) throws Exception {

        String val = xpath.evaluate("/Placemark/name/text()", doc);
        setName(val);

        val = xpath.evaluate("/Placemark/description/text()", doc);
        setDescription(_cleanHTML(val));

        val = xpath.evaluate("/Placemark/Style/IconStyle/Icon/href/text()", doc);
        if (val == null || val.length() == 0) {
            setIcon(TIcon.createFromURL(DEFAULT_GMAP_ICON_URL));
        } else {
            setIcon(TIcon.createFromURL(val));
        }

        val = xpath.evaluate("/Placemark/Point/coordinates/text()", doc);
        if (val != null && val.length() > 0) {
            m_coordinates = new TCoordinates(val);
        }
    }

}
