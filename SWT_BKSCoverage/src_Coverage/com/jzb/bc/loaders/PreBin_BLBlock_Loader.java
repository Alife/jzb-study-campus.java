/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;
import java.util.ArrayList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.bc.model.PBCMethod;
import com.jzb.bc.model.FInterface;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.bc.model.PreBin;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class PreBin_BLBlock_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_refFInterfaces;
    private XPathExpression m_xp_refMethods;

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_refFInterfaces = m_xp.compile("/*//facadeComponentReference");
        m_xp_refMethods = m_xp.compile("/*//facadeMethod");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        PreBin preBin = (PreBin) param;

        Tracer._debug("Parsing PreBin Facade references");
        _parserFIntRefs(preBin, doc, prjFolder, resource);

        Tracer._debug("Parsing PreBin Methods references");
        _parserMethodRefs(preBin, doc, prjFolder, resource);

        // TODO: Faltaria el revisar si podemos saber que CtxBean estan en uso por los elementos internos del
        // PreensambladoBinario

    }

    private void _parserFIntRefs(PreBin preBin, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_refFInterfaces.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String sName = m_xp.evaluate("@name", nl.item(n));
            String sPackage = m_xp.evaluate("@package", nl.item(n));

            String fiUID = FInterface.calcUID(sPackage, sName);

            FInterface fi = (FInterface) NodeRegistry.getByUID(fiUID);
            if (fi == null) {
                // Creates a temporary phantom node
                fi = FInterface.createPhantom(sPackage, sName, resource);
                NodeRegistry.add(fi);
            }

            preBin.getImplements().add(fi);
            fi.getImplementors().add(preBin);
        }
    }

    private void _parserMethodRefs(PreBin preBin, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_refMethods.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String bopeUID = m_xp.evaluate("@id", nl.item(n));
            String facName = m_xp.evaluate("@facadeName", nl.item(n));

            String methodName = m_xp.evaluate("@name", nl.item(n));
            String compName = m_xp.evaluate("@componentName", nl.item(n));
            String compPackage = m_xp.evaluate("@componentPackage", nl.item(n));

            ArrayList<String> ifQNames = new ArrayList<String>();
            NodeList ifNodes = (NodeList) m_xp.evaluate("/*//implementedFacade[@name='" + facName + "']/associationList/association/facadeComponentReference", doc, XPathConstants.NODESET);
            for (int x = 0; x < ifNodes.getLength(); x++) {
                String fiName = m_xp.evaluate("@name", ifNodes.item(x));
                String fiPackage = m_xp.evaluate("@package", ifNodes.item(x));
                String QName = fiPackage + "." + fiName;
                ifQNames.add(QName);
            }

            // Crea metodos ficticeos del PreBin que implementen las B_Ope
            PBCMethod method = new PBCMethod("PB@" + bopeUID, methodName, compPackage, compName, bopeUID, ifQNames, resource);
            NodeRegistry.add(method);
            preBin.getOwns().add(method);
            method.getOwners().add(preBin);
        }

    }
}
