/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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

    private TCoordinates            t_coordinates;
    // Solo para temas de cuenta de elementos en pantalla. No se almacena ni inicializa
    private int                     t_displayCount;

    // ---------------------------------------------------------------------------------
    public TCategory(TMap ownerMap) {
        super(EntityType.Category, ownerMap);
        t_coordinates = new TCoordinates();
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
     * @return the coordinates
     */
    public TCoordinates getCoordinates() {
        return t_coordinates;
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
    // Copia los datos, los puntos y las subcategorias, pero NO HACIA "arriba". Eso su cat "padre"
    @Override
    public void mergeFrom(TBaseEntity other, boolean conflict) {

        super.mergeFrom(other, conflict);
        TCategory casted_other = (TCategory) other;

        t_coordinates = casted_other.t_coordinates;

        TMap myMap = getOwnerMap();

        // Se copia los puntos que categoriza desde la otra.
        // El que borre los suyos o no, depende de si estan en conflicto. En cuyo caso se queda con todos
        // Si un punto o subcategoria se ha borrado en una iteración de merge previa, habrá borrado su enlace
        // NOTA 1: Cualquier punto que se intente enlazar, por el orden del "merge" deberá existir previamente
        // NOTA 2: Si no se borra mi contenido, puede que algun punto ya este enlazado y NO debe quedar DOBLE
        if (!conflict) {
            m_points.clear();
        }
        // Itero los puntos de la otra categoria
        for (TPoint point : casted_other.m_points) {
            // si no lo tengo lo añado
            TPoint myPoint = m_points.getById(point.getId());
            if (myPoint == null) {
                // Siempre que exista en mi mapa
                myPoint = myMap.getPoints().getById(point.getId());
                if (myPoint != null) {
                    m_points.add(myPoint);
                }
            }
        }

        // Lo mismo que antes con las subcategorias
        if (!conflict) {
            m_subCategories.clear();
        }
        // Itero las subcategorias de la otra categoria
        for (TCategory scat : casted_other.m_subCategories) {
            // si no la tengo lo añado
            TCategory mySCat = m_subCategories.getById(scat.getId());
            if (mySCat == null) {
                // Siempre que exista en mi mapa
                mySCat = myMap.getCategories().getById(scat.getId());
                if (mySCat != null) {
                    m_subCategories.add(mySCat);
                }
            }
        }

    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TMapFigure#readExternal(java.io.ObjectInput)
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        t_coordinates = (TCoordinates) in.readObject();
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
     * @param coordinates
     *            the coordinates to set
     */
    public void setCoordinates(TCoordinates coordinates) {
        t_coordinates = coordinates != null ? coordinates : new TCoordinates();
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
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#updateId(java.lang.String)
     */
    @Override
    public void updateId(String id) {

        String oldId = getId();
        super.updateId(id);

        for (TCategory cat : m_categories) {
            cat._fixSubItemID(oldId, this);
        }

        for (TPoint point : m_points) {
            point._fixSubItemID(oldId, this);
        }
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TMapFigure#writeExternal(java.io.ObjectOutput)
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(t_coordinates);
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
     * @see com.jzb.tpoi.data.TBaseEntity#_fixSubItemID(java.lang.String, com.jzb.tpoi.data.TBaseEntity)
     */
    @Override
    protected void _fixSubItemID(String oldID, TBaseEntity item) {
        if (item instanceof TPoint) {
            m_points.fixItemID(oldID);
        } else {
            m_subCategories.fixItemID(oldID);
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
