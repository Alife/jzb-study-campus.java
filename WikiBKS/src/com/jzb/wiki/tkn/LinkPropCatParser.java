/**
 * 
 */
package com.jzb.wiki.tkn;

import com.jzb.wiki.dt.TWikiItem;
import com.jzb.wiki.dt.TWikiItem.TYPE;

import de.susebox.jtopas.Token;

/**
 * @author n63636
 * 
 */
public class LinkPropCatParser extends TKParser {

    private String m_textValue = "";

    public LinkPropCatParser(TKParser parent) throws Exception {
        super(parent);
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getStartingTags()
     */
    @Override
    protected String[] _getStartingTags() {
        return new String[] { "[[" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getEndingTags()
     */
    @Override
    protected String[] _getEndingTags() {
        return new String[] { "]]" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getInnerSubTags()
     */
    @Override
    protected String[] _getInnerSubTags() {
        return new String[] { ":", "|" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_childItemEnded(com.jzb.wiki.dt.TWikiItem)
     */
    @Override
    protected void _childItemEnded(TWikiItem item, TWikiItem childItem) throws Exception {
        if (item.getName() != null) {
            if (!(childItem.getType() == TYPE.HTMLCOMMENT)) {
                m_textValue += "$" + childItem.getName() + "$";
            }
        }
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_consumeToken(de.susebox.jtopas.Token, com.jzb.wiki.dt.TWikiItem)
     */
    @Override
    protected void _consumeToken(Token token, TWikiItem item) throws Exception {

        switch (token.getType()) {

            case Token.NORMAL:
            case Token.WHITESPACE:
            case Token.SPECIAL_SEQUENCE:
                m_textValue += token.getImage();
                break;
        }
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_createElement()
     */
    @Override
    protected TWikiItem _createElement() throws Exception {
        return new TWikiItem(TYPE.LINK);
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_finishElement()
     */
    @Override
    protected void _finishElement(TWikiItem item) throws Exception {
        _endTextValue(item);
    }

    private void _endTextValue(TWikiItem item) throws Exception {

        if (m_textValue.contains("::")) {
            item.setType(TYPE.PROPERTY);
            TWikiItem.parseNameValue(item, m_textValue, "::");
        } else if (m_textValue.contains(":")) {
            item.setType(TYPE.CATEGORY);
            if(m_textValue.trim().startsWith(":"))
                TWikiItem.parseNameValue(item, m_textValue.trim().substring(1), ":");
            else
                TWikiItem.parseNameValue(item, m_textValue, ":");
        } else {
            item.setValue(m_textValue.trim());
        }

        m_textValue = "";

    }

}
