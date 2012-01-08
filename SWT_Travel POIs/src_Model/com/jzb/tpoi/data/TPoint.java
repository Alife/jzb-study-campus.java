/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.xml.xpath.XPath;

import org.w3c.dom.Document;

/**
 * @author n63636
 * 
 */
public class TPoint extends TMapFigure {

    public static final String DEFAULT_GMAP_ICON_URL = "http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png";

    private TCoordinates       m_coordinates;

    // ---------------------------------------------------------------------------------
    public TPoint(TMap ownerMap) {
        super(EntityType.Point, ownerMap);
        m_coordinates = new TCoordinates();
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
    }

    // ---------------------------------------------------------------------------------
    @Override
    protected void _parseFromKmlBlob(Document doc, XPath xpath) throws Exception {

        String val = xpath.evaluate("/Placemark/name/text()", doc);
        setName(val);

        val = xpath.evaluate("/Placemark/description/text()", doc);
        setDescription(val);

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
