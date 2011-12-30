/**
 * 
 */
package com.jzb.wiki.tkn;

import java.util.HashMap;

import com.jzb.util.Tracer;
import com.jzb.wiki.dt.TWikiItem;

import de.susebox.jtopas.Flags;
import de.susebox.jtopas.StandardTokenizer;
import de.susebox.jtopas.StandardTokenizerProperties;
import de.susebox.jtopas.StringSource;
import de.susebox.jtopas.Token;
import de.susebox.jtopas.Tokenizer;
import de.susebox.jtopas.TokenizerProperties;
import de.susebox.jtopas.TokenizerSource;

/**
 * @author n63636
 * 
 */
public abstract class TKParser {

    private static TokenizerProperties                 s_props   = new StandardTokenizerProperties();
    private TKParser                                   m_parent;
    private HashMap<String, Class<? extends TKParser>> m_parsers = new HashMap<String, Class<? extends TKParser>>();

    public TKParser(TKParser parent) throws Exception {
        m_parent = parent;
        _initializeProps();
        _addSubParsers();
    }

    public TWikiItem parse(String wikiText) throws Exception {
        Tokenizer tokenizer = new StandardTokenizer(s_props);
        TokenizerSource source = new StringSource(wikiText);
        tokenizer.setSource(source);
        TWikiItem item = _parseTokens(tokenizer);
        tokenizer.close();
        return item;
    }

    protected abstract void _childItemEnded(TWikiItem item, TWikiItem childItem) throws Exception;

    protected abstract void _consumeToken(Token token, TWikiItem item) throws Exception;

    protected abstract TWikiItem _createElement() throws Exception;

    protected abstract void _finishElement(TWikiItem item) throws Exception;

    protected String[] _getInnerSubTags() {
        return new String[0];
    }

    protected String[] _getEndingTags() {
        return new String[0];
    }

    protected String[] _getStartingTags() {
        return new String[0];
    }

    protected TKParser[] _getSubparsers() throws Exception {
        return new TKParser[0];
    }

    protected TKParser _getTokenParser(Token token) throws Exception {

        Class<? extends TKParser> clazz = m_parsers.get(token.getImage());
        if (clazz != null) {
            TKParser parser = clazz.getConstructor(TKParser.class).newInstance(this);
            return parser;
        } else if (m_parent != null) {
            return m_parent._getTokenParser(token);
        } else {
            return null;
        }

    }

    protected boolean _isEndingToken(Tokenizer tokenizer, Token token) throws Exception {

        if (token.getType() == Token.SPECIAL_SEQUENCE && _getEndingTags() != null) {
            String st = token.getImage();
            for (String s : _getEndingTags()) {
                if (s.equalsIgnoreCase(st)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected TWikiItem _parseTokens(Tokenizer tokenizer) throws Exception {

        Tracer._debug("[ ** STARTING PARSER: " + this);

        TWikiItem item = _createElement();

        while (tokenizer.hasMoreToken()) {

            Token token = tokenizer.nextToken();
            Tracer._debug(token);

            if (_isEndingToken(tokenizer, token))
                break;

            TKParser subParser = _getTokenParser(token);
            if (subParser != null) {
                TWikiItem childItem = subParser._parseTokens(tokenizer);
                if (childItem != null) {
                    item.addChild(childItem);
                    _childItemEnded(item, childItem);
                }
            } else {
                _consumeToken(token, item);
            }

        }

        _finishElement(item);

        Tracer._debug("] ** ENDING PARSER: " + this);

        return item;

    }

    private void _addSubParsers() throws Exception {

        for (TKParser parser : _getSubparsers()) {

            for (String tokenStr : parser._getStartingTags()) {
                s_props.addSpecialSequence(tokenStr);
                m_parsers.put(tokenStr, parser.getClass());
            }

            for (String tokenStr : parser._getEndingTags()) {
                s_props.addSpecialSequence(tokenStr);
            }

            for (String tokenStr : parser._getInnerSubTags()) {
                s_props.addSpecialSequence(tokenStr);
            }
        }
    }

    private void _initializeProps() {

        s_props.setParseFlags(Flags.F_NO_CASE | Flags.F_TOKEN_POS_ONLY | Flags.F_RETURN_WHITESPACES);
        s_props.setParseFlags(Flags.F_NO_CASE | Flags.F_RETURN_WHITESPACES | Flags.F_COUNT_LINES);
        s_props.setSeparators(null);
        // props.addBlockComment("<!--", "-->");
    }

    protected boolean lookAhead(Tokenizer tokenizer, String... tokenStrs) throws Exception {

        int rp = tokenizer.getReadPosition();
        for (String str : tokenStrs) {
            tokenizer.readMore();
            Token nextToken = tokenizer.nextToken();
            if (!nextToken.getImage().equalsIgnoreCase(str)) {
                tokenizer.setReadPositionAbsolute(rp);
                return false;
            }
        }
        return true;

    }
}
