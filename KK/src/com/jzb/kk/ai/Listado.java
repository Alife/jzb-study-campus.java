/**
 * 
 */
package com.jzb.kk.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author n63636
 * 
 */
public class Listado {

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
            Listado me = new Listado();
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

        HashMap<String, String> appInfo = new HashMap<String, String>();
        File fin = new File("C:\\WKSPs\\Consolidado\\KK\\src\\com\\jzb\\kk\\ai\\Listado2.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fin), "UTF-8"));

        while (br.ready()) {
            String s, str = br.readLine();

            StringTokenizer st = new StringTokenizer(str, "\t");
            if (st.hasMoreTokens()) {
                String appID = st.nextToken();
                st.nextToken();
                st.nextToken();
                st.nextToken();
                String appDelete = st.nextToken();
                System.out.println(appDelete + "\t" + appID);
                appInfo.put(appID, appDelete);
            }

        }
        br.close();

        File ipaFolder = new File("C:\\JZarzuela\\iPhone\\IPAs");
        File destFolder = new File("C:\\JZarzuela\\iPhone\\moved_IPAs");

        System.out.println();
        _moveIPAs(ipaFolder, destFolder, ipaFolder, appInfo);
    }

    private void _moveIPAs(File ipaFolder, File destFolder, File folder, HashMap<String, String> appInfo) throws Exception {

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _moveIPAs(ipaFolder, destFolder, f, appInfo);
            } else {
                String pkg = _getIPAPkg(f.getName());
                if (pkg != null) {
                    String delete = appInfo.get(pkg);
                    if (delete == null) {
                        // System.out.println("Warning no 'delete' info for: " + f);
                    } else {
                        if (delete.equals("yes")) {
                            System.out.println(delete +"\t"+f);
                            File newfile = new File(destFolder, f.getAbsolutePath().substring(ipaFolder.getAbsolutePath().length()));
                            newfile.getParentFile().mkdirs();
                            if(!f.renameTo(newfile)) {
                                System.out.println("  **> ERROR MOVING FILE");
                            }
                        } else {
                            System.out.println(delete +"\t"+f);
                        }
                    }
                }
            }
        }
    }

    private String _getIPAPkg(String fname) {
        int p1 = fname.indexOf("_PK[");
        if (p1 <= 0)
            return null;

        int p2 = fname.indexOf("]_V");
        return fname.substring(p1 + 4, p2);
    }

    public void doIt2(String[] args) throws Exception {

        String appID = "<vacio>";
        String appVersion = "<vacio>";
        String appFullName = "<vacio>";
        String appSize = "<vacio>";

        File fin = new File("C:\\WKSPs\\Consolidado\\KK\\src\\com\\jzb\\kk\\ai\\Listado.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fin), "UTF-8"));

        while (br.ready()) {
            String s, str = br.readLine();

            if (str.length() == 0) {
                System.out.println(appID + "; " + appFullName + "; " + appVersion + "; " + appSize);
                appID = appVersion = appFullName = appSize = "<vacio>";
            }

            if ((s = _parseToken(str, "Id")) != null) {
                appID = s;
                continue;
            }

            if ((s = _parseToken(str, "Versión")) != null) {
                appVersion = s;
                continue;
            }

            if ((s = _parseToken(str, "Tamaño")) != null) {
                appSize = s;
                continue;
            }

            if ((s = _parseToken(str, "Nombre")) != null) {
                appFullName = s;
                continue;
            }

        }
        br.close();

    }

    DecimalFormat df = new DecimalFormat("0.00");

    private String _parseToken(String str, String token) {
        if (str.startsWith(token + " : ")) {
            int p = token.length() + 3;

            if (token.equalsIgnoreCase("Tamaño")) {
                double size = Double.parseDouble(str.substring(p, str.length() - 3));
                if (str.substring(str.length() - 2).equals("Ko")) {
                    size /= 1000;
                } else if (str.substring(str.length() - 2).equals("Go")) {
                    size *= 1000;
                }
                return df.format(size);
            } else {
                return str.substring(p);
            }
        }
        return null;
    }
}
