package com.jzb.dxc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DivxCleaner {

    private int                    m_counter    = 1;
    private File                   m_dstFolder;
    private HashSet<String>        m_exts       = new HashSet();
    private ArrayList<IFilterRule> m_filters    = new ArrayList();
    private ArrayList<File>        m_srcFolders = new ArrayList();

    public static void main(String[] args) {
        try {
            System.out.println("***** EXECUTION STARTED *****");
            DivxCleaner me = new DivxCleaner();
            long t1 = System.currentTimeMillis();
            me.doIt(args);
            long t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** EXECUTION FAILED *****");
            th.printStackTrace(System.out);
            if (Tracer.isInit())
                Tracer.error("***** EXECUTION FAILED *****", th);
        }
    }

    public void doIt(String[] args) throws Exception {
        loadConfig(args);

        Tracer.init(new File(this.m_dstFolder, "_traces"));

        for (File srcFolder : this.m_srcFolders) {
            processSrcFolder(srcFolder);
        }
        processDstFolder(this.m_dstFolder);
    }

    private String filterName(String name) {
        String newName = name;

        GenericFilterRule.lastSerieNameMatched = null;

        for (IFilterRule f : this.m_filters) {
            try {
                newName = f.filter(newName + " ");
                newName = newName.toLowerCase().trim();
                if (newName.length() == 0)
                    newName = name;
            } catch (Exception ex) {
                Tracer.error("Error filtering name: " + name, ex);
                newName = name;
            }
        }

        return newName;
    }

    private InputStream getConfigFile(String[] args) throws Exception {
        if (args.length > 0) {
            File fIn = new File(args[0]);
            if (fIn.exists()) {
                return new FileInputStream(fIn);
            }
            throw new Exception("Passed configuration XML file doesn't exist: " + args[0]);
        }

        throw new Exception("'config.xml' file must be passed as a parameter");
    }

    private File getDstFolder(String newName) {
        File df;
        if ((Character.isDigit(newName.charAt(0))) && (newName.charAt(4) == '-')) {
            if (GenericFilterRule.lastSerieNameMatched != null)
                df = new File(this.m_dstFolder, "Series/" + GenericFilterRule.lastSerieNameMatched);
            else
                df = new File(this.m_dstFolder, "Series");
        } else {
            df = this.m_dstFolder;
        }
        return df;
    }

    private String getExtension(String name) {
        int p = name.lastIndexOf('.');
        if (p > 0) {
            return name.substring(p);
        }
        return "";
    }

    private String getName(String name) {
        int p = name.lastIndexOf('.');
        if (p > 0) {
            return name.substring(0, p);
        }
        return name;
    }

    private void loadConfig(String[] args) throws Exception {
        InputStream is = getConfigFile(args);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(is));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        String val = xpath.evaluate("/config/process/exts/text()", doc);
        StringTokenizer st = new StringTokenizer(val, ";");
        while (st.hasMoreElements()) {
            this.m_exts.add("." + st.nextToken().trim().toLowerCase());
        }

        val = xpath.evaluate("/config/process/dstPath/text()", doc);
        this.m_dstFolder = new File(val);
        if (!this.m_dstFolder.exists()) {
            throw new Exception("Error, destination folder doesn't exist: " + val);
        }

        NodeList nlist = (NodeList) xpath.evaluate("/config/process/srcPath/text()", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);
            val = node.getNodeValue();
            File of = new File(val);
            if (!of.exists())
                System.out.println("Warning, origin folder doesn't exist: " + val);
            else {
                this.m_srcFolders.add(of);
            }

        }

        nlist = (NodeList) xpath.evaluate("/config/filterRules/filter", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);
            NamedNodeMap attrs = node.getAttributes();
            GenericFilterRule.MODE mode = GenericFilterRule.getMode(attrs.getNamedItem("mode").getNodeValue());
            String regexp = attrs.getNamedItem("regexp").getNodeValue();
            String data = attrs.getNamedItem("data").getNodeValue();
            GenericFilterRule filter = new GenericFilterRule(mode, regexp, data);
            this.m_filters.add(filter);
        }
    }

    private boolean phisycalOSMove(File org, File dst) {
        try {
            String[] pCmd = { "mv", "-n", "-v", "-T", org.toString(), dst.toString() };

            Process proc = Runtime.getRuntime().exec(pCmd);

            int returnVal = proc.waitFor();

            return returnVal == 0;
        } catch (Exception e) {
            Tracer.error("Error moving file '" + org + "' to '" + dst + "'", e);
        }
        return false;
    }

    private void processDstFolder(File dstFolder) {
        
        Tracer.trace("\n*** Filtering again output folder's files: %s\n", new Object[] { dstFolder.getAbsolutePath() });

        for (File f : dstFolder.listFiles()) {
            String ext = getExtension(f.getName());
            String name = getName(f.getName());

            if (f.isDirectory()) {
                processDstFolder(f);
            } else {
                if (!this.m_exts.contains(ext.toLowerCase()))
                    continue;
                String newName = filterName(name);
                File df = getDstFolder(newName);

                if ((name.equals(newName)) && (df.equals(f.getParentFile()))) {
                    continue;
                }
                
                Tracer.trace("renaming file: " + f.getAbsolutePath());

                File newFile = new File(df, newName + ext);
                if (newFile.exists()) {
                    newName = newName + "_" + System.currentTimeMillis() + "_" + this.m_counter++;
                    newFile = new File(df, newName + ext);
                }

                Tracer.trace("    " + newFile);
                
                newFile.getParentFile().mkdirs();
                if (!f.renameTo(newFile))
                    Tracer.error("Error renaming file '%s' to '%s'", new Object[] { f, newFile });
            }
        }
    }

    private void processSrcFolder(File srcFolder) {
        for (File f : srcFolder.listFiles()) {
            String ext = getExtension(f.getName());
            String name = getName(f.getName());

            if (f.isDirectory()) {
                processSrcFolder(f);
            } else {
                if (!this.m_exts.contains(ext.toLowerCase()))
                    continue;
                
                Tracer.trace("Moving file: " + f.getAbsolutePath());

                String newName = filterName(name);

                File df = getDstFolder(newName);
                
                File newFile = new File(df, newName + ext);
                if (newFile.exists()) {
                    newName = newName + "_" + System.currentTimeMillis() + "_" + this.m_counter++;
                    newFile = new File(df, newName + ext);
                }

                Tracer.trace("    " + newFile);
                newFile.getParentFile().mkdirs();
                if (!phisycalOSMove(f, newFile))
                    Tracer.error("Error moving file '%s' to '%s'", new Object[] { f, newFile });
            }
        }
    }
}