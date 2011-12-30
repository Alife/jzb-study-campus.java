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
import com.jzb.bc.model.CtxBean;
import com.jzb.bc.model.I_Ope;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.bc.model.P_Ope;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class OP_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_FCalls;
    private XPathExpression m_xp_OICalls;
    private XPathExpression m_xp_OPCalls;
    private XPathExpression m_xp_refTypes;

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_FCalls = m_xp.compile("/presentationOperation/stateList/FacadeComponentState");
        m_xp_OPCalls = m_xp.compile("/presentationOperation/stateList/OPState");
        m_xp_OICalls = m_xp.compile("/presentationOperation/stateList/OIState");
        m_xp_refTypes = m_xp.compile("/*/operationContext//*/@type");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        Tracer._debug("Adding OP node");
        String opName = _getVegaElementName(doc, prjFolder, resource);
        String alName = resource.getParentFile().getName();
        P_Ope op = new P_Ope(opName, alName, resource);
        NodeRegistry.add(op);

        Tracer._debug("Parsing CtxBeans, OPCalls, IOCalls and FCalls");
        _parse_RefTypes(op, doc, prjFolder, resource);
        _parse_OPCalls(op, doc, prjFolder, resource);
        _parse_OICalls(op, doc, prjFolder, resource);
        _parse_FCalls(op, doc, prjFolder, resource);
    }

    private void _parse_FCalls(P_Ope op, Document doc, File prjFolder, File resource) throws Exception {

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
            op.getReferences().add(bope);
            bope.getReferees().add(op);
        }
    }

    private void _parse_OICalls(P_Ope op, Document doc, File prjFolder, File resource) throws Exception {

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
            op.getReferences().add(iope);
            iope.getReferees().add(op);
        }
    }

    private void _parse_OPCalls(P_Ope op, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_OPCalls.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String refAL = m_xp.evaluate("@referenceApplication", nl.item(n));
            String refOP = m_xp.evaluate("@presentationOperation", nl.item(n));

            String opUID = P_Ope.calcUID(refOP, refAL);

            P_Ope pope = (P_Ope) NodeRegistry.getByUID(opUID);
            if (pope == null) {
                // Creates a temporary phantom node
                pope = P_Ope.createPhantom(refOP, refAL, resource);
                NodeRegistry.add(pope);
            }
            op.getReferences().add(pope);
            pope.getReferees().add(op);
        }
    }

    private void _parse_RefTypes(P_Ope op, Document doc, File prjFolder, File resource) throws Exception {

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

                op.getReferences().add(ref);
                ref.getReferees().add(op);
            }
        }
    }
}
