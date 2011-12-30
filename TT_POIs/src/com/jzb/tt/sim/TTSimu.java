/**
 * 
 */
package com.jzb.tt.sim;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author n63636
 * 
 */
public class TTSimu {

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
            TTSimu me = new TTSimu();
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
        HashMap<String, String> tokens = _getAuthToken();
        _doRequest(tokens.get("Auth"));
    }

    private void _doRequest(String AuthToken) throws Exception {

        URL url = new URL("http://maps.google.com/maps/feeds/maps/default/full");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); // proxy
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("GET");
        // urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        urlConnection.setRequestProperty("Authorization", "GoogleLogin auth=" + AuthToken);
        urlConnection.setRequestProperty("User-Agent", "listAllMaps GMaps-Java/null GData-Java/null(gzip)");
        // urlConnection.setRequestProperty("Accept-Encoding", "gzip");
        urlConnection.setRequestProperty("GData-Version", "2.0");

        // Retrieve the output
        InputStream inputStream = null;
        StringBuilder outputBuilder = new StringBuilder();
        try {
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }

            String string;
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while (null != (string = reader.readLine())) {
                    outputBuilder.append(string).append('\n');
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        String out = outputBuilder.toString();
        System.out.println(out);

    }

    private HashMap<String, String> _getAuthToken() throws Exception {

        //URL url = new URL("https://www.google.com/accounts/ClientLogin");
        URL url = new URL("http://127.0.0.1:8080/accounts/ClientLogin");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); // proxy
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // Add user-agent string "<service_name> GData-Java/x.x.x"
        urlConnection.setRequestProperty("User-Agent", "local GData-Java/null");

        String content = "source=listAllMaps&Email=jzarzuela%40gmail.com&accountType=HOSTED_OR_GOOGLE&service=local&Passwd=%23webweb1971";
        OutputStream outputStream = null;
        try {
            outputStream = urlConnection.getOutputStream();
            outputStream.write(content.toString().getBytes("utf-8"));
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

        // Retrieve the output
        InputStream inputStream = null;
        StringBuilder outputBuilder = new StringBuilder();
        try {
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }

            String string;
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while (null != (string = reader.readLine())) {
                    outputBuilder.append(string).append('\n');
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        String out = outputBuilder.toString();
        return _parseTokens(out);
    }

    private HashMap<String, String> _parseTokens(String cad) throws Exception {

        HashMap<String, String> tokens = new HashMap<String, String>();
        StringTokenizer tk1 = new StringTokenizer(cad, "\n");
        while (tk1.hasMoreTokens()) {
            String tknLine = tk1.nextToken();
            StringTokenizer tk2 = new StringTokenizer(tknLine, "=");
            String key = tk2.nextToken().trim();
            String value = tk2.nextToken().trim();
            tokens.put(key, value);
        }

        return tokens;
    }
}
