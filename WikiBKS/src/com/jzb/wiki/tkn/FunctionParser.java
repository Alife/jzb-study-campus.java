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
public class FunctionParser extends TKParser {

    private String m_value = "";

    public FunctionParser(TKParser parent) throws Exception {
        super(parent);
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getStartingTags()
     */
    @Override
    protected String[] _getStartingTags() {
        return new String[] { "{{#" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getEndingTags()
     */
    @Override
    protected String[] _getEndingTags() {
        return new String[] { "}" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getTokenParser(de.susebox.jtopas.Token)
     */
    @Override
    protected TKParser _getTokenParser(Token token) throws Exception {
        return null;
    }
    
    /**
     * @see com.jzb.wiki.tkn.TKParser#_createElement()
     */
    @Override
    protected TWikiItem _createElement() throws Exception {
        return  new TWikiItem(TYPE.FUNCTION);
    }
    
    /**
     * @see com.jzb.wiki.tkn.TKParser#_childItemEnded(com.jzb.wiki.dt.TWikiItem)
     */
    @Override
    protected void _childItemEnded(TWikiItem item, TWikiItem childItem) throws Exception {
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
                m_value += token.getImage();
                break;
        }

    }


    /**
     * @see com.jzb.wiki.tkn.TKParser#_finishElement()
     */
    @Override
    protected void _finishElement(TWikiItem item) throws Exception {
        item.setValue(m_value);
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
