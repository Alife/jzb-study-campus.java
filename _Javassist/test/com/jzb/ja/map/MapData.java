/**
 * 
 */
package com.jzb.ja.map;

/**
 * @author n63636
 * 
 */
public class MapData {

    public String     origin;
    public String     dest;
    public IConverter converter;

    public MapData(String o, String d, IConverter c) {
        origin = o;
        dest = d;
        converter = c;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Mapping: '"+origin+"' -> '" + dest + "' / " + converter.getClass();
    }
}
