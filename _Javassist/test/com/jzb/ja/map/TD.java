/**
 * 
 */
package com.jzb.ja.map;

/**
 * @author n63636
 * 
 */
public class TD {

    public Object value;

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (value != null)
            return "TD: (" + value.getClass() + ")" + value.toString();
        else
            return "TD: null";
    }
}
