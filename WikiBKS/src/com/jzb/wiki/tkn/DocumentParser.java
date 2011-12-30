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
public class DocumentParser extends TKParser {

    public DocumentParser() throws Exception {
        this(null);
    }

    public DocumentParser(TKParser parent) throws Exception {
        super(parent);
    }

   
    /**
     * @see com.jzb.wiki.tkn.TKParser#_createElement()
     */
    @Override
    protected TWikiItem _createElement() throws Exception {
        return  new TWikiItem(TYPE.DOCUMENT);
    }
    
    /**
     * @see com.jzb.wiki.tkn.TKParser#_getSubparsers()
     */
    @Override
    protected TKParser[] _getSubparsers() throws Exception {
        return new TKParser[] { 
                new MacroParser(null), 
                new MacroParamParser(null), 
                new HTMLCommentParser(null), 
                new FunctionParser(null),
                new LinkPropCatParser(null)
        };
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
    }

    /**
     * @see com.jzb.wiki.tkn.TKParser#_finishElement()
     */
    @Override
    protected void _finishElement(TWikiItem item) throws Exception {
    }
}
