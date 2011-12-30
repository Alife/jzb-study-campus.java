/**
 * 
 */
package com.jzb.wiki.pdp.parser;

import com.jzb.wiki.pdp.IItemParser;
import com.jzb.wiki.pdp.IParser;
import com.jzb.wiki.pdp.IPdPItem;
import com.jzb.wiki.pdp.item.PdPItem_Base;

/**
 * @author n000013
 * 
 */
public abstract class BaseItemParser implements IItemParser {

    IParser m_parser;

    public BaseItemParser(IParser parser) {
        m_parser = parser;
    }

    protected IPdPItem parseTemplate(PdPItem_Base item) throws Exception {

        for (;;) {

            String line = m_parser.readLine();

            if (line == null) {
                throw new Exception("Project item parsing error");
            }

            if (line.contains("}}"))
                break;

            if (!line.contains("|"))
                continue;

            _parseTemplateParams(item, line);
        }

        return item;
    }

    private void _parseTemplateParams(PdPItem_Base item, String line) throws Exception {

        int p1 = line.indexOf('|');
        int p2 = line.indexOf('=');
        String name = line.substring(p1 + 1, p2 - 1).trim();

        p1 = p2 + 1;
        while (p1 < line.length() && line.charAt(p1) == ' ')
            p1++;

        StringBuffer sb = new StringBuffer();
        sb.append(line.substring(p1));

        for (;;) {
            String newLine = m_parser.readLine();

            if (newLine.trim().startsWith("|") || newLine.contains("}}")) {
                m_parser.backLine();
                break;
            }

            IItemParser nestedParser = _hasNestedTemplates(newLine);
            if (nestedParser != null) {
                IPdPItem subItem = nestedParser.parse();
                item.addSubItem(subItem);
            } else {
                sb.append("\n");
                sb.append(newLine);
            }
        }

        String value = sb.toString();
        if (value.startsWith("\n")) {
            int px;
            for (px = 0; px<value.length() && value.charAt(px) == '\n'; px++) ;
            value = value.substring(px);
        }
        value=value.replace('\t',' ').trim();
        item.setAttr(name, value);

    }

    private IItemParser _hasNestedTemplates(String line) {
        for (IItemParser parser : m_parser.getItemParsers()) {
            if (parser.accept(line))
                return parser;
        }
        return null;
    }

}