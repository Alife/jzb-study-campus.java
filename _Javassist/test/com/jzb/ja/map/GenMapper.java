/**
 * 
 */
package com.jzb.ja.map;

import java.util.HashMap;

/**
 * @author n63636
 * 
 */
public class GenMapper {

    private MapData[] m_mappings;

    public void map(java.util.HashMap dataIn, java.util.HashMap dataOut) throws Exception {
        String mapLine = "";
        Object o1;
        TD td;
        try {

            mapLine = "Mapping: 'datoA' -> 'datoX' / class com.jzb.ja.map.data.STDConverter";
            o1 = dataIn.get("datoA");
            td = (TD) dataOut.get("datoX");
            td.setValue(m_mappings[0].converter.convertFrom(o1));

            mapLine = "Mapping: 'complex.imp' -> 'datoY1' / class com.jzb.ja.map.data.STIConverter";
            o1 = ((HashMap) dataIn.get("complex")).get("imp");
            td = (TD) dataOut.get("datoY1");
            td.setValue(m_mappings[1].converter.convertFrom(o1));

            mapLine = "Mapping: 'complex.cur' -> 'datoY2' / class com.jzb.ja.map.data.NullConverter";
            o1 = ((HashMap) dataIn.get("complex")).get("cur");
            td = (TD) dataOut.get("datoY2");
            td.setValue(m_mappings[2].converter.convertFrom(o1));

            mapLine = "Mapping: 'datoB' -> 'datoZ' / class com.jzb.ja.map.data.NullConverter";
            o1 = dataIn.get("datoB");
            td = (TD) dataOut.get("datoZ");
            td.setValue(m_mappings[3].converter.convertFrom(o1));

        } catch (Exception ex) {
            throw new Exception("Error mapping: " + mapLine, ex);
        }
    }
}
