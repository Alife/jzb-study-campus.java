/**
 * 
 */
package com.jzb.wiki.pdp;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

import com.jzb.wiki.pdp.parser.ItemParser_FixNote;
import com.jzb.wiki.pdp.parser.ItemParser_Project;
import com.jzb.wiki.pdp.parser.ItemParser_Title;

/**
 * @author n000013
 * 
 */
public class PdPInfoParser implements IParser {

    private BufferedReader m_br;
    private IItemParser    m_parsers[] = { 
            new ItemParser_Title(this), 
            new ItemParser_Project(this)
            //new ItemParser_FixNote(this)
    };
    
    private String m_text;

    public PdPInfoParser(String text) {
        m_text = text;
    }

    public IItemParser[] getItemParsers() {
        return m_parsers;
    }

    public ArrayList<IPdPItem> parse() throws Exception {

        ArrayList<IPdPItem> items= new ArrayList<IPdPItem>();
        
        m_br = new BufferedReader(new StringReader(m_text));
        while (m_br.ready()) {
            
            String line = readLine();
            if (line == null)
                break;

            for (IItemParser ipr : m_parsers) {

                if (!ipr.accept(line))
                    continue;
                
                backLine();
                IPdPItem item = ipr.parse();
                items.add(item);
                break;
            }

        }
        m_br.close();
        
        return items;
    }

    /**
     * @see com.jzb.wiki.pdp.IParser#readLine()
     */
    public String readLine() throws Exception {
        m_br.mark(65536);
        return m_br.readLine();
    }
    
    /**
     * @see com.jzb.wiki.pdp.IParser#backLine()
     */
    public void backLine() throws Exception {
        m_br.reset();
    }
}
