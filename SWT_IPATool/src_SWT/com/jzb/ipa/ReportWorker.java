/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import com.jzb.futil.FileExtFilter;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class ReportWorker extends BaseWorker {

    public ReportWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void createReport(final String baseFolderStr, final boolean recurseFolders) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("** Creating report for IPA files from base folder: '" + baseFolderStr + "'");
                _createReport(new File(baseFolderStr), recurseFolders);
                Tracer._info("** Report creation done.");
                return null;
            }
        };

        _makeCall(baseFolderStr, callable);
    }

    private void _createReport(final File afolder, final boolean recurseFolders) {

        StringBuffer sb = new StringBuffer();
        int index = 1;

        try {
            Tracer._debug("** Creating report for IPA files from folder: '" + afolder + "'");
            for (File afile : afolder.listFiles(new FileExtFilter(true, "ipa"))) {
                if (!afile.isDirectory()) {
                    _generateHTMLForFile(index++, afile, sb);
                }
            }

            if (!m_justChecking && sb.length() > 0) {
                PrintStream ps = new PrintStream(new FileOutputStream(new File(afolder, "report.html"), false));
                ps.println(_printHTMLBegin());
                ps.println(sb);
                ps.println(_printHTMLEnd());
                ps.close();
            }

            // Recurse folders if specified
            if (recurseFolders) {
                for (File afile : afolder.listFiles()) {
                    if (afile.isDirectory()) {
                        _createReport(afile, recurseFolders);
                    }
                }
            }

        } catch (Exception ex) {
            Tracer._error("Error generating report file", ex);
        }
    }

    private void _generateHTMLForFile(int index, File ipaFile, StringBuffer sb) {

        File jpgFile = new File(ipaFile.getParentFile(), ipaFile.getName().substring(0, ipaFile.getName().length() - 3) + "jpg");

        sb.append("    <tr>\n");
        sb.append("        <td>");
        sb.append("<a href=\"");
        sb.append(ipaFile);
        sb.append("\"><img id=\"IMG_" + index + "\" src=\"");
        sb.append(jpgFile.getName());
        sb.append("\" file=\"");
        sb.append(ipaFile);
        sb.append("\" width=\"128\" height=\"128\" border=0 alt=\"\"></a></td>\n");

        sb.append("<td><input type=\"checkbox\" id=\"CHK_" + index + "\" onclick=\"setText()\"></td>");

        sb.append("        <td>");
        sb.append(ipaFile.getName());
        sb.append("</td>\n");

        sb.append("    </tr>\n");
    }

    private String _printHTMLBegin() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>IPA folder report</title>\n");

        Reader fr = new InputStreamReader(getClass().getResourceAsStream("reportScript.txt"));
        while (fr.ready()) {
            sb.append((char) fr.read());
        }
        fr.close();

        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<table>\n");
        return sb.toString();
    }

    private String _printHTMLEnd() {
        StringBuffer sb = new StringBuffer();
        sb.append("</table>\n");
        sb.append("<textarea id=\"chkTxt\" rows=\"20\" cols=\"60\"></textarea>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb.toString();
    }
}
