/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.bc.model.B_Ope;
import com.jzb.bc.model.CMethod;
import com.jzb.bc.model.CtxBean;
import com.jzb.bc.model.I_Ope;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class OI_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_FCalls;
    private XPathExpression m_xp_MethodCalls;
    private XPathExpression m_xp_OICalls;
    private XPathExpression m_xp_refTypes;

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_FCalls = m_xp.compile("/internalOperation/stateList/FacadeComponentState");
        m_xp_MethodCalls = m_xp.compile("/internalOperation/stateList/ComponentState");
        m_xp_OICalls = m_xp.compile("/internalOperation/stateList/OIState");
        m_xp_refTypes = m_xp.compile("/*/operationContext//*/@type");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        Tracer._debug("Adding OI node");
        String oiName = _getVegaElementName(doc, prjFolder, resource);
        String alName = resource.getParentFile().getName();
        I_Ope oi = new I_Ope(oiName, alName, resource);
        NodeRegistry.add(oi);

        Tracer._debug("Parsing CtxBeans, IOCalls, FCalls and MethodCalls");
        _parse_RefTypes(oi, doc, prjFolder, resource);
        _parse_OICalls(oi, doc, prjFolder, resource);
        _parse_FCalls(oi, doc, prjFolder, resource);
        _parse_MethodCalls(oi, doc, prjFolder, resource);
    }

    private void _parse_FCalls(I_Ope oi, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_FCalls.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String fiName = m_xp.evaluate("@facadeComponent", nl.item(n));
            String methodUID = m_xp.evaluate("@methodID", nl.item(n));

            String opeID = B_Ope.calcUID(methodUID, fiName);
            B_Ope bope = (B_Ope) NodeRegistry.getByUID(opeID);
            if (bope == null) {
                // Creates a temporary phantom node
                bope = B_Ope.createPhantom(methodUID, fiName, resource);
                NodeRegistry.add(bope);
            }
            oi.getReferences().add(bope);
            bope.getReferees().add(oi);
        }
    }

    private void _parse_MethodCalls(I_Ope oi, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_MethodCalls.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String packageName = m_xp.evaluate("@package", nl.item(n));
            String componentName = m_xp.evaluate("@componentName", nl.item(n));
            String methodUID = m_xp.evaluate("@methodID", nl.item(n));

            String QName = packageName + "." + componentName;
            String cmID = CMethod.calcUID(methodUID, QName);
            CMethod method = (CMethod) NodeRegistry.getByUID(cmID);
            if (method == null) {
                // Creates a temporary phantom node
                method = CMethod.createPhantom(methodUID, QName, resource);
                NodeRegistry.add(method);
            }
            oi.getReferences().add(method);
            method.getReferees().add(oi);
        }
    }

    private void _parse_OICalls(I_Ope oi, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_OICalls.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String refAL = m_xp.evaluate("@referenceApplication", nl.item(n));
            String refOI = m_xp.evaluate("@internalOperation", nl.item(n));

            String oiUID = I_Ope.calcUID(refOI, refAL);

            I_Ope iope = (I_Ope) NodeRegistry.getByUID(oiUID);
            if (iope == null) {
                // Creates a temporary phantom node
                iope = I_Ope.createPhantom(refOI, refAL, resource);
                NodeRegistry.add(iope);
            }
            oi.getReferences().add(iope);
            iope.getReferees().add(oi);
        }
    }

    private void _parse_RefTypes(I_Ope oi, Document doc, File prjFolder, File resource) throws Exception {

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

                oi.getReferences().add(ref);
                ref.getReferees().add(oi);
            }
        }
    }
}
