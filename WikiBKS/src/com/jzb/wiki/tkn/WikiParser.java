/**
 * 
 */
package com.jzb.wiki.tkn;

import java.util.Collection;
import java.util.HashMap;

import com.jzb.util.Des3Encrypter;
import com.jzb.wiki.BKSWikiHelper;
import com.jzb.wiki.dt.TWikiItem;
import com.jzb.wiki.dt.TWikiItem.TYPE;

/**
 * @author n63636
 * 
 */
public class WikiParser {

    private BKSWikiHelper m_helper   = null;
    private boolean       m_loggedIn = false;

    /**
     * 
     */
    public WikiParser() {
    }

    public TWikiItem parsePage(String pageTitle, boolean recurse) throws Exception {

        HashMap<String, TWikiItem> alreadyParsed = new HashMap<String, TWikiItem>();

        TWikiItem doc = _parsePage(pageTitle, alreadyParsed, recurse);

        return doc;
    }

    private TWikiItem _parsePage(String pageTitle, HashMap<String, TWikiItem> alreadyParsed, boolean recurse) throws Exception {

        TWikiItem doc = alreadyParsed.get(pageTitle);
        if (doc == null) {
            String fullURL = "?title=" + pageTitle + "&action=edit";
            String fullTitle = pageTitle + " - banksphereWiki";

            BKSWikiHelper helper = getHelper();
            helper.navigateTo(fullURL, fullTitle);
            String wikiText = helper.getEditingText();

            doc = parseText(wikiText);

            alreadyParsed.put(pageTitle, doc);

            if (recurse) {
                Collection<TWikiItem> macros = doc.getByType(TYPE.MACRO);
                for (TWikiItem item : macros) {
                    String itemPageTitle = item.getName();
                    if (itemPageTitle != null && !itemPageTitle.startsWith("#")) {
                        itemPageTitle = "Plantilla:" + itemPageTitle;
                        TWikiItem subDoc = _parsePage(itemPageTitle, alreadyParsed, recurse);
                        doc.addChild(subDoc);
                    }
                }
            }
        }

        return doc;
    }

    public TWikiItem parseText(String wikiText) throws Exception {
        DocumentParser parser = new DocumentParser();
        TWikiItem doc = (TWikiItem) parser.parse(wikiText);
        return doc;
    }

    private BKSWikiHelper getHelper() throws Exception {
        if (m_helper == null) {
            m_helper = new BKSWikiHelper();
            if (!m_loggedIn) {
                m_helper.login(Des3Encrypter.decryptStr("PjN1Jb0t6CY0Eo9zcFVohw=="), Des3Encrypter.decryptStr("PjN1Jb0t6CYD25gJXVCyxw=="));
                m_loggedIn = true;
            }
        }
        return m_helper;
    }
}
