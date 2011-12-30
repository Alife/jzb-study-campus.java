/**
 * 
 */
package com.jzb.wiki.pdp;

import java.util.ArrayList;

import com.jzb.wiki.util.NameValuePair;

/**
 * @author n000013
 * 
 */
public interface IPdPItem {

    public enum T_Tyte {
        TITLE, PROJECT, FIX_NOTE
    };

    public T_Tyte getType();

    public String getTypeName();

    public String getValue();
    
    public NameValuePair[] getAttrs();
    
    public String getAttrVal(String name);
    public String getAttrVal(int index);
    
    public ArrayList<IPdPItem> getSubItems();
}