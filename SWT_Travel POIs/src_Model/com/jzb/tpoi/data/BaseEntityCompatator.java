/**
 * 
 */
package com.jzb.tpoi.data;

import java.util.Comparator;

/**
 * @author n63636
 * 
 */
public class BaseEntityCompatator<T extends TBaseEntity> implements Comparator<T> {

    private BaseEntityComparationType m_compType;

    // ---------------------------------------------------------------------------------
    public BaseEntityCompatator(BaseEntityComparationType compType) {
        m_compType = compType;
    }

    // ---------------------------------------------------------------------------------
    public int _compareByCategoryAndName(TBaseEntity o1, TBaseEntity o2) {
        int typeComp = o1.getType().compareTo(o2.getType());
        if (typeComp != 0) {
            return typeComp;
        } else {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    }

    // ---------------------------------------------------------------------------------
    public int _compareByCreationTime(TBaseEntity o1, TBaseEntity o2) {
        return o1.getTS_Created().compareTo(o2.getTS_Created());
    }

    // ---------------------------------------------------------------------------------
    public int _compareByLastUpdateTime(TBaseEntity o1, TBaseEntity o2) {
        return o2.getTS_Updated().compareTo(o1.getTS_Updated());
    }

    // ---------------------------------------------------------------------------------
    public int _compareByName(TBaseEntity o1, TBaseEntity o2) {
        return o1.getDisplayName().compareTo(o2.getDisplayName());
    }

    // ---------------------------------------------------------------------------------
    public int compare(T o1, T o2) {
        switch (m_compType) {
            case categoryAndName:
                return _compareByCategoryAndName(o1, o2);
            case creationTime:
                return _compareByCreationTime(o1, o2);
            case lastUpdateTime:
                return _compareByLastUpdateTime(o1, o2);
            default:
                return _compareByName(o1, o2);
        }
    }
}
