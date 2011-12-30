/**
 * 
 */
package com.jzb.ja.map;

import java.util.HashMap;

/**
 * @author n63636
 * 
 */
public class IntMapper {

    private MapData[] m_mappings;

    public IntMapper(MapData[] mappings) {
        m_mappings = mappings;
    }

    public void map(HashMap dataIn, HashMap dataOut) throws Exception {

        for (MapData mapping : m_mappings) {

            try {
                Object o1 = _getElement(mapping.origin, dataIn);
                TD o2 = (TD) _getElement(mapping.dest, dataOut);
                o2.setValue(mapping.converter.convertFrom(o1));
            } catch (Exception ex) {
                System.out.println("Error mapping: " + mapping);
                ex.printStackTrace();
                throw new Exception("Error mapping: " + mapping, ex);
            }
        }
        // System.out.println("Mapper done!");
    }

    private Object _getElement(String fullName, HashMap data) {
        int p1 = fullName.indexOf('.');
        if (p1 < 0) {
            return data.get(fullName);
        } else {
            String pName = fullName.substring(0, p1);
            String cName = fullName.substring(p1 + 1);
            return _getElement(cName, (HashMap) data.get(pName));
        }
    }
}
