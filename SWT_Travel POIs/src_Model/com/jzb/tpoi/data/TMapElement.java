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

}
