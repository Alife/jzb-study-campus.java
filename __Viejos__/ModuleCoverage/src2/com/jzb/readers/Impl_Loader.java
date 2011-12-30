/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.readers;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * @author PS00A501
 */
public abstract class Impl_Loader {

    private File m_compFile;
    protected XPath m_xp = XPathFactory.newInstance().newXPath();

    
    protected File getCompFile() {
        return m_compFile;
    }
    
    protected Impl_Loader() {
    }
    
    public Impl_Loader(File compFile) throws Exception {
        m_compFile=compFile;

    }
    private static Impl_Loader s_loaders[] = {
        new OI_Loader(),
        new OP_Loader(),
        new SQL_Loader(),
        new AAL_Loader(),
        new Java_Loader(),
        new IF_Loader(),
        new Facade_Loader(),
        new Scenario_Loader(),
        new Unknown_Loader()
    };

    public final static Impl_Loader newInstance(File compFile) throws Exception {
        
        String sample=readSample(compFile);
        for (int n = 0; n < s_loaders.length; n++) {
            if (s_loaders[n].acceptByContent(sample))
                return s_loaders[n].createLoader(compFile);
        }
        
        System.out.println("Unknown element type("+sample+"): " + compFile.getAbsolutePath());
        
        return new Unknown_Loader().createLoader(compFile);
    }

    private static String readSample(File compFile) throws Exception {
        FileReader fr = new FileReader(compFile);
        char cbuf[] = new char[100];
        int r=fr.read(cbuf);
        fr.close();
        return new String(cbuf,0,r);
    }
    
    public abstract boolean acceptByContent(String sample);
    
    public abstract Impl_Loader createLoader(File compFile) throws Exception;
    
    public ArrayList load(File afile) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder  = factory.newDocumentBuilder();
        Document document = builder.parse(afile);
        ArrayList list = new ArrayList();
        parseDOM(document, list);
        return list;
    }
        
    protected abstract void parseDOM(Document doc, ArrayList list) throws Exception;
    
};
