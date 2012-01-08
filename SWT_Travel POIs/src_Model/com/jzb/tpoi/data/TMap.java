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
public class TMap extends TBaseEntity {

    private HashMap<String, TCategory> m_categories = new HashMap<String, TCategory>();
    private TPoint                     m_extInfoPoint;
    private HashMap<String, TPoint>    m_points     = new HashMap<String, TPoint>();

    // ---------------------------------------------------------------------------------
    public TMap() {
        super(EntityType.Map);
        m_extInfoPoint = ExtendedInfo.createEmptyExtInfoPoint(this);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the extInfoPoint
     */
    public TPoint __getExtInfoPoint() {
        return m_extInfoPoint;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param extInfoPoint
     *            the extInfoPoint to set
     */
    public void __setExtInfoPoint(TPoint extInfoPoint) {
        m_extInfoPoint = extInfoPoint;
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

        TCategory oldCat = m_categories.put(cat.getId(), cat);
        if (!cat.equals(oldCat)) {
            touchAsUpdated();
        }

    }

    // ---------------------------------------------------------------------------------
    public void addPoint(TPoint point) {

        if (point == null)
            return;

        TPoint oldPoint;
        if (ExtendedInfo.isExtInfoPoint(point)) {
            m_extInfoPoint = point;
            oldPoint = null;
        } else {
            oldPoint = m_points.put(point.getId(), point);
        }

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
    public boolean containsCategoryById(String categoryID) {
        return m_categories.containsKey(categoryID);
    }

    // ---------------------------------------------------------------------------------
    public boolean containsPointById(String pointID) {
        return m_points.containsKey(pointID);
    }

    // ---------------------------------------------------------------------------------
    public void deleteCategory(TCategory cat) {

        if (m_categories.remove(cat.getId()) != null) {
            cat.clearAll();
            touchAsUpdated();
        }
    }

    // ---------------------------------------------------------------------------------
    public void deletePoint(TPoint point) {

        if (m_points.remove(point.getId()) != null) {

            for (TCategory cat : m_categories.values()) {
                cat.deletePoint(point);
            }

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
    public TCategory getCategoryById(String categoryID) {
        return m_categories.get(categoryID);
    }

    // ---------------------------------------------------------------------------------
    public TPoint getPointById(String pointID) {
        return m_points.get(pointID);
    }

    // ---------------------------------------------------------------------------------
    public void readBodyExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        m_extInfoPoint = new TPoint(this);
        m_extInfoPoint.readExternal(in);

        int len1 = in.readInt();
        for (int n = 0; n < len1; n++) {
            TPoint point = new TPoint(this);
            point.readExternal(in);
            m_points.put(point.getId(), point);
        }

        int len2 = in.readInt();
        for (int n = 0; n < len2; n++) {
            TCategory cat = new TCategory(this);
            cat.readExternal(in);
            m_categories.put(cat.getId(), cat);
        }
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#readExternal(java.io.ObjectInput)
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        readHeaderExternal(in);
        readBodyExternal(in);
    }

    // ---------------------------------------------------------------------------------
    public void readHeaderExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    // ---------------------------------------------------------------------------------
    public void writeBodyExternal(ObjectOutput out) throws IOException {

        m_extInfoPoint.writeExternal(out);

        out.writeInt(m_points.size());
        for (TPoint point : m_points.values()) {
            point.writeExternal(out);
        }

        // Escribe en orden de uso
        HashSet<String> writtenCats = new HashSet<String>();
        out.writeInt(m_categories.size());
        for (TCategory cat : m_categories.values()) {
            _writeCat(out, cat, writtenCats);
        }
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#writeExternal(java.io.ObjectOutput)
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        writeHeaderExternal(out);
        writeBodyExternal(out);
    }

    // ---------------------------------------------------------------------------------
    public void writeHeaderExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void xmlStringBody(StringBuffer sb, String ident) {
        super.xmlStringBody(sb, ident);

        if (m_categories.size() == 0) {
            sb.append(ident).append("<categories/>\n");
        } else {
            sb.append(ident).append("<categories>\n");
            for (TCategory p : m_categories.values()) {
                sb.append(p.toXmlString(ident + "  ")).append("\n");
            }
            sb.append(ident).append("</categories>\n");
        }

        if (m_points.size() == 0) {
            sb.append(ident).append("<points/>\n");
        } else {
            sb.append(ident).append("<points>\n");
            for (TPoint p : m_points.values()) {
                sb.append(p.toXmlString(ident + "  ")).append("\n");
            }
            sb.append(ident).append("</points>\n");
        }

        if (m_extInfoPoint == null) {
            sb.append(ident).append("<ext_info_point/>\n");
        } else {
            sb.append(ident).append("<ext_info_point>\n");
            sb.append(m_extInfoPoint.toXmlString(ident + "  ")).append("\n");
            sb.append(ident).append("</ext_info_point>\n");
        }
    }

    // ---------------------------------------------------------------------------------
    private void _writeCat(ObjectOutput out, TCategory cat, HashSet<String> writtenCats) throws IOException {

        if (writtenCats.add(cat.getId())) {

            for (TCategory subcat : cat.getAllCategories()) {
                _writeCat(out, subcat, writtenCats);
            }

            cat.writeExternal(out);
        }
    }
}
