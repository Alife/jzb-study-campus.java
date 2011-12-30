/**
 * 
 */
package com.jzb.kk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.jzb.util.DefaultHttpProxy;

import HTTPClient.AuthorizationInfo;
import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;

/**
 * @author n63636
 * 
 */
public class GetHtml {

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
            GetHtml me = new GetHtml();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
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
        
        System.getProperties().put( "proxySet", "true" );
        System.getProperties().put( "proxyHost", "172.31.219.10" );
        System.getProperties().put( "proxyPort", "8080" );
        
        URL url = new URL("http://www.yahoo.com");
        URLConnection conn=url.openConnection();
        InputStream is = conn.getInputStream();
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        while(br.ready()) {
            String s=br.readLine();
            System.out.println(s);
        }
        br.close();
        
    }
    
    public void doIt2(String[] args) throws Exception {
        
        DefaultHttpProxy.setDefaultProxy();
        
        // Se establece el proxy por defecto para cualquier conexión. 
        // Se puede hacer al nivel de cada conexión 
        //HTTPConnection.setProxyServer("172.31.219.10",8080);

        // Se añade la información de autenticación básica que pide nuestro proxy
        // No se por qué el "realm" es "a"...
        //AuthorizationInfo.addBasicAuthorization("172.31.219.10",8080,"a","n63636","kk");

        // El HTTPClient tiene un problema con las cookies de Google. Esto no haría falta
        HTTPConnection.removeDefaultModule(HTTPClient.CookieModule.class);
        
        // Hace la llamada a través del proxy
        HTTPConnection con = new HTTPConnection(new URL("http://www.google.com"));
        HTTPResponse rsp=con.Get("/");
        // Pinta el resultado
        System.out.println(rsp.getStatusCode());
        System.out.println(rsp.getReasonLine());
        System.out.println(rsp.getText());
    }
}
