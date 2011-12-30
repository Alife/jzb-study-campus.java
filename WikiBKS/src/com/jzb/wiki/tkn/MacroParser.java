/**
 * 
 */
package com.jzb.wiki.tkn;

import com.jzb.wiki.dt.TWikiItem;
import com.jzb.wiki.dt.TWikiItem.TYPE;

import de.susebox.jtopas.Token;
import de.susebox.jtopas.Tokenizer;

/**
 * @author n63636
 * 
 */
public class MacroParser extends TKParser {

    private String m_textValue = "";

    public MacroParser(TKParser parent) throws Exception {
        super(parent);
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getStartingTags()
     */
    @Override
    protected String[] _getStartingTags() {
        return new String[] { "{{" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getEndingTags()
     */
    @Override
    protected String[] _getEndingTags() {
        return new String[] { "}" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getInnerSubTags()
     */
    @Override
    protected String[] _getInnerSubTags() {
        return new String[] { "|" };
    }
    
    /**
     * @see com.jzb.wiki.tkn.TKParser#_childItemEnded(com.jzb.wiki.dt.TWikiItem)
     */
    @Override
    protected void _childItemEnded(TWikiItem item, TWikiItem childItem) throws Exception {
        if (item.getName() != null) {
            if (!(childItem.getType()==TYPE.HTMLCOMMENT)) {
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
                m_textValue += token.getImage();
                break;

            case Token.SPECIAL_SEQUENCE:
                if (token.getImage().equals("|")) {
                    _endTextValue(item);
                }
                break;

        }
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_createElement()
     */
    @Override
    protected TWikiItem _createElement() throws Exception {
        return  new TWikiItem(TYPE.MACRO);
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_finishElement()
     */
    @Override
    protected void _finishElement(TWikiItem item) throws Exception {
        _endTextValue(item);
    }

    private void _endTextValue(TWikiItem item) throws Exception {

        if (item.getName() == null) {
            item.setName(m_textValue.trim());
        } else {
            item.parseAndAddParam(m_textValue);
        }
        m_textValue = "";

    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_isEndingToken(de.susebox.jtopas.Token)
     */
    @Override
    protected boolean _isEndingToken(Tokenizer tokenizer, Token token) throws Exception {
        
        if (token.getType() == Token.SPECIAL_SEQUENCE && token.getImage().equalsIgnoreCase("}")) {
            return lookAhead(tokenizer, "}");
        } else {
            return false;
        }
    }
}
