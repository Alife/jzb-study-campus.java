/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.jzb.util.Base64;
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
     *         <iconURL/>
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
     *             <iconURL/>
     *         </category>
     *     </categories>
     *     <categorizedLinks>
     *         <catLink> 
     *             <catId/> 
     *             <pointIds>point_id1, point_id2,...</pointIds>
     *             <subCatsIds>cat_id1, cat_id2,...</subCatsIds>
     *         </catLink> 
     *     </categorizedLinks>
     * </extended_Info>
     --------------------------------------------------------------------------------------*/

    public static final String EIP_DEF_COORDINATES = "-101.804811,40.736959,0.0";
    public static final String EIP_NAME            = "@EXT_INFO";

    // ---------------------------------------------------------------------------------
    public static TPoint createEmptyExtInfoPoint(TMap map) {

        TPoint point = new TPoint(null);
        point.setName(EIP_NAME);
        point.setDescription("");
        point.setIcon(TIcon.createFromURL(TIcon.EIP_DEF_ICON_URL));
        point.setCoordinates(new TCoordinates(EIP_DEF_COORDINATES));
        String xml = "extended_Info=#";
        point.setDescription(xml);

        point.updateOwnerMap(map);

        return point;

    }

    // ---------------------------------------------------------------------------------
    public static boolean isExtInfoPoint(TMapFigure mapFigure) {
        return (mapFigure instanceof TPoint) && mapFigure.getName().equals(EIP_NAME);
    }

    // ---------------------------------------------------------------------------------
    // AQUI LOS PUNTOS DEL MAPA YA DEBEN ESTAR LEIDOS
    public static void parseMapExtInfo(TMap map) throws Exception {

        byte buffer[] = _decodeInfo(map.getExtInfoPoint().getDescription());
        if (buffer == null) {
            return;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bais);

        map.setShortName((String) ois.readObject());
        String iconSmalURL = (String) ois.readObject();
        map.setIcon(TIcon.createFromSmallURL(iconSmalURL));

        int numCats = ois.readInt();
        for (int n = 0; n < numCats; n++) {
            TCategory cat = new TCategory(map);
            cat.readExternal(ois);
            map.getCategories().add(cat);
        }

        for (int n = 0; n < numCats; n++) {

            String catID = (String) ois.readObject();
            TCategory cat = map.getCategories().getById(catID);

            int numPoints = ois.readInt();
            for (int x = 0; x < numPoints; x++) {
                String pointID = (String) ois.readObject();
                TPoint point = map.getPoints().getById(pointID);
                if (point != null) {
                    cat.getPoints().add(point);
                }
            }

            int numSubCats = ois.readInt();
            for (int x = 0; x < numSubCats; x++) {
                String subCatID = (String) ois.readObject();
                TCategory subCat = map.getCategories().getById(subCatID);
                if (subCat != null) {
                    cat.getSubCategories().add(subCat);
                }
            }
        }
        ois.close();
    }

    // ---------------------------------------------------------------------------------
    public static void updateExtInfoPoint(TMap map) throws Exception {

        TPoint eip = map.getExtInfoPoint();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(map.getShortName());
        oos.writeObject(map.getIcon().getSmallUrl());

        oos.writeInt(map.getCategories().size());
        for (TCategory cat : map.getCategories()) {
            cat.writeExternal(oos);
        }

        for (TCategory cat : map.getCategories()) {

            oos.writeObject(cat.getId());

            oos.writeInt(cat.getPoints().size());
            for (TPoint point : cat.getPoints()) {
                oos.writeObject(point.getId());
            }

            oos.writeInt(cat.getSubCategories().size());
            for (TCategory subCat : cat.getSubCategories()) {
                oos.writeObject(subCat.getId());
            }
        }
        oos.close();

        String xml = _encodeInfo(baos.toByteArray());

        eip.setDescription(xml);

    }

    // ---------------------------------------------------------------------------------
    private static byte[] _decodeInfo(String xml) {

        if (!xml.startsWith("extended_Info")) {
            return null;
        }

        int p1 = xml.indexOf(':');
        if (p1 < 0) {
            return null;
        }
        int p2 = xml.indexOf('#', p1);
        if (p2 < 0) {
            return null;
        }

        try {
            xml = xml.substring(p1 + 1, p2).trim();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Inflater unzip = new Inflater();
            unzip.setInput(Base64.decode(xml));
            byte uzbuf[] = new byte[2048];
            while (!unzip.finished()) {
                int len = unzip.inflate(uzbuf);
                baos.write(uzbuf, 0, len);
            }
            unzip.end();
            baos.close();

            return baos.toByteArray();

        } catch (Throwable th) {
            Tracer._error("Error decoding EIP: ", th);
            return null;
        }
    }

    // ---------------------------------------------------------------------------------
    private static String _encodeInfo(byte info[]) {

        StringBuffer xml = new StringBuffer("extended_Info:");

        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte uzbuf[] = new byte[2048];
            Deflater zip = new Deflater(9);
            zip.setInput(info);
            zip.finish();
            for (;;) {
                int len = zip.deflate(uzbuf);
                if (len > 0)
                    baos.write(uzbuf, 0, len);
                else
                    break;
            }
            zip.end();
            baos.close();

            xml.append(Base64.encode(baos.toByteArray()));

        } catch (Throwable th) {
            Tracer._error("Error encoding EIP: ", th);
        }

        xml.append('#');
        return xml.toString();
    }

    // ---------------------------------------------------------------------------------
    public ExtendedInfo() {
    }
}
