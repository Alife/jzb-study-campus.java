/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.bc.model.BKSNode;
import com.jzb.bc.model.FInterface;
import com.jzb.bc.model.Facade;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class Facade_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_FacName;
    private XPathExpression m_xp_FIntRefs;
    private XPathExpression m_xp_PkgName;

    @Override
    protected String _getVegaElementName(Document doc, File prjFolder, File resource) {

        // TODO: ¿cuál es el nombre bueno el del fichero o el del XML?

        String s1 = _getFileName(resource.getParentFile());
        String s2;
        try {
            s2 = m_xp_FacName.evaluate(doc.getDocumentElement());
        } catch (Exception ex) {
            Tracer._error("Evaluating Element Name XPathExpression", ex);
            s2 = null;
        }
        if (s2 != null && s1.equals(s2)) {
            return s1;
        } else {
            Tracer._debug("Element's name in File and XMLNode name don't match: '" + s1 + "' - '" + s2 + "', file:" + resource);
            return s1;
        }
    }

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_FacName = m_xp.compile("./@name");
        m_xp_PkgName = m_xp.compile("./@package");
        m_xp_FIntRefs = m_xp.compile("/*//facadeComponentReference");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        Tracer._debug("Adding Facade node");
        String fName = _getVegaElementName(doc, prjFolder, resource);
        String pkgName = m_xp_PkgName.evaluate(doc.getDocumentElement());

        Facade facade = new Facade(pkgName, fName, resource);
        NodeRegistry.add(facade);

        Tracer._debug("Parsing referenced FInterface");
        _parse_FInterfaceRefs(facade, doc, prjFolder, resource);
    }

    private void _parse_FInterfaceRefs(Facade facade, Document doc, File prjFolder, File resource) throws Exception {

        NodeList nl = (NodeList) m_xp_FIntRefs.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {

            String fiName = m_xp.evaluate("@name", nl.item(n));
            String fiPkg = m_xp.evaluate("@package", nl.item(n));

            String uid = FInterface.calcUID(fiPkg, fiName);
            BKSNode fi = NodeRegistry.getByUID(uid);
            if (fi == null) {
                // Creates a temporary phantom node
                fi = FInterface.createPhantom(fiPkg, fiName, resource);
                NodeRegistry.add(fi);
            }

            facade.getOwns().add(fi);
            fi.getOwners().add(facade);

        }

    }
}
