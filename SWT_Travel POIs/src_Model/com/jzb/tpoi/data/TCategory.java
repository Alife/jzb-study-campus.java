/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author n63636
 * 
 */
public class TCategory extends TMapElement {

    private HashMap<String, TCategory> m_categories = new HashMap<String, TCategory>();
    // Solo para temas de cuenta de elementos en pantalla. No se almacena ni inicializa
    private int                        m_displayCount;

    private HashMap<String, TPoint>    m_points     = new HashMap<String, TPoint>();

    // ---------------------------------------------------------------------------------
    public static String _calcMapElementFullID(TMap ownerMap, String shortID) {

        if (TBaseEntity._isLocalId(shortID)) {
            return shortID;
        } else {
            String mapID = ownerMap.getId();
            int p1 = mapID.indexOf("/features/");
            if (p1 < 0) {
                return shortID;
            } else {
                int p2 = mapID.indexOf('/', p1 + 10);
                int p3 = mapID.indexOf('/', p2 + 1);
                String userID = mapID.substring(p1 + 10, p2);
                String smapID = mapID.substring(p2 + 1, p3);
                return "http://maps.google.com/maps/feeds/features/" + userID + "/" + smapID + "/full/" + shortID;
            }
        }
    }

    // ---------------------------------------------------------------------------------
    public static String _calcMapElementShortID(TMapElement element) {

        String elementID = element.getId();
        if (TBaseEntity._isLocalId(elementID)) {
            return elementID;
        } else {
            int p1 = elementID.indexOf("/full/");
            if (p1 < 0) {
                return elementID;
            } else {
                return elementID.substring(p1 + 6);
            }
        }
    }

    // ---------------------------------------------------------------------------------
    public TCategory(TMap ownerMap) {
        super(EntityType.Category, ownerMap);
    }

    // ---------------------------------------------------------------------------------
    public void addCategories(Collection<TCategory> cats) {
        for (TCategory cat : cats) {
            addCategory(cat);
        }
    }

    // ---------------------------------------------------------------------------------
    public void addCategory(TCategory cat) {

        if (cat == null)
            return;

        // IMPORTANTE PARA NO CREAR CLICLOS. LO DEJAMOS AL INTERFAZ GRAFICO
        // if (!cat.containsCategoryById(this.getId(), true))
        {
            TCategory oldCat = m_categories.put(cat.getId(), cat);
            if (!cat.equals(oldCat)) {
                touchAsUpdated();
            }
        }

    }

    // ---------------------------------------------------------------------------------
    public void addPoint(TPoint point) {

        if (point == null)
            return;

        TPoint oldPoint = m_points.put(point.getId(), point);
        if (!point.equals(oldPoint)) {
            touchAsUpdated();
        }
    }

    // ---------------------------------------------------------------------------------
    public void addPoints(Collection<TPoint> points) {

        for (TPoint p : points) {
            addPoint(p);
        }
    }

    // ---------------------------------------------------------------------------------
    public void clearAll() {
        m_categories.clear();
        m_points.clear();
    }

    // ---------------------------------------------------------------------------------
    public boolean containsCategoryById(String categoryID) {
        return m_categories.containsKey(categoryID);
    }

    // ---------------------------------------------------------------------------------
    public boolean containsCategoryById(String categoryID, boolean recursive) {

        if (m_categories.containsKey(categoryID)) {
            return true;
        }

        if (!recursive) {
            return false;
        } else {
            for (TCategory cat : m_categories.values()) {
                if (cat.containsCategoryById(categoryID, true)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    public boolean containsPointById(String pointID) {
        return m_points.containsKey(pointID);
    }

    // ---------------------------------------------------------------------------------
    public boolean containsPointById(String pointID, boolean recursive) {

        if (m_points.containsKey(pointID)) {
            return true;
        }

        if (!recursive) {
            return false;
        } else {
            for (TCategory cat : m_categories.values()) {
                if (cat.containsPointById(pointID, true)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    public void deleteCategory(TCategory cat) {

        if (m_categories.remove(cat.getId()) != null) {
            touchAsUpdated();
        }
    }

    // ---------------------------------------------------------------------------------
    public void deletePoint(TPoint point) {
        if (m_points.remove(point.getId()) != null) {
            touchAsUpdated();
        }
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the categories
     */
    public Collection<TCategory> getAllCategories() {

        return m_categories.values();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the points
     */
    public Collection<TPoint> getAllPoints() {

        return m_points.values();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the points
     */
    public Collection<TPoint> getAllRecursivePoints() {

        HashSet<TPoint> allPoints = new HashSet<TPoint>(m_points.values());
        for (TCategory cat : m_categories.values()) {
            allPoints.addAll(cat.getAllRecursivePoints());
        }
        return allPoints;
    }

    // ---------------------------------------------------------------------------------
    public TCategory getCategoryById(String categoryID) {
        return m_categories.get(categoryID);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the displayCount
     */
    public int getDisplayCount() {
        return m_displayCount;
    }

    // ---------------------------------------------------------------------------------
    public TPoint getPointById(String pointID) {
        return m_points.get(pointID);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return increments the display count
     */
    public void incrementDisplayCount() {
        m_displayCount++;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#readExternal(java.io.ObjectInput)
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        super.readExternal(in);

        TMap ownerMap = getOwnerMap();

        int len1 = in.readInt();
        for (int n = 0; n < len1; n++) {
            String pointID = (String) in.readObject();
            TPoint point = ownerMap.getPointById(pointID);
            if (point != null) {
                m_points.put(pointID, point);
            }
        }

        int len2 = in.readInt();
        for (int n = 0; n < len2; n++) {
            String categoryID = (String) in.readObject();
            TCategory category = ownerMap.getCategoryById(categoryID);
            if (category != null) {
                m_categories.put(categoryID, category);
            }
        }

    }

    // ---------------------------------------------------------------------------------
    /**
     * @param displayCount
     *            the displayCount to set
     */
    public void setDisplayCount(int displayCount) {
        m_displayCount = displayCount;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#writeExternal(java.io.ObjectOutput)
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        super.writeExternal(out);

        out.writeInt(m_points.size());
        for (TPoint mf : m_points.values()) {
            out.writeObject(mf.getId());
        }

        out.writeInt(m_categories.size());
        for (TCategory cat : m_categories.values()) {
            out.writeObject(cat.getId());
        }
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void xmlStringBody(StringBuffer sb, String ident) {
        super.xmlStringBody(sb, ident);

        if (m_points.size() == 0) {
            sb.append(ident).append("<points/>\n");
        } else {
            sb.append(ident).append("<points>");
            boolean first = true;
            for (TPoint mf : m_points.values()) {
                if (!first) {
                    sb.append(',');
                }
                sb.append(_calcMapElementShortID(mf));
                first = false;
            }
            sb.append("</points>\n");
        }

        if (m_categories.size() == 0) {
            sb.append(ident).append("<categories/>\n");
        } else {
            sb.append(ident).append("<categories>");
            boolean first = true;
            for (TCategory cat : m_categories.values()) {
                if (!first) {
                    sb.append(',');
                }
                sb.append(_calcMapElementShortID(cat));
                first = false;
            }
            sb.append("</categories>\n");
        }
    }
}
