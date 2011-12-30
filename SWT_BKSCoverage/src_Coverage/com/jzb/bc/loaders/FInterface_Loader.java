/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jzb.bc.model.BException;
import com.jzb.bc.model.B_Ope;
import com.jzb.bc.model.CtxBean;
import com.jzb.bc.model.FInterface;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class FInterface_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_Methods;
    private XPathExpression m_xp_refBExceptions;
    private XPathExpression m_xp_refTypes;

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_Methods = m_xp.compile("/*/methodList/method");
        m_xp_refTypes = m_xp.compile("./methodParameterList/methodParameter/@type");
        m_xp_refBExceptions = m_xp.compile("./businessExceptionReferenceList/businessExceptionReference");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        Tracer._debug("Parsing FInterface");
        String fiName = _getVegaElementName(doc, prjFolder, resource);
        String fiPkg = resource.getParentFile().getName();
        FInterface fint = new FInterface(fiPkg, fiName, resource);
        NodeRegistry.add(fint);

        Tracer._debug("Parsing FInterface's B_Opes");
        _parseB_Opes(fint, doc, prjFolder, resource);
    }

    private String _calcMethodSignature(Node methodNode) throws Exception {

        StringBuffer signature = new StringBuffer("(");
        NodeList nl = (NodeList) m_xp.evaluate("./methodParameterList/methodParameter/@type", methodNode, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            if (n > 0)
                signature.append(", ");
            signature.append(nl.item(n).getNodeValue());
        }
        signature.append(")");
        return signature.toString();
    }

    private void _parse_RefBExceptions(B_Ope bope, Document doc, Node methodNode, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_refBExceptions.evaluate(methodNode, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String pkg = m_xp.evaluate("@package", nl.item(n));
            String name = m_xp.evaluate("@name", nl.item(n));

            String uid = BException.calcUID(pkg, name);
            BException bex = (BException) NodeRegistry.getByUID(uid);
            if (bex == null) {
                // Creates a temporary phantom node
                bex = BException.createPhantom(pkg, name, resource);
                NodeRegistry.add(bex);
            }

            bope.getReferences().add(bex);
            bex.getReferees().add(bope);

        }
    }

    private void _parse_RefTypes(B_Ope bope, Document doc, Node methodNode, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_refTypes.evaluate(methodNode, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String typeName = nl.item(n).getNodeValue();

            if (typeName.contains(".")) {

                CtxBean ref = (CtxBean) NodeRegistry.getByUID(typeName);
                if (ref == null) {
                    // Creates a temporary phantom node
                    ref = CtxBean.createPhantom(typeName, resource);
                    NodeRegistry.add(ref);
                }

                bope.getReferences().add(ref);
                ref.getReferees().add(bope);
            }
        }
    }

    private void _parseB_Opes(FInterface fint, Document doc, File prjFolder, File resource) throws Exception {

        String fiName = _getVegaElementName(doc, prjFolder, resource);
        String fiPkg = resource.getParentFile().getName();

        NodeList nl = (NodeList) m_xp_Methods.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String methodName = m_xp.evaluate("@name", nl.item(n));
            String methodUID = m_xp.evaluate("@gid", nl.item(n));
            String signature = _calcMethodSignature(nl.item(n));

            B_Ope bope = new B_Ope(methodUID, methodName, signature, fiPkg, fiName, resource);
            NodeRegistry.add(bope);

            fint.getOwns().add(bope);
            bope.getOwners().add(fint);

            _parse_RefTypes(bope, doc, nl.item(n), prjFolder, resource);
            _parse_RefBExceptions(bope, doc, nl.item(n), prjFolder, resource);
        }
    }
}
