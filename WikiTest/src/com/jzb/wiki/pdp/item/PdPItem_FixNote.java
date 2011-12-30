/**
 * 
 */
package com.jzb.wiki.pdp.item;

import com.jzb.wiki.util.NameValuePair;

/**
 * @author n000013
 * 
 */
public class PdPItem_FixNote extends PdPItem_Base {

    public PdPItem_FixNote() {
        NameValuePair attrs[] = { 
                new NameValuePair("Peticionario", null), 
                new NameValuePair("Entidad", null), 
                new NameValuePair("Areas", null), 
                new NameValuePair("F_Tope", null),
                new NameValuePair("F_Fin", null), 
                new NameValuePair("Descripcion", null), 
                new NameValuePair("Fix", null), 
             };
        setAttrs(attrs);
    }

    public T_Tyte getType() {
        return T_Tyte.FIX_NOTE;
    }

    public String getTypeName() {
        return "Item-FixNote";
    }
}