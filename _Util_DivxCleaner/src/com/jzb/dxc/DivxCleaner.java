/**
 * 
 */
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

import com.jzb.dxc.GenericFilterRule.MODE;

/**
 * @author n63636
 * 
 */
public class DivxCleaner {

    private ArrayList<IFilterRule> m_filters    = new ArrayList<IFilterRule>();
    private HashSet<String>        m_exts       = new HashSet<String>();
    private File                   m_dstFolder;
    private ArrayList<File>        m_srcFolders = new ArrayList<File>();
    private int                    m_counter    = 1;

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** EXECUTION STARTED *****");
            DivxCleaner me = new DivxCleaner();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** EXECUTION FAILED *****");
            th.printStackTrace(System.out);
            if (Tracer.isInit())
                Tracer.error("***** EXECUTION FAILED *****", th);
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

        loadConfig(args);
        Tracer.init(new File(m_dstFolder, "_traces"));

        for (File srcFolder : m_srcFolders) {
            processSrcFolder(srcFolder);
        }
        processDstFolder(m_dstFolder);
    }

    private void processDstFolder(File dstFolder) {

        Tracer.trace("\n*** Filtering again output folder's files: %s\n", dstFolder.getAbsolutePath());

        for (File f : dstFolder.listFiles()) {

            String ext = getExtension(f.getName());
            String name = getName(f.getName());

            if (f.isDirectory()) {
                processDstFolder(f);
            } else if (m_exts.contains(ext)) {

                String newName = filterName(name);
                File df = getDstFolder(newName);

                if (name.equals(newName) && df.equals(f.getParentFile()))
                    continue;

                Tracer.trace("renaming file: " + f.getAbsolutePath());

                File newFile = new File(df, newName + ext);
                newFile.getParentFile().mkdirs();

                if (newFile.exists()) {
                    newName = newName + "_" + System.currentTimeMillis() + "_" + (m_counter++);
                    newFile = new File(df, newName + ext);
                }

                Tracer.trace("    " + newName);

                if (!f.renameTo(newFile)) {
                    Tracer.error("Error renaming file to '%s': %s", newName, f.getAbsolutePath());
                }
            }
        }
    }

    private void processSrcFolder(File srcFolder) { // throws Exception {

        for (File f : srcFolder.listFiles()) {

            String ext = getExtension(f.getName());
            String name = getName(f.getName());

            if (!f.isDirectory() && m_exts.contains(ext)) {

                Tracer.trace("Moving file: " + f.getAbsolutePath());

                String newName = filterName(name);

                File df = getDstFolder(newName);
                File newFile = new File(df, newName + ext);
                newFile.getParentFile().mkdirs();

                if (newFile.exists()) {
                    newName = newName + "_" + System.currentTimeMillis() + "_" + (m_counter++);
                    newFile = new File(df, newName + ext);
                }

                Tracer.trace("    " + newName);

                if (!f.renameTo(newFile)) {
                    Tracer.error("Error moving file to '%s': %s", newName, f.getAbsolutePath());
                }
            }
        }
    }

    private String filterName(String name) {

        String newName = name;

        for (IFilterRule f : m_filters) {
            try {
                newName = f.filter(newName + " ");
                newName = newName.toLowerCase().trim();
                if (newName.length() == 0) {
                    newName = name;
                }
            } catch (Exception ex) {
                Tracer.error("Error filtering name: " + name, ex);
                newName = name;
            }
        }

        return newName;
    }

    private String getName(String name) {

        int p = name.lastIndexOf('.');
        if (p > 0) {
            return name.substring(0, p);
        } else {
            return name;
        }
    }

    private String getExtension(String name) {

        int p = name.lastIndexOf('.');
        if (p > 0) {
            return name.substring(p);
        } else {
            return "";
        }
    }

    private void loadConfig(String[] args) throws Exception {

        InputStream is = getConfigFile();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(is));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        // Carga las extensiones y los directorios *****************************************
        String val = xpath.evaluate("/config/process/exts/text()", doc);
        StringTokenizer st = new StringTokenizer(val, ";");
        while (st.hasMoreElements()) {
            m_exts.add("." + st.nextToken().trim());
        }

        val = xpath.evaluate("/config/process/dstPath/text()", doc);
        m_dstFolder = new File(val);
        if (!m_dstFolder.exists()) {
            throw new Exception("Error, destination folder doesn't exist: " + val);
        }

        NodeList nlist = (NodeList) xpath.evaluate("/config/process/srcPath/text()", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);
            val = node.getNodeValue();
            File of = new File(val);
            if (!of.exists()) {
                System.out.println("Warning, origin folder doesn't exist: " + val);
            } else {
                m_srcFolders.add(of);
            }
        }

        // Carga los filtros ******************************************************
        nlist = (NodeList) xpath.evaluate("/config/filterRules/filter", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);
            NamedNodeMap attrs = node.getAttributes();
            MODE mode = GenericFilterRule.getMode(attrs.getNamedItem("mode").getNodeValue());
            String regexp = attrs.getNamedItem("regexp").getNodeValue();
            String data = attrs.getNamedItem("data").getNodeValue();
            GenericFilterRule filter = new GenericFilterRule(mode, regexp, data);
            m_filters.add(filter);
        }
    }

    private File getDstFolder(String newName) {
        File df;
        if (Character.isDigit(newName.charAt(0)) && newName.charAt(4) == '-') {
            df = new File(m_dstFolder, "Series");
        } else {
            df = m_dstFolder;
        }
        return df;
    }

    private InputStream getConfigFile() throws Exception {

        if (ClassLoader.getSystemResource("config.xml") != null) {
            return ClassLoader.getSystemResourceAsStream("config.xml");
        }

        String s = System.getProperty("java.class.path");
        StringTokenizer st2 = new StringTokenizer(s, ";");
        while (st2.hasMoreTokens()) {
            String ss = st2.nextToken().toLowerCase().trim();
            if (ss.endsWith(".exe")) {
                File f = new File(ss).getParentFile();
                f = new File(f, "config.xml");
                if (f.exists()) {
                    return new FileInputStream(f);
                }

            }
        }

        throw new Exception("Error, file 'Config.xml' not found");

    }
}
