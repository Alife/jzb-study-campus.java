/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.bc.model.BException;
import com.jzb.bc.model.CtxBean;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class BException_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_refTypes;

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_refTypes = m_xp.compile("/*/businessExceptionContext//*/@type");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        // Hay un atributo llamado "event" que se usaria en OPs y OIs como referencia
        // a la BException. Pero como se usa debido al método y éste ya referencia a la BExcepcition
        // nos podemos ahorrar esa referencia

        Tracer._debug("Parsing BException");
        String exName = _getVegaElementName(doc, prjFolder, resource);
        String exPkg = resource.getParentFile().getName();

        BException bex = new BException(exPkg, exName, resource);
        NodeRegistry.add(bex);

        Tracer._debug("Parsing inner CtxBeans refs");
        _parseRefTypes(bex, doc, prjFolder, resource);

    }

    private void _parseRefTypes(BException bex, Document doc, File prjFolder, File resource) throws Exception {

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

                bex.getReferences().add(ref);
                ref.getReferees().add(bex);
            }

        }
    }

}
