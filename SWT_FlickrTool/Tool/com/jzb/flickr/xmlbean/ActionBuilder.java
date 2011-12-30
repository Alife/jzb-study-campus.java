/**
 * 
 */
package com.jzb.flickr.xmlbean;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("unchecked")
public class ActionBuilder {

    private static final String     ACTCLASS_ATTRIB = "actClass";

    private Class<IAction>          m_actClass;
    private HashMap<String, String> m_attribs       = new HashMap<String, String>();
    private ITracer                 m_tracer;

    public ActionBuilder(ITracer tracer, Node node) throws Exception {

        m_tracer = tracer;
        String clazz = _getAttrValue(node, ACTCLASS_ATTRIB);
        m_actClass = (Class<IAction>) Class.forName(clazz);

        NamedNodeMap attribs = node.getAttributes();
        for (int n = 0; n < attribs.getLength(); n++) {
            Attr attr = (Attr) attribs.item(n);
            m_attribs.put(attr.getName(), attr.getValue());
        }

        m_attribs.remove(ACTCLASS_ATTRIB);
    }

    public IAction newInstance(IAction parentAct, Object parentData) throws Exception {
        Constructor ctor = m_actClass.getConstructor(ITracer.class);
        IAction obj = (IAction) ctor.newInstance(m_tracer);
        for (Map.Entry<String, String> entry : m_attribs.entrySet()) {
            JavaBeanUtils.setPropValue(obj, entry.getKey(), _resolveVal(parentAct, parentData, entry.getValue()));
        }
        return obj;
    }

    private String _getAttrValue(Node node, String name) {
        NamedNodeMap attribs = node.getAttributes();
        Attr attrib = (Attr) attribs.getNamedItem(name);
        if (attrib == null)
            throw new NullPointerException("Attribute '" + name + "' not found in node '" + node.getNodeName() + "'");
        return attrib.getValue();
    }

    private Object _resolveVal(IAction parentAct, Object parentData, String strVal) throws Exception {

        Object objVal = strVal;

        if (strVal.equals("$item")) {
            objVal = parentData;
        } else if (strVal.startsWith("$parent.")) {
            objVal = JavaBeanUtils.getPropValue(parentAct, strVal.substring(8));
        } else if (strVal.startsWith("@")) {
            objVal = JavaBeanUtils.getPropValue(parentData, strVal.substring(1));
        }

        return objVal;
    }

}
