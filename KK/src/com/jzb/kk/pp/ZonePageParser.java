/**
 * 
 */
package com.jzb.kk.pp;

import java.util.ArrayList;
import java.util.HashSet;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class ZonePageParser {

    public ArrayList<HtmlAnchor> parse(WebClient webClient, HtmlAnchor href) {

        try {

            Tracer._debug("Parsing Zone page: " + href.getHrefAttribute());

            HtmlPage page = (HtmlPage) webClient.getPage(href.getHrefAttribute());
            ArrayList<HtmlAnchor> list = _findLinks(page);

            return list;

        } catch (Throwable th) {
            Tracer._error("Error handling page: " + th);
            return new ArrayList<HtmlAnchor>();
        }

    }

    private ArrayList<HtmlAnchor> _findLinks(HtmlPage page) throws Exception {

        HashSet<String> done = new HashSet<String>();

        ArrayList<HtmlAnchor> list = new ArrayList<HtmlAnchor>();
        for (HtmlElement child : page.getAllHtmlChildElements()) {
            if (child instanceof HtmlAnchor) {
                HtmlAnchor link = (HtmlAnchor) child;
                if (link.getHrefAttribute().contains("/english/Detailed/") && !done.contains(link.getHrefAttribute())) {
                    done.add(link.getHrefAttribute());
                    list.add(link);
                }
            }
        }
        return list;
    }

}
