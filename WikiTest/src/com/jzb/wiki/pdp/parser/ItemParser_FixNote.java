/**
 * 
 */
package com.jzb.wiki.pdp.parser;

import com.jzb.wiki.pdp.IItemParser;
import com.jzb.wiki.pdp.IParser;
import com.jzb.wiki.pdp.IPdPItem;
import com.jzb.wiki.pdp.item.PdPItem_FixNote;

/**
 * @author n000013
 * 
 */
public class ItemParser_FixNote implements IItemParser {

    private IParser m_parser;

    public ItemParser_FixNote(IParser parser) {
        m_parser=parser;
    }

    public boolean accept(String line) {
        return line.contains("{{NOTAS_PRJ_FUERA_PDP");
    }
    
    public IPdPItem parse() throws Exception {
        PdPItem_FixNote fixNote = new PdPItem_FixNote();
        return parseTemplate(fixNote);
    }
}