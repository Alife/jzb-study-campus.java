/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.StringReader;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class ExtendedInfo {

    /*--------------------------------------------------------------------------------------
     * <extended_Info> 
     *     <map>
     *         <shortName/>
     *         <iconName/>
     *     </map>
     *     <categories>
     *         <category>
     *             <id/>
     *             <name/>
     *             <shortName/>
     *             <ETag/>
     *             <description/>
     *             <created_ts/>
     *             <updated_ts/>
     *             <iconName/>
     *         </category
     *         <categorizedLinks>
     *             <catLink> 
     *                 <catId/> 
     *                 <pointIds>point_id1, point_id2,...</pointIds>
     *                 <subCatsIds>cat_id1, cat_id2,...</subCatsIds>
     *             </catLink> 
     *         </categorizedLinks>
     *     </categories>
     * </extended_Info>
     --------------------------------------------------------------------------------------*/

    public static final String EIP_DEF_COORDINATES = "-101.804811,40.736959,0.0";
    public static final String EIP_DEF_ICON_URL    = "http://maps.gstatic.com/mapfiles/ms2/micons/earthquake.png";
    public static final String EIP_NAME            = "@EXT_INFO";

    // ---------------------------------------------------------------------------------
    public static TPoint createEmptyExtInfoPoint() {

        TPoint point = new TPoint(null);
        point.setName(EIP_NAME);
        point.setDescription("");
        point.setIcon(TIcon.createFromURL(EIP_DEF_ICON_URL));
        point.setCoordinates(new TCoordinates(EIP_DEF_COORDINATES));
        String xml = "<extended_Info/>";
        point.setDescription(xml);

        return point;

    }

    // ---------------------------------------------------------------------------------
    public static boolean isExtInfoPoint(TMapFigure mapFigure) {
        return (mapFigure instanceof TPoint) && mapFigure.getName().equals(EIP_NAME);
    }

    // ---------------------------------------------------------------------------------
    // AQUI LOS PUNTOS DEL MAPA YA DEBEN ESTAR LEIDOS
    public static void parseExtInfoFromXml(TMap map) throws Exception {

        if (map.getExtInfoPoint() == null) {
            return;
        }

        String xml = map.getExtInfoPoint().getDescription();
        if (!xml.contains("extended_Info")) {
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        String val = xpath.evaluate("/extended_Info/map/shortName/text()", doc);
        if (val != null && val.length() > 0)
            map.setShortName(val);

        val = xpath.evaluate("/extended_Info/map/iconName/text()", doc);
        if (val != null && val.length() > 0)
            map.setIcon(TIcon.createFromName(val));

        NodeList nlist = (NodeList) xpath.evaluate("/extended_Info/categories/category", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {

            Node node = nlist.item(n);

            TCategory cat = new TCategory(map);

            val = xpath.evaluate("id/text()", node);
            cat.updateId(val);

            val = xpath.evaluate("name/text()", node);
            cat.setName(val);

            val = xpath.evaluate("shortName/text()", node);
            if (val != null && val.length() > 0)
                cat.setShortName(val);

            val = xpath.evaluate("ETag/text()", node);
            cat.updateSyncETag(val);

            val = xpath.evaluate("description/text()", node);
            cat.setDescription(val);

            val = xpath.evaluate("created_ts/text()", node);
            cat.setTS_Created(TDateTime.parseDateTime(val));

            val = xpath.evaluate("updated_ts/text()", node);
            cat.setTS_Updated(TDateTime.parseDateTime(val));

            val = xpath.evaluate("iconName/text()", node);
            if (val != null && val.length() > 0)
                cat.setIcon(TIcon.createFromName(val));

            map.getCategories().add(cat);

        }

        // Enlaza las categorias con sus puntos y subcategorias
        nlist = (NodeList) xpath.evaluate("/extended_Info/categorizedLinks/catLink", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {

            Node node = nlist.item(n);

            val = xpath.evaluate("catId/text()", node);
            TCategory cat = map.getCategories().getById(val);
            if (cat == null) {
                Tracer._warn("Container category not found: " + val);
                continue;
            }

            val = xpath.evaluate("pointIds/text()", node);
            StringTokenizer st = new StringTokenizer(val, ",");
            while (st.hasMoreTokens()) {
                String pointID = st.nextToken().trim();
                TPoint point = map.getPoints().getById(pointID);
                if (point != null) {
                    cat.getPoints().add(point);
                } else {
                    Tracer._warn("Categorized point not found: " + pointID);
                }
            }

            val = xpath.evaluate("subCatsIds/text()", node);
            st = new StringTokenizer(val, ",");
            while (st.hasMoreTokens()) {
                String catID = st.nextToken().trim();
                TCategory subCat = map.getCategories().getById(catID);
                if (subCat != null) {
                    cat.getCategories().add(subCat);
                } else {
                    Tracer._warn("Categorized subcategory not found: " + catID);
                }
            }
        }

    }

    // ---------------------------------------------------------------------------------
    public ExtendedInfo() {
    }
}
