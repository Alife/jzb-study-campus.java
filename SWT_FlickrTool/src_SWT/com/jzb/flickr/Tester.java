/**
 * 
 */
package com.jzb.flickr;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.jzb.flickr.act.FlickrContext;
import com.jzb.flickr.xmlbean.ActTaskManager;
import com.jzb.flickr.xmlbean.IActTask;
import com.jzb.flickr.xmlbean.IActTaskManager;
import com.jzb.flickr.xmlbean.SysOutTracer;
import com.jzb.flickr.xmlbean.XMLActParser;

/**
 * @author n000013
 * 
 */
public class Tester {

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
            Tester me = new Tester();
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

        FlickrContext.init();

        InputStream is = this.getClass().getResourceAsStream("Test.xml");
        ArrayList<IActTask> atList = XMLActParser.parse(new SysOutTracer(), is);

        IActTaskManager manager = new ActTaskManager("prueba", new SysOutTracer(), null, 1);
        for (IActTask actTask : atList) {
            actTask.prepareExecution(null, null);
            manager.submitActTask(actTask);
        }
        manager.waitForFinishing();

    }

    public void doIt3(String[] args) throws Exception {

        TreeMap<String, Object> params = new TreeMap<String, Object>();

        params.put("api_key", "d184f81aad32a2ffa21b380cab557383");
        params.put("async", "0");
        params.put("auth_token", "72157608115575293-e0de90ebd804337b");
        params.put("is_family", "1");
        params.put("is_friend", "1");
        params.put("is_public", "0");
        params.put("tags", "\"uno\" \"PDF Printer\" \"dos\"");
        params.put("title", "Hello");
        params.put("api_sig", "6a84e878ed6c20fc300d89188616dc8d");
        params.put("photo", new File("C:\\TEMP\\BullZip\\PDF Printer\\hello.jpg"));

        HttpClient httpclient = new DefaultHttpClient();

        HttpHost proxy = new HttpHost("cache.sscc.banesto.es", 8080);
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        HttpPost httppost = new HttpPost("http://www.flickr.com/services/upload/");

        MultipartEntity reqEntity = new MultipartEntity();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object val = entry.getValue();
            ContentBody cbody;
            if (val instanceof File) {
                FileBody fb = new FileBody((File) val, "image/jpeg");
                cbody = fb;
            } else {
                cbody = new StringBody(val.toString());
            }
            reqEntity.addPart(entry.getKey(), cbody);
        }

        httppost.setEntity(reqEntity);

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);

        HttpEntity resEntity = response.getEntity();
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        if (resEntity != null) {
            System.out.println("Content Length: " + resEntity.getContentLength());
            System.out.println("Content Encoding: " + resEntity.getContentEncoding());
            System.out.println("Content Type: " + resEntity.getContentType());
            System.out.println("Chunked?: " + resEntity.isChunked());
        }
        if (resEntity != null) {
            InputStream is = resEntity.getContent();
            int len = (int) resEntity.getContentLength();
            while (len > 0) {
                System.out.print((char) is.read());
                len--;
            }
            if (len > 0) {
            } else {

                while (is.available() > 0) {
                    System.out.print((char) is.read());
                }
            }
            is.close();
        }
    }
}
