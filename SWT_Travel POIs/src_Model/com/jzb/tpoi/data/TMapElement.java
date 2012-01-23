/**
 * 
 */
package com.jzb.tpoi.data;

/**
 * @author n63636
 * 
 */
public abstract class TMapElement extends TBaseEntity {

    private TMap m_ownerMap;

    // ---------------------------------------------------------------------------------
    protected TMapElement(EntityType type, TMap ownerMap) {
        super(type);
        m_ownerMap = ownerMap;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the ownerMap
     */
    public TMap getOwnerMap() {
        return m_ownerMap;
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void touchAsUpdated() {
        super.touchAsUpdated();
        if (m_ownerMap != null)
            m_ownerMap.touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see com.jzb.tpoi.data.TBaseEntity#updateId(java.lang.String)
     */
    @Override
    public void updateId(String id) {
        String oldId = getId();
        super.updateId(id);
        if (m_ownerMap != null) {
            m_ownerMap._fixItemID(oldId);
        }
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param ownerMap
     *            the ownerMap to set
     */
    public void updateOwnerMap(TMap ownerMap) {
        m_ownerMap = ownerMap;
    }

    // ---------------------------------------------------------------------------------
    @Override
    public void xmlStringBody(StringBuffer sb, String ident) {

        super.xmlStringBody(sb, ident);

        sb.append(ident).append("<ownerMap>").append(m_ownerMap != null ? m_ownerMap.getName() : "null").append("</ownerMap>\n");

    }
}
