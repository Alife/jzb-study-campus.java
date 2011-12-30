/**
 * 
 */
package com.jzb.flickr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.jzb.flickr.xmlbean.SysOutProgressMonitor;

/**
 * @author n000013
 * 
 */
public class SimpleUploader {

    private static final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private AuthContext         m_autCxt;

    public SimpleUploader(AuthContext autCxt) {
        m_autCxt = autCxt;
    }

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            SimpleUploader me = new SimpleUploader(new AuthContext());
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        // System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        // System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        // System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");
        // System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
        // System.setErr(System.out);

        UploadInfo uinfo = new UploadInfo("UnaPrueba");
        uinfo.setTags("Uno", "Dos", "Mas Otro");

        for (int n = 0; n < 100; n++) {
            try {
                upload(new File("C:\\TEMP\\BullZip\\PDF Printer\\hello.jpg"), uinfo);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public void upload(File imageFile, UploadInfo info) throws Exception {

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.TRUE);
        httpclient.getParams().setParameter("http.socket.linger", new Integer(1));
        httpclient.getParams().setParameter("http.connection.stalecheck", Boolean.FALSE);

        // ---------- Multi-part request
        MultipartEntity reqEntity = new MultipartEntity();
        for (Map.Entry<String, String> entry : _getParams(info).entrySet()) {
            reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
        }

        reqEntity.addPart("photo", new FileBody2(imageFile, "image/jpeg"));

        // ---------- HttpClient
        HttpHost proxy = new HttpHost("cache.sscc.banesto.es", 8080);
        // httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        // ---------- HttpPost Method
         HttpPost httppost = new HttpPost("http://www.flickr.com/services/upload/");
        //HttpPost httppost = new HttpPost("http://127.0.0.1:9999/pepe");
        httppost.setEntity(reqEntity);
        httppost.setHeader("Connection", "close");

        // ---------- Make server request
        System.out.println("**> Executing request " + httppost.getRequestLine());
        HttpContext localContext = new BasicHttpContext();
        HttpResponse response;
        try {

            ResponseHandler<HttpResponse> rh = new ResponseHandler<HttpResponse>() {

                public HttpResponse handleResponse(HttpResponse resp) throws ClientProtocolException, IOException {
                    return resp;
                }
            };

            response = httpclient.execute(httppost, rh, localContext);

        } finally {
            localContext.toString();
            //http.request_sent
        }

        // ---------- Process the response
        HttpEntity respEntity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            InputStream is = respEntity.getContent();
            _parseResponse(is);
            is.close();
            respEntity.consumeContent();
        } else {
            System.out.println("**> Error in response: " + response.getStatusLine());
            if (respEntity != null) {
                respEntity.consumeContent();
            }
        }

        // httppost.abort();

        // ---------- Terminate everything
        httpclient.getConnectionManager().shutdown();

    }

    private String _getAPI_Signature(Map<String, String> params) throws Exception {

        HashSet<String> ignoreParameters = new HashSet<String>();
        ignoreParameters.add("photo");

        StringBuffer buffer = new StringBuffer();
        buffer.append(m_autCxt.getSharedSecret());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!ignoreParameters.contains(entry.getKey())) {
                buffer.append(entry.getKey());
                buffer.append(entry.getValue());
            }
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        return _toHexString(md.digest(buffer.toString().getBytes("UTF-8")));
    }

    private Map<String, String> _getParams(UploadInfo info) throws Exception {

        TreeMap<String, String> params = new TreeMap<String, String>();

        params.put("api_key", m_autCxt.getAPI_Key());
        params.put("auth_token", m_autCxt.getAuthToken());

        params.putAll(info.getParams());

        params.put("api_sig", _getAPI_Signature(params));

        return params;
    }

    private String _toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            // look up high nibble char
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);

            // look up low nibble char
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    private void _parseResponse(InputStream is) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);
        is.close();

        XPathFactory xpathFact = XPathFactory.newInstance();
        XPath xp = xpathFact.newXPath();

        String status = (String) xp.evaluate("/rsp/@stat", doc, XPathConstants.STRING);
        String errCode = (String) xp.evaluate("/rsp/err/@code", doc, XPathConstants.STRING);
        String errMsg = (String) xp.evaluate("/rsp/err/@msg", doc, XPathConstants.STRING);
        String photoid = (String) xp.evaluate("/rsp/photoid/text()", doc, XPathConstants.STRING);

        System.out.println("status='" + status + "'");
        System.out.println("errCode='" + errCode + "'");
        System.out.println("errMsg='" + errMsg + "'");
        System.out.println("photoid='" + photoid + "'");

        _writeDoc(doc, System.out);
    }

    private void _writeDoc(Document doc, OutputStream out) throws IOException {
        // XXX note that this may fail to write out namespaces correctly if the
        // document
        // is created with namespaces and no explicit prefixes; however no code in
        // this package is likely to be doing so
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DocumentType dt = doc.getDoctype();
            if (dt != null) {
                String pub = dt.getPublicId();
                if (pub != null) {
                    t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, pub);
                }
                t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.getSystemId());
            }
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // NOI18N
            t.setOutputProperty(OutputKeys.INDENT, "yes"); // NOI18N
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // NOI18N
            Source source = new DOMSource(doc);
            Result result = new StreamResult(out);
            t.transform(source, result);
        } catch (Exception e) {
            throw (IOException) new IOException(e.toString()).initCause(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw (IOException) new IOException(e.toString()).initCause(e);
        }
    }
}
