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
public class HTMLCommentParser extends TKParser {

    public HTMLCommentParser(TKParser parent) throws Exception {
        super(parent);
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getStartingTags()
     */
    @Override
    protected String[] _getStartingTags() {
        return new String[] { "<!--", "<noinclude>" };
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_getEndingTags()
     */
    @Override
    protected String[] _getEndingTags() {
        return new String[] { "-->", "</noinclude>" };
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
        String s = item.getValue().toString();
        item.setValue(s + token.getImage());
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_createElement()
     */
    @Override
    protected TWikiItem _createElement() throws Exception {
        TWikiItem item = new TWikiItem(TYPE.HTMLCOMMENT);
        item.setValue("");
        return item;
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_finishElement()
     */
    @Override
    protected void _finishElement(TWikiItem item) throws Exception {
    }

    @Override
    protected TKParser _getTokenParser(Token token) throws Exception {
        return null;
    }

    @Override
    protected boolean _isEndingToken(Tokenizer tokenizer, Token token) throws Exception {
        if (token.getImage().toString().equalsIgnoreCase("-->")) {
            return true;
        } else {
            return false;
        }
    }

}
