/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class TMap extends TBaseEntity {

    private NMCollection<TCategory> m_categories        = new NMCollection<TCategory>(this) {

                                                            @Override
                                                            protected void dlbLink(TCategory item) {
                                                            }

                                                            @Override
                                                            protected void dlbUnlink(TCategory item) {
                                                                item.getPoints().clear();
                                                                item.getSubCategories().clear();
                                                                if (!item.isLocal()) {
                                                                    m_deletedCategories.add(item);
                                                                }
                                                            }
                                                        };

    private NMCollection<TCategory> m_deletedCategories = new NMCollection<TCategory>(this) {

                                                            @Override
                                                            protected void dlbLink(TCategory item) {
                                                            }

                                                            @Override
                                                            protected void dlbUnlink(TCategory item) {
                                                            }
                                                        };

    private NMCollection<TPoint>    m_deletedPoints     = new NMCollection<TPoint>(this) {

                                                            @Override
                                                            protected void dlbLink(TPoint item) {
                                                            }

                                                            @Override
                                                            protected void dlbUnlink(TPoint item) {
                                                            }
                                                        };

    private TPoint                  m_extInfoPoint;

    private NMCollection<TPoint>    m_points            = new NMCollection<TPoint>(this) {

                                                            @Override
                                                            protected void dlbLink(TPoint item) {
                                                            }

                                                            @Override
                                                            protected void dlbUnlink(TPoint item) {
                                                                item.getCategories().clear();
                                                                if (!item.isLocal()) {
                                                                    m_deletedPoints.add(item);
                                                                }
                                                            }
                                                        };

    // ---------------------------------------------------------------------------------
    public TMap() {
        super(EntityType.Map);
        m_extInfoPoint = ExtendedInfo.createEmptyExtInfoPoint();
        m_extInfoPoint.updateOwnerMap(this);
    }

    // ---------------------------------------------------------------------------------
    // Copia los datos, los puntos y las categorias
    @Override
    public void assignFrom(TBaseEntity other) {

        super.assignFrom(other);
        TMap casted_other = (TMap) other;

        m_extInfoPoint.assignFrom(casted_other.m_extInfoPoint);

        m_points.clear();
        m_deletedPoints.clear();
        m_categories.clear();
        m_deletedCategories.clear();

        for (TCategory cat : casted_other.m_categories) {
            TCategory myCat = new TCategory(this);
            m_categories.add(myCat);
            myCat.assignFrom(cat);
        }

        for (TCategory cat : casted_other.m_deletedCategories) {
            TCategory myCat = new TCategory(this);
            m_deletedCategories.add(myCat);
            myCat.assignFrom(cat);
        }

        for (TPoint point : casted_other.m_points) {
            TPoint myPoint = new TPoint(this);
            m_points.add(myPoint);
            myPoint.assignFrom(point);
        }

        for (TPoint point : casted_other.m_deletedPoints) {
            TPoint myPoint = new TPoint(this);
            m_deletedPoints.add(myPoint);
            myPoint.assignFrom(point);
        }
    }

    // ---------------------------------------------------------------------------------
    public void clearInfo() {
        m_points.clear();
        m_categories.clear();
        m_deletedPoints.clear();
        m_deletedCategories.clear();
        m_extInfoPoint = ExtendedInfo.createEmptyExtInfoPoint();
        m_extInfoPoint.updateOwnerMap(this);
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
     * @return the deletedCategories
     */
    public NMCollection<TCategory> getDeletedCategories() {
        return m_deletedCategories;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the deletedPoints
     */
    public NMCollection<TPoint> getDeletedPoints() {
        return m_deletedPoints;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the extInfoPoint
     */
    public TPoint getExtInfoPoint() {
        return m_extInfoPoint;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the points
     */
    public NMCollection<TPoint> getPoints() {
        return m_points;
    }

    // ---------------------------------------------------------------------------------
    public void readBodyExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        m_extInfoPoint = new TPoint(this);
        m_extInfoPoint.readExternal(in);

        int len;

        len = in.readInt();
        for (int n = 0; n < len; n++) {
            TCategory cat = new TCategory(this);
            cat.readExternal(in);
            m_categories.add(cat);
        }

        len = in.readInt();
        for (int n = 0; n < len; n++) {
            TPoint point = new TPoint(this);
            point.readExternal(in);
            m_points.add(point);
        }

        len = in.readInt();
        for (int n = 0; n < len; n++) {
            TCategory cat = new TCategory(this);
            cat.readExternal(in);
            m_deletedCategories.add(cat);
        }

        len = in.readInt();
        for (int n = 0; n < len; n++) {
            TPoint point = new TPoint(this);
            point.readExternal(in);
            m_deletedPoints.add(point);
        }

        len = in.readInt();
        for (int n = 0; n < len; n++) {
            String pId = (String) in.readObject();
            TPoint point = m_points.getById(pId);

            int len2 = in.readInt();
            for (int x = 0; x < len2; x++) {
                String catId = (String) in.readObject();
                TCategory cat = m_categories.getById(catId);
                point.getCategories().add(cat);
            }
        }

        len = in.readInt();
        for (int n = 0; n < len; n++) {
            String cId1 = (String) in.readObject();
            TCategory cat1 = m_categories.getById(cId1);

            int len2 = in.readInt();
            for (int x = 0; x < len2; x++) {
                String catId = (String) in.readObject();
                TCategory cat2 = m_categories.getById(catId);
                cat1.getSubCategories().add(cat2);
            }
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
    public void resetChanged() {

        if (m_extInfoPoint != null) {
            m_extInfoPoint.updateChanged(false);
            m_extInfoPoint.setSyncStatus(SyncStatusType.Sync_OK);

        }

        for (TPoint point : m_points) {
            point.updateChanged(false);
            point.setSyncStatus(SyncStatusType.Sync_OK);
        }

        m_deletedPoints.clear();

        for (TCategory cat : m_categories) {
            cat.updateChanged(false);
            cat.setSyncStatus(SyncStatusType.Sync_OK);
        }

        m_deletedCategories.clear();

        // Por ultimo marca el estado como sincronizado y todo sin modificar
        setSyncStatus(SyncStatusType.Sync_OK);
        updateChanged(false);
        
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param extInfoPoint
     *            the extInfoPoint to set
     */
    public void setExtInfoPoint(TPoint extInfoPoint) {
        m_extInfoPoint = extInfoPoint;
    }

    // ---------------------------------------------------------------------------------
    public void writeBodyExternal(ObjectOutput out) throws IOException {

        m_extInfoPoint.writeExternal(out);

        out.writeInt(m_categories.size());
        for (TCategory cat : m_categories) {
            cat.writeExternal(out);
        }

        out.writeInt(m_points.size());
        for (TPoint point : m_points) {
            point.writeExternal(out);
        }

        out.writeInt(m_deletedCategories.size());
        for (TCategory cat : m_deletedCategories) {
            cat.writeExternal(out);
        }

        out.writeInt(m_deletedPoints.size());
        for (TPoint point : m_deletedPoints) {
            point.writeExternal(out);
        }

        out.writeInt(m_points.size());
        for (TPoint point : m_points) {
            out.writeObject(point.getId());
            out.writeInt(point.getCategories().size());
            for (TCategory cat : point.getCategories()) {
                out.writeObject(cat.getId());
            }
        }

        out.writeInt(m_categories.size());
        for (TCategory cat1 : m_categories) {
            out.writeObject(cat1.getId());
            out.writeInt(cat1.getSubCategories().size());
            for (TCategory cat2 : cat1.getSubCategories()) {
                out.writeObject(cat2.getId());
            }
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
            for (TCategory p : m_categories) {
                sb.append(p.toXmlString(ident + "  ")).append("\n");
            }
            sb.append(ident).append("</categories>\n");
        }

        if (m_points.size() == 0) {
            sb.append(ident).append("<points/>\n");
        } else {
            sb.append(ident).append("<points>\n");
            for (TPoint p : m_points) {
                sb.append(p.toXmlString(ident + "  ")).append("\n");
            }
            sb.append(ident).append("</points>\n");
        }

        if (m_deletedPoints.size() == 0) {
            sb.append(ident).append("<deletedPoints/>\n");
        } else {
            sb.append(ident).append("<deletedPoints>\n");
            for (TPoint p : m_deletedPoints) {
                sb.append(p.toXmlString(ident + "  ")).append("\n");
            }
            sb.append(ident).append("</deletedPoints>\n");
        }

        if (m_deletedCategories.size() == 0) {
            sb.append(ident).append("<deletedCategories/>\n");
        } else {
            sb.append(ident).append("<deletedCategories>\n");
            for (TCategory c : m_deletedCategories) {
                sb.append(c.toXmlString(ident + "  ")).append("\n");
            }
            sb.append(ident).append("</deletedCategories>\n");
        }

        if (m_extInfoPoint == null) {
            sb.append(ident).append("<ext_info_point/>\n");
        } else {
            sb.append(ident).append("<ext_info_point>\n");
            sb.append(m_extInfoPoint.toXmlString(ident + "  ")).append("\n");
            sb.append(ident).append("</ext_info_point>\n");
        }
    }
}