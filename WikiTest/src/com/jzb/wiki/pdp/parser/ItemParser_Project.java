/**
 * 
 */
package com.jzb.wiki.pdp.parser;

import com.jzb.wiki.pdp.IParser;
import com.jzb.wiki.pdp.IPdPItem;
import com.jzb.wiki.pdp.item.PdPItem_Project;

/**
 * @author n000013
 * 
 */
public class ItemParser_Project extends BaseItemParser {

    public ItemParser_Project(IParser parser) {
        super(parser);
    }

    public boolean accept(String line) {
        return line.contains("<span style=\"pdp:s_entry\">");
    }

    public IPdPItem parse() throws Exception {

        PdPItem_Project prj = new PdPItem_Project();

        m_parser.readLine();
        for (;;) {

            String line = m_parser.readLine();

            if (line == null) {
                throw new Exception("Project item parsing error");
            }

            if (line.contains("<span style=\"pdp:e_entry\">"))
                break;

            if (line.contains("<span style=\"pdp:s_"))
                parseAttribute(prj);
        }

        return prj;
    }

    private void parseAttribute(PdPItem_Project prj) throws Exception {

        StringBuffer sb = new StringBuffer();
        m_parser.backLine();

        String line = m_parser.readLine();
        int p1 = 19+line.indexOf("<span style=\"pdp:s_");
        int p2 = line.indexOf("\"></span>", p1);
        String name = line.substring(p1, p2);
        p1 = 7+line.indexOf("</span>", p2);

        for (;;) {

            if (line == null) {
                throw new Exception("Project item parsing error");
            }

            if (line.contains("<span style=\"pdp:e_"))
                break;

            if (p1 != 0) {
                sb.append(line.substring(p1));
                p1 = 0;
            } else {
                sb.append(line);
            }

            line = m_parser.readLine();

        }
        p2 = line.indexOf("<span style=\"pdp:e_");
        sb.append(line.substring(p1,p2));
        
        String value = sb.toString();
        
        prj.setAttr(name, value);
    }
}