/**
 * 
 */
package com.jzb.wiki;

import com.jzb.util.Tracer;
import com.meterware.httpunit.FormControl;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.cookies.CookieProperties;
import com.meterware.httpunit.dom.HTMLTextAreaElementImpl;
import com.meterware.httpunit.javascript.JavaScript;

/**
 * @author n000013
 * 
 */
public class BKSWikiHelper {

    private static final String LOGIN_OK_TITLE = "Portada - banksphereWiki";

    private static final String URL_WIKI_BASE  = "http://localhost/banksphereWiki/index.php";
    // private static final String URL_WIKI_BASE = "http://180.112.26.249/banksphereWiki/index.php";

    private WebResponse         m_lastResp     = null;
    private WebConversation     m_wc           = new WebConversation();

    public BKSWikiHelper() {
        CookieProperties.setDomainMatchingStrict(false);
        CookieProperties.setPathMatchingStrict(false);
        HttpUnitOptions.setScriptingEnabled(false);
        JavaScript.setThrowExceptionsOnError(false);
    }

    public boolean changeEditingText(String text, String summary, boolean minorEdit, String title) throws Exception {

        Tracer._debug("*** Changing edition content");

        WebForm form = m_lastResp.getFormWithName("editform");

        FormControl txtArea = form.getControlWithID("wpTextbox1");
        FormControl txtSummary = form.getControlWithID("wpSummary");
        FormControl chkMinorEdit = form.getControlWithID("wpMinoredit");
        SubmitButton btnSave = form.getSubmitButton("wpSave");

        HTMLTextAreaElementImpl htaei = (HTMLTextAreaElementImpl) txtArea.getNode();
        htaei.setValue(text);
        txtSummary.setAttribute("value", summary);
        chkMinorEdit.setState(minorEdit);

        m_lastResp = form.submit(btnSave);

        return _checkTitle(title);
    }

    public String getEditingText() throws Exception {
        WebForm form = m_lastResp.getFormWithName("editform");
        FormControl txtArea = form.getControlWithID("wpTextbox1");
        return txtArea.getText();
    }

    public String getText() {
        try {
            return m_lastResp.getText();
        } catch (Throwable th) {
            return "";
        }
    }

    public String getTitle() {
        try {
            return m_lastResp.getTitle();
        } catch (Throwable th) {
            return "";
        }
    }

    public boolean login(String usr, String pwd) throws Exception {

        Tracer._debug("*** Login into BKS Wiki");
        m_wc.clearContents();

        PostMethodWebRequest post = new PostMethodWebRequest(_composeURL("?title=Especial:Entrar&action=submitlogin&type=login&returnto=Portada"));
        m_lastResp = m_wc.getResponse(post);
        String token = _getLoginToken();
        if (token == null) {
            Tracer._warn("*** ERROR: Login session token couldn't be read from login page");
        }

        post.setParameter("wpName", usr);
        post.setParameter("wpPassword", pwd);
        post.setParameter("wpRemember", "1");
        post.setParameter("wpLoginAttempt", "Entrar");
        post.setParameter("wpLoginToken", token);
        m_lastResp = m_wc.getResponse(post);

        return _checkTitle(LOGIN_OK_TITLE);
    }

    public boolean loginOLD(String usr, String pwd) throws Exception {

        Tracer._debug("*** Login into BKS Wiki");
        m_wc.clearContents();

        // PostMethodWebRequest post = new PostMethodWebRequest(_composeURL("?title=Especial:Userlogin&amp;action=submitlogin&amp;type=login&amp;returnto=Portada"));
        PostMethodWebRequest post = new PostMethodWebRequest(_composeURL("?title=Especial:Entrar&action=submitlogin&type=login&returnto=Portada"));
        post.setParameter("wpName", usr);
        post.setParameter("wpPassword", pwd);
        post.setParameter("wpRemember", "1");
        post.setParameter("wpLoginattempt", "Registrarse/Entrar");
        m_lastResp = m_wc.getResponse(post);

        return _checkTitle(LOGIN_OK_TITLE);
    }

    public boolean navigateTo(String purl, String title) throws Exception {

        Tracer._debug("*** Navigating to '" + (title != null && title.length() > 0 ? "title=" + title : "url=" + purl) + "'");
        m_lastResp = m_wc.getResponse(_composeURL(purl));
        return _checkTitle(title);
    }

    private boolean _checkTitle(String expected) throws Exception {
        return _checkTitle(expected, m_lastResp.getTitle());
    }

    private boolean _checkTitle(String expected, String received) {
        if (expected == null || expected.length() == 0)
            return true;

        
        if (expected.replace(' ','_').compareToIgnoreCase(received.replace(' ','_')) == 0) {
            return true;
        } else {
            Tracer._error("*** ERROR: Navigation failed. Expected page's title was '" + expected + "' and the one received was: '" + received + "'");
            return false;
        }
    }

    private String _composeURL(String partialUrl) {
        String url;
        if (partialUrl.startsWith("/") || partialUrl.startsWith("\\") || partialUrl.startsWith("?")) {
            url = URL_WIKI_BASE + partialUrl;
        } else {
            url = URL_WIKI_BASE + "/" + partialUrl;
        }
        // Tracer._debug(url);
        return url;
    }

    private String _getLoginToken() {

        int p1, p2;

        String html = getText();
        p1 = html.indexOf("name=\"wpLoginToken\"");
        if (p1 == -1)
            return "";

        p1 = html.indexOf("value=\"", p1);
        if (p1 == -1)
            return "";
        p1 += 7;
        p2 = html.indexOf("\"", p1);
        if (p2 == -1)
            return "";

        return html.substring(p1, p2);
    }
}
