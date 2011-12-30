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
public class MacroParamParser extends TKParser {

    private String  m_paramValue      = "";
    private boolean m_processingParam = false;

    public MacroParamParser(TKParser parent) throws Exception {
        super(parent);
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getStartingTags()
     */
    @Override
    protected String[] _getStartingTags() {
        return new String[] { "{{{" };
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
        if (m_processingParam) {
            if (!(childItem.getType()==TYPE.HTMLCOMMENT)) {
                m_paramValue += "$" + childItem.getName() + "$";
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
                if (item.getName() == null) {
                    item.setName(token.getImage());
                } else {
                    if (m_processingParam) {
                        _processParamToken(token);
                    }
                }
                break;

            case Token.WHITESPACE:
                if (m_processingParam) {
                    _processParamToken(token);
                }
                break;

            case Token.SPECIAL_SEQUENCE:
                if (token.getImage().equals("|")) {
                    if (m_processingParam) {
                        _endParam(item);
                    }
                    m_processingParam = true;
                }
                break;

        }
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_createElement()
     */
    @Override
    protected TWikiItem _createElement() throws Exception {
        return  new TWikiItem(TYPE.MACRO_PARAM);
    }


    /**
     * @see com.jzb.wiki.tkn.TKParser#_finishElement()
     */
    @Override
    protected void _finishElement(TWikiItem item) throws Exception {
        if (m_processingParam) {
            _endParam(item);
        }
    }

    private void _endParam(TWikiItem item) throws Exception {

        item.parseAndAddParam(m_paramValue);
        m_paramValue = "";
        m_processingParam = false;

    }

    private void _processParamToken(Token token) throws Exception {
        m_paramValue += token.getImage();
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_isEndingToken(de.susebox.jtopas.Token)
     */
    @Override
    protected boolean _isEndingToken(Tokenizer tokenizer, Token token) throws Exception {

        if (token.getType() == Token.SPECIAL_SEQUENCE && token.getImage().equalsIgnoreCase("}")) {
            return lookAhead(tokenizer, "}","}");
        } else {
            return false;
        }
    }
}
