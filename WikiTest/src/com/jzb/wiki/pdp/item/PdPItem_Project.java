/**
 * 
 */
package com.jzb.wiki.pdp.item;

import com.jzb.wiki.util.NameValuePair;

/**
 * @author n000013
 * 
 */
public class PdPItem_Project extends PdPItem_Base {

    public PdPItem_Project() {
        NameValuePair attrs[] = { 
                new NameValuePair("title", null), 
                new NameValuePair("JP", null), 
                new NameValuePair("HT", null), 
                new NameValuePair("HP", null),
                new NameValuePair("EP", null), 
                new NameValuePair("Desc", null) 
             };
        setAttrs(attrs);
    }

    public T_Tyte getType() {
        return T_Tyte.PROJECT;
    }

    public String getTypeName() {
        return "Item-Project";
    }
}