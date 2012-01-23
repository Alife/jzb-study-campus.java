/**
 * 
 */
package com.jzb.tpoi.data;

import java.util.Collection;
import java.util.HashSet;

import com.jzb.tpoi.data.NMCollection.LinkedColl;

/**
 * @author n63636
 * 
 */
public class TCategory extends TMapElement {

    @LinkedColl(name = "subCategories")
    private NMCollection<TCategory> m_categories    = new NMCollection<TCategory>(this);

    @LinkedColl(name = "categories")
    private NMCollection<TPoint>    m_points        = new NMCollection<TPoint>(this);

    @LinkedColl(name = "categories")
    private NMCollection<TCategory> m_subCategories = new NMCollection<TCategory>(this);

    // Solo para temas de cuenta de elementos en pantalla. No se almacena ni inicializa
    private int                     t_displayCount;

    // ---------------------------------------------------------------------------------
    public TCategory(TMap ownerMap) {
        super(EntityType.Category, ownerMap);
    }

    // ---------------------------------------------------------------------------------
    // Copia los datos y las categorias, pero NO LOS PUNTOS
    @Override
    public void assignFrom(TBaseEntity other) {

        super.assignFrom(other);
        TCategory casted_other = (TCategory) other;

        m_points.clear();
        m_subCategories.clear();
        m_categories.clear();

        // Si las categorias en "other" no existen en este mapa la creas como nuevas de forma recursiva
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
    /**
     * @return the points
     */
    public Collection<TPoint> getAllRecursivePoints() {

        HashSet<TPoint> allPoints = new HashSet<TPoint>(m_points.values());
        for (TCategory cat : getSubCategories()) {
            allPoints.addAll(cat.getAllRecursivePoints());
        }
        return allPoints;
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
     * @return the displayCount
     */
    public int getDisplayCount() {
        return t_displayCount;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the points
     */
    public NMCollection<TPoint> getPoints() {
        return m_points;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the subCategories
     */
    public NMCollection<TCategory> getSubCategories() {
        return m_subCategories;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return increments the display count
     */
    public void incrementDisplayCount() {
        t_displayCount++;
    }

    // ---------------------------------------------------------------------------------
    public boolean recursiveContainsPoint(TPoint p) {
        if (getPoints().contains(p)) {
            return true;
        } else {
            for (TCategory cat : getSubCategories()) {
                if (cat.recursiveContainsPoint(p)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    public boolean recursiveContainsSubCategory(TCategory c) {
        if (getSubCategories().contains(c)) {
            return true;
        } else {
            for (TCategory cat : getSubCategories()) {
                if (cat.recursiveContainsSubCategory(c)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param displayCount
     *            the displayCount to set
     */
    public void setDisplayCount(int displayCount) {
        t_displayCount = displayCount;
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void xmlStringBody(StringBuffer sb, String ident) {
        super.xmlStringBody(sb, ident);

        if (m_points.size() <= 0) {
            sb.append(ident).append("<points/>\n");
        } else {
            sb.append(ident).append("<points>");
            boolean first = true;
            for (TPoint point : m_points) {
                if (!first)
                    sb.append(", ");
                sb.append(point.getName());
                first = false;
            }
            sb.append("</points>\n");
        }

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

        if (m_subCategories.size() <= 0) {
            sb.append(ident).append("<subCategories/>\n");
        } else {
            sb.append(ident).append("<subCategories>");
            boolean first = true;
            for (TCategory cat : m_subCategories) {
                if (!first)
                    sb.append(", ");
                sb.append(cat.getName());
                first = false;
            }
            sb.append("</subCategories>\n");
        }

    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#getDefaultIcon()
     */
    @Override
    protected TIcon getDefaultIcon() {
        // TODO Auto-generated method stub
        return TIcon.createFromURL(TIcon.DEFAULT_CAT_ICON_URL);
    }
}
