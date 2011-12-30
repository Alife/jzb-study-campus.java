/**
 * 
 */
package com.jzb.ja.map.data;

import com.jzb.ja.map.IConverter;


/**
 * @author n63636
 *
 */
public class STIConverter implements IConverter {
    
    /**
     * @see com.jzb.ja.map.IConverter#convertFrom(java.lang.Object)
     */
    @Override
    public Object convertFrom(Object in) throws Exception {
        return Integer.parseInt((String)in);
    }

}
