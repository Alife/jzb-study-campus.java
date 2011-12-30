/**
 * 
 */
package com.jzb.kk.pp;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class MainPageParser {

    public ArrayList<HtmlAnchor> parse(WebClient webClient, String pageUrl) {

        try {

            Tracer._debug("Parsing Main page: " + pageUrl);

            HtmlPage page = (HtmlPage) webClient.getPage(pageUrl);
            ArrayList<HtmlAnchor> list=_findLinks(page);

            return list;

        } catch (Throwable th) {
            Tracer._error("Error handling page: " + th);
            return new ArrayList<HtmlAnchor>();
        }

    }

    private ArrayList<HtmlAnchor> _findLinks(HtmlPage page) throws Exception {

        ArrayList<HtmlAnchor> list = new ArrayList<HtmlAnchor>();
        List<DomNode> links = (List<DomNode>) page.getByXPath("//div[@id='leftsidebar']");
        for (DomNode node : links) {
            for (HtmlElement child : node.getAllHtmlChildElements()) {
                if (child instanceof HtmlAnchor)
                    list.add((HtmlAnchor)child);
            }
        }
        return list;
    }

}
