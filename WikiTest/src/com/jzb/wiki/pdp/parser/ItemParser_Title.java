/**
 * 
 */
package com.jzb.wiki.pdp.parser;

import com.jzb.wiki.pdp.IItemParser;
import com.jzb.wiki.pdp.IParser;
import com.jzb.wiki.pdp.IPdPItem;
import com.jzb.wiki.pdp.item.PdPItem_Title;

/**
 * @author n000013
 * 
 */
public class ItemParser_Title implements IItemParser {

    private IParser m_parser;
    
    public ItemParser_Title(IParser parser) {
        m_parser = parser;
    }

    public boolean accept(String line) {
        line = line.trim();
        if (line.toLowerCase().contains("<h2"))
            return true;
        else
            return false;
    }

    public IPdPItem parse() throws Exception {
        String line = m_parser.readLine();
        int p1 = 2+line.toLowerCase().indexOf("<h");
        p1 = 1+line.toLowerCase().indexOf(">",p1);
        int p2 = line.toLowerCase().indexOf("</h");

        String value = line.substring(p1, p2).trim();
        
        return new PdPItem_Title(value);
    }

}