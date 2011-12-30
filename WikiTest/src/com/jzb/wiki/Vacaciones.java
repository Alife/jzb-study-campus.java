/**
 * 
 */
package com.jzb.wiki;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.util.Des3Encrypter;
import com.jzb.wiki.util.BKSWikiHelper;
import com.jzb.wiki.util.NameValuePair;

/**
 * @author n000013
 * 
 */
public class Vacaciones {

    private static final SimpleDateFormat s_df = new SimpleDateFormat("dd/MM/yyyy");

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
            Vacaciones me = new Vacaciones();
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

        System.out.println("*** Reading info from excel file");
        BKSWikiHelper helper = new BKSWikiHelper();
        helper.login(Des3Encrypter.decryptStr("PjN1Jb0t6CY0Eo9zcFVohw=="), Des3Encrypter.decryptStr("PjN1Jb0t6CYD25gJXVCyxw=="));
        helper.navigateTo("?title=VV_BKS&action=edit", "Editando VV BKS - BanksphereWiki");
        // helper.changeEditingText(text, "Cambio automático desde excel", false, "Guardias BKS Core - BanksphereWiki");
        // _parseWikiData(helper.getEditingText());

        _useRE(helper.getEditingText());
    }

    private void _parseWikiData(String text) throws Exception {
        BufferedReader br = new BufferedReader(new StringReader(text));
        while (br.ready()) {
            String line = br.readLine();
            if (line == null)
                break;
            if (line.contains("{{VV_ADD_LINE")) {
                _parseItem(br);
            }
        }
        br.close();
    }

    private String _stripComments(String text) throws Exception {
        int p1=0, p2 = 0, p3 = 0;

        StringBuffer sb= new StringBuffer();
        for (;;) {
            p1 = text.indexOf("<!--", p2);
            if (p1 == -1) {
                sb.append(text.substring(p3));
                break;
            }

            p2 = text.indexOf("-->", p1);
            if (p2 == -1) {
                // comentario sin final???
                break;
            }

            p2+=3;
            sb.append(text.substring(p3, p1));
            p3 = p2;
        }

        System.out.println(sb);

        return sb.toString();
    }

    private void _useRE(String text) throws Exception {
        String re = "\\{\\{VV_ADD_LINE\\s*\\|\\s*Nombre\\s*=([^\\n]*)\\s*\\|\\s*Julio\\s*=([^\\n]*)\\s*\\|\\s*Agosto\\s*=([^\\n]*)\\s*\\|\\s*Septiembre\\s*=(.*)\\s*\\}\\}";

        text = _stripComments(text);

        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(text);
        for (;;) {
            if (!m.find())
                break;
            System.out.println("-------------------------------------");
            for (int n = 1; n <= m.groupCount(); n++) {
                System.out.println(m.group(n));
            }
        }

    }

    private void _parseItem(BufferedReader br) throws Exception {

        for (;;) {
            String line = br.readLine();

            if (line == null || (line.contains("}}") && !line.contains("=")))
                break;

            if (line.contains("=")) {
                NameValuePair nvp = new NameValuePair();
                nvp.parse(line, '=', new char[] { '|' });
                System.out.println(nvp);
            }
        }

    }
}
