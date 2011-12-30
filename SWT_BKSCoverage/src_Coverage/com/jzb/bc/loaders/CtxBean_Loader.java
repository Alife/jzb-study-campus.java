/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.bc.model.CtxBean;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class CtxBean_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_refTypes;

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_refTypes = m_xp.compile("/*/collectionList//*/@type");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        Tracer._debug("Parsing CtxBean");
        String ctxBeanName = _getVegaElementName(doc, prjFolder, resource);
        String ctxBeanPkg = resource.getParentFile().getName();

        CtxBean ctxBean = new CtxBean(ctxBeanPkg, ctxBeanName, resource);
        NodeRegistry.add(ctxBean);

        Tracer._debug("Parsing inner CtxBeans refs");
        _parseRefTypes(ctxBean, doc, prjFolder, resource);

    }

    private void _parseRefTypes(CtxBean ctxBean, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_refTypes.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String typeName = nl.item(n).getNodeValue();

            if (typeName.contains(".")) {

                CtxBean ref = (CtxBean) NodeRegistry.getByUID(typeName);
                if (ref == null) {
                    // Creates a temporary phantom node
                    ref = CtxBean.createPhantom(typeName, resource);
                    NodeRegistry.add(ref);
                }

                ctxBean.getReferences().add(ref);
                ref.getReferees().add(ctxBean);
            }

        }
    }

}
