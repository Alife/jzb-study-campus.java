/**
 * 
 */
package com.jzb.flickr.xmlbean;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author n000013
 * 
 */
public class XMLActParser {

    private static Properties s_nodeClasses;

    static {
        _init();
    }

    public static ArrayList<IActTask> parse(ITracer tracer, InputStream is) throws Exception {

        DocumentBuilderFactory dbfact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfact.newDocumentBuilder();
        Document doc = docBuilder.parse(is);
        return parseNodes(tracer, doc.getFirstChild());
    }

    public static ArrayList<IActTask> parseNodes(ITracer tracer, Node parentNode) throws Exception {

        ArrayList<IActTask> tasks = new ArrayList<IActTask>();

        NodeList nlist = parentNode.getChildNodes();
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            String className = s_nodeClasses.getProperty(node.getNodeName());
            if (className == null)
                throw new NullPointerException("Class not defined for ActTask: " + node.getNodeName());
            Constructor ctor = Class.forName(className).getConstructor(ITracer.class);
            IActTask actTask = (IActTask) ctor.newInstance(tracer);
            actTask.initialize(node);
            tasks.add(actTask);
        }

        return tasks;
    }

    private static void _init() {
        if (s_nodeClasses == null) {
            try {
                s_nodeClasses = new Properties();
                s_nodeClasses.load(XMLActParser.class.getResourceAsStream("XMLActParser.properties"));
            } catch (IOException ex) {
                throw new RuntimeException("Error reading properties", ex);
            }
        }
    }
}
