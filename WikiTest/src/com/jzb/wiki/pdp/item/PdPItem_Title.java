/**
 * 
 */
package com.jzb.wiki.pdp.item;


/**
 * @author n000013
 * 
 */
public class PdPItem_Title extends PdPItem_Base {


    public PdPItem_Title(String val) {
        setValue(val);
    }

    public T_Tyte getType() {
        return T_Tyte.TITLE;
    }

    public String getTypeName() {
        return "Item-Title";
    }
}