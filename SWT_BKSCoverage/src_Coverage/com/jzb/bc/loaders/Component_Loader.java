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
import com.jzb.bc.model.CMethod;
import com.jzb.bc.model.Component;
import com.jzb.bc.model.CtxBean;
import com.jzb.bc.model.FInterface;
import com.jzb.bc.model.I_Ope;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class Component_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_Impls;
    private XPathExpression m_xp_Methods;
    private XPathExpression m_xp_refBExceptions;
    private XPathExpression m_xp_refTypes;

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_Methods = m_xp.compile("/*/methodList/method");
        m_xp_Impls = m_xp.compile("/*/implsList/implementation");
        m_xp_refTypes = m_xp.compile("./methodParameterList/methodParameter/@type");
        m_xp_refBExceptions = m_xp.compile("./businessExceptionReferenceList/businessExceptionReference");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        Tracer._debug("Parsing Component");
        String compName = _getVegaElementName(doc, prjFolder, resource);
        String compPkg = resource.getParentFile().getName();
        Component comp = new Component(compPkg, compName, resource);
        NodeRegistry.add(comp);

        Tracer._debug("Parsing Component's CMethods");
        _parseCMethods(comp, doc, prjFolder, resource);

        Tracer._debug("Parsing Component's FInterface implementations");
        _parseFIntImplementations(comp, doc, prjFolder, resource);
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

    private void _parse_RefBExceptions(CMethod method, Document doc, Node methodNode, File prjFolder, File resource) throws Exception {

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

            method.getReferences().add(bex);
            bex.getReferees().add(method);

        }
    }

    private void _parse_RefTypes(CMethod method, Document doc, Node methodNode, File prjFolder, File resource) throws Exception {

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

                method.getReferences().add(ref);
                ref.getReferees().add(method);
            }
        }

        String returnType = m_xp.evaluate("./methodReturn/@type", methodNode);
        if (returnType.contains(".")) {

            CtxBean ref = (CtxBean) NodeRegistry.getByUID(returnType);
            if (ref == null) {
                // Creates a temporary phantom node
                ref = CtxBean.createPhantom(returnType, resource);
                NodeRegistry.add(ref);
            }

            method.getReferences().add(ref);
            ref.getReferees().add(method);
        }
    }

    private void _parseCMethods(Component comp, Document doc, File prjFolder, File resource) throws Exception {

        String compName = _getVegaElementName(doc, prjFolder, resource);
        String compPkg = resource.getParentFile().getName();

        NodeList nl = (NodeList) m_xp_Methods.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String methodName = m_xp.evaluate("@name", nl.item(n));
            String methodUID = m_xp.evaluate("@gid", nl.item(n));
            String signature = _calcMethodSignature(nl.item(n));

            CMethod method = new CMethod(methodUID, methodName, signature, compPkg, compName, resource);
            NodeRegistry.add(method);

            _parseExternalCalls(method, doc, nl.item(n), prjFolder, resource);
            _parse_RefTypes(method, doc, nl.item(n), prjFolder, resource);
            _parse_RefBExceptions(method, doc, nl.item(n), prjFolder, resource);

            comp.getOwns().add(method);
            method.getOwners().add(comp);

        }
    }

    private void _parseDelegateImplementation(CMethod method, Document doc, Node methodNode, File resource) throws Exception {

        String targetField = m_xp.evaluate("methodImplementation/@targetField", methodNode);
        String calledMethodID = m_xp.evaluate("methodImplementation/@targetAccess", methodNode);
        // String calledMethodName = m_xp.evaluate("methodImplementation/@targetAccessName", methodNode);

        Node targetFieldNode = (Node) m_xp.evaluate("/*/componentFieldList/*[@name='" + targetField + "']", doc, XPathConstants.NODE);

        String delegateType = targetFieldNode.getNodeName();
        if ("componentCallField".equals(delegateType)) {

            // ------------------------------------------------------------
            // Delega en un metodo de otro componente
            String calledComponentPackage = m_xp.evaluate("@calledComponentPackage", targetFieldNode);
            String calledComponentName = m_xp.evaluate("@calledComponentName", targetFieldNode);

            String QName = calledComponentPackage + "." + calledComponentName;
            String mID = CMethod.calcUID(calledMethodID, QName);
            CMethod calledMethod = (CMethod) NodeRegistry.getByUID(mID);
            if (calledMethod == null) {
                // Creates a temporary phantom node
                calledMethod = CMethod.createPhantom(calledMethodID, QName, resource);
                NodeRegistry.add(calledMethod);
            }
            method.getReferences().add(calledMethod);
            calledMethod.getReferees().add(method);

        } else if ("facadeComponentCallField".equals(delegateType)) {

            // ------------------------------------------------------------
            // Delega en un metodo de una fachada
            String calledFacadeComponent = m_xp.evaluate("@calledFacadeComponent", targetFieldNode);
            String bopID = B_Ope.calcUID(calledMethodID, calledFacadeComponent);
            B_Ope calledB_Ope = (B_Ope) NodeRegistry.getByUID(bopID);
            if (calledB_Ope == null) {
                // Creates a temporary phantom node
                calledB_Ope = B_Ope.createPhantom(calledMethodID, calledFacadeComponent, resource);
                NodeRegistry.add(calledB_Ope);
            }
            method.getReferences().add(calledB_Ope);
            calledB_Ope.getReferees().add(method);

        } else {
            Tracer._error("Component delegating to something unknown (" + delegateType + "): " + method);
        }

    }

    private void _parseExternalCalls(CMethod method, Document doc, Node methodNode, File prjFolder, File resource) throws Exception {

        String type = m_xp.evaluate("methodImplementation/@type", methodNode);
        if (type.equals("delegateImplementation")) {
            _parseDelegateImplementation(method, doc, methodNode, resource);
        } else if (type.equals("mapperImplementation")) {
            _parseMapperImplementation(method, doc, methodNode, resource);

        } else if (type.equals("javaImplementation")) {
            // no añade nada... Debería buscar en el código fuente Java
        } else if (type.equals("dummyImplementation")) {
            // no añade nada
        } else {
            Tracer._error("LAA Component calling to something unknown (" + type + "): " + method);
        }
    }

    private void _parseFIntImplementations(Component comp, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_Impls.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            String sPackage = m_xp.evaluate("@package", nl.item(n));
            String sName = m_xp.evaluate("@name", nl.item(n));

            String uid = FInterface.calcUID(sPackage, sName);
            FInterface fi = (FInterface) NodeRegistry.getByUID(uid);
            if (fi == null) {
                // Creates a temporary phantom node
                fi = FInterface.createPhantom(sPackage, sName, resource);
                NodeRegistry.add(fi);
            }

            comp.getImplements().add(fi);
            fi.getImplementors().add(comp);
        }
    }

    private void _parseMapperImplementation(CMethod method, Document doc, Node methodNode, File resource) throws Exception {

        // Implementacion de llamada a una OI
        String targetAccess = m_xp.evaluate("methodImplementation/@targetAccess", methodNode);
        if (!targetAccess.equals("Call")) {
            return;
        }

        String targetField = m_xp.evaluate("methodImplementation/@targetField", methodNode);
        Node targetFieldNode = (Node) m_xp.evaluate("/*/componentFieldList/operationCallField[@name='" + targetField + "']", doc, XPathConstants.NODE);
        String refAL = m_xp.evaluate("@calledApplication", targetFieldNode);
        String refOI = m_xp.evaluate("@calledOeperation", targetFieldNode);

        String oiUID = I_Ope.calcUID(refOI, refAL);

        I_Ope iope = (I_Ope) NodeRegistry.getByUID(oiUID);
        if (iope == null) {
            // Creates a temporary phantom node
            iope = I_Ope.createPhantom(refOI, refAL, resource);
            NodeRegistry.add(iope);
        }
        method.getReferences().add(iope);
        iope.getReferees().add(method);

    }
}
