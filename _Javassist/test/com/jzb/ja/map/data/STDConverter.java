/**
 * 
 */
package com.jzb.ja.map.data;

import java.text.SimpleDateFormat;

import com.jzb.ja.map.IConverter;

/**
 * @author n63636
 * 
 */
public class STDConverter implements IConverter {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
    /**
     * @see com.jzb.ja.map.IConverter#convertFrom(java.lang.Object)
     */
    @Override
    public Object convertFrom(Object in) throws Exception {
        //throw new Exception("kk");
        return sdf.parse((String)in);
    }
}
