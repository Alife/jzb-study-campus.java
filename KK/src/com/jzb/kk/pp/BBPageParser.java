/**
 * 
 */
package com.jzb.kk.pp;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class BBPageParser {

    public BBInfo parse(WebClient webClient, String pageUrl) {

        BBInfo bbInfo = new BBInfo();
        try {

            // Tracer._debug("Parsing B&B page: " + pageUrl);

            bbInfo.pageURL = pageUrl;

            HtmlPage page = (HtmlPage) webClient.getPage(pageUrl);

            _parseRoomInfo(page, bbInfo);
            _parseGPSInfo(page, bbInfo);

            _validateInfo(bbInfo);

            return bbInfo;

        } catch (Throwable th) {
            Tracer._error("Error handling page: " + th);
            bbInfo.isOK = false;
            return bbInfo;
        }

    }

    private void _validateInfo(BBInfo bbInfo) throws Exception {
        bbInfo.isOK = true;
        bbInfo.isOK &= bbInfo.extraBedPrice.length() > 0 && _getIntValue(bbInfo.roomsNumber) > 1;
        //bbInfo.isOK &= bbInfo.gpsLat != Double.MIN_VALUE && bbInfo.gpsLng != Double.MIN_VALUE;
    }

    private void _parseGPSInfo(HtmlPage page, BBInfo bbInfo) throws Exception {

        HtmlElement td = _findTdStrNode(page, "Latitude");
        if (td != null) {

            String value = _getNodeStringValue(td).toLowerCase();
            int p1 = value.indexOf("latitude:");
            int p2 = value.indexOf("longitude:");
            if (p1 < 0 || p2 < 0) {
                bbInfo.gpsLat = Double.MIN_VALUE;
                bbInfo.gpsLng = Double.MIN_VALUE;
            } else {
                bbInfo.gpsLat = _getDoubleValue(value.substring(p1 + 9, p2).trim());
                bbInfo.gpsLng = _getDoubleValue(value.substring(p2 + 10).trim());
            }

        } else {
            Tracer._warn("Warning: GPS node not found");
        }

    }

    private void _parseRoomInfo(HtmlPage page, BBInfo bbInfo) throws Exception {

        HtmlElement td = _findTdStrNode(page, "Bedroom(3 pers.)");
        if (td != null) {
            DomNode node = td.getNextSibling();
            bbInfo.roomsNumber = _getNodeStringValue(node);
            node = node.getNextSibling();
            bbInfo.dayMinPrice = _getNodeStringValue(node);
            node = node.getNextSibling();
            bbInfo.dayMaxPrice = _getNodeStringValue(node);
            node = node.getNextSibling();
            node = node.getNextSibling();
            node = node.getNextSibling();
            bbInfo.extraBedPrice = _getNodeStringValue(node);
        } else {
            Tracer._warn("Warning: 3 Rooms node not found");
        }

    }

    private int _getIntValue(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Throwable th) {
            return -1;
        }
    }

    private double _getDoubleValue(String str) {
        try {
            String str2 = new String(str.replace('\u00a0', ' ').trim());
            return Double.parseDouble(str2);
        } catch (Throwable th) {
            return Double.MIN_VALUE;
        }
    }

    private String _getNodeStringValue(DomNode node) {
        String value = node.getTextContent();
        if (value == null)
            return "";
        else
            return value.trim();
    }

    private HtmlElement _findTdStrNode(HtmlPage page, String searchedText) throws Exception {

        searchedText = searchedText.toLowerCase();
        List<DomNode> tables = (List<DomNode>) page.getByXPath("//table[@width='100%']");
        for (DomNode node : tables) {
            for (HtmlElement child : node.getHtmlElementDescendants()) {
                if (child instanceof HtmlTableDataCell && _getNodeStringValue(child).toLowerCase().startsWith(searchedText)) {
                    return child;
                }
            }
        }

        return null;
    }

}
