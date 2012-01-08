/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author n63636
 * 
 */
public class ExtendedInfo {

    public static final String   EIP_COORDINATES     = "-101.804811,40.736959,0.0";
    public static final String   EIP_ICON_URL        = "http://maps.gstatic.com/mapfiles/ms2/micons/earthquake.png";
    public static final String   EXT_INFO_POINT_NAME = "@EXT_INFO";

    private ArrayList<TCategory> m_categories        = new ArrayList<TCategory>();
    private TIcon                m_mapIcon;
    private String               m_mapShortName;

    // ---------------------------------------------------------------------------------
    public static TPoint createEmptyExtInfoPoint(TMap map) {

        TPoint point = new TPoint(map);
        point.setName(EXT_INFO_POINT_NAME);
        point.setDescription("");
        point.setIcon(TIcon.createFromURL(EIP_ICON_URL));
        point.setCoordinates(new TCoordinates(EIP_COORDINATES));

        String xml = "<extended_Info>\r\n" + "\r\n" + "    <map>\r\n" + "        <shortName></shortName>\r\n" + "        <icon>pepepepe</icon>\r\n" + "    </map>\r\n" + "\r\n"
                + "    <categories>\r\n" + "        <category>\r\n" + "            <id>01010101001</id>\r\n" + "            <name>categoria1</name>\r\n"
                + "            <shortName>cat1</shortName>\r\n" + "            <description>desc_categoria1</description>\r\n" + "            <created_ts>2012-01-04T21:00:00.000Z</created_ts>\r\n"
                + "            <updated_ts>2012-01-04T21:00:00.000Z</updated_ts>\r\n" + "            <icon>icon_categoria1</icon>\r\n"
                + "            <pointsInfo>0004b5a7a25e1191a242e@test , 908423409238409238409@pepe,48980948092380498234@luis</pointsInfo>\r\n" + "        </category>\r\n" + "    </categories>\r\n"
                + "\r\n" + "</extended_Info>";

        point.setDescription(xml);

        return point;

    }

    // ---------------------------------------------------------------------------------
    public static boolean isExtInfoPoint(TMapFigure mapFigure) {
        return (mapFigure instanceof TPoint) && mapFigure.getName().equals(EXT_INFO_POINT_NAME);
    }

    // ---------------------------------------------------------------------------------
    public static ExtendedInfo parseFromXml(TMap map) throws Exception {

        ExtendedInfo extInfo = new ExtendedInfo();

        TPoint eiPoint = map.__getExtInfoPoint();
        String xml = eiPoint.getDescription();
        if (!xml.contains("extended_Info")) {
            return extInfo;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        String val = xpath.evaluate("/extended_Info/map/shortName/text()", doc);
        extInfo.setMapShortName(val);

        val = xpath.evaluate("/extended_Info/map/icon/text()", doc);
        extInfo.setMapIcon(TIcon.createFromURL(val));

        NodeList nlist = (NodeList) xpath.evaluate("/extended_Info/categories/category", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {

            Node node = nlist.item(n);

            TCategory cat = new TCategory(map);

            val = xpath.evaluate("id/text()", node);
            cat.updateId(val);

            val = xpath.evaluate("name/text()", node);
            cat.setName(val);

            val = xpath.evaluate("shortName/text()", node);
            cat.setShortName(val);

            val = xpath.evaluate("description/text()", node);
            cat.setDescription(val);

            val = xpath.evaluate("created_ts/text()", node);
            cat.setTS_Created(TDateTime.parseDateTime(val));

            val = xpath.evaluate("updated_ts/text()", node);
            cat.setTS_Updated(TDateTime.parseDateTime(val));

            val = xpath.evaluate("icon/text()", node);
            cat.setIcon(TIcon.createFromURL(val));

            extInfo.addCategory(cat);
        }

        return extInfo;
    }

    // ---------------------------------------------------------------------------------
    public void addCategory(TCategory cat) {
        m_categories.add(cat);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the categories
     */
    public ArrayList<TCategory> getCategories() {
        return m_categories;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the mapIcon
     */
    public TIcon getMapIcon() {
        return m_mapIcon;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the mapShortName
     */
    public String getMapShortName() {
        return m_mapShortName;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param categories
     *            the categories to set
     */
    public void setCategories(ArrayList<TCategory> categories) {
        m_categories = categories;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param mapIcon
     *            the mapIcon to set
     */
    public void setMapIcon(TIcon mapIcon) {
        m_mapIcon = mapIcon;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param mapShortName
     *            the mapShortName to set
     */
    public void setMapShortName(String mapShortName) {
        m_mapShortName = mapShortName;
    }

}
