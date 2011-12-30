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
import com.jzb.bc.model.NodeRegistry;
import com.jzb.bc.model.NodeType;
import com.jzb.bc.model.P_Ope;
import com.jzb.bc.model.SCN_Ope;
import com.jzb.bc.model.Scenario;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class Scenario_Loader extends XMLBase_Loader {

    private XPathExpression m_xp_ALRefs;
    private XPathExpression m_xp_CompName;
    private XPathExpression m_xp_OPRefs;

    @Override
    protected String _getVegaElementName(Document doc, File prjFolder, File resource) {

        // TODO: ¿cuál es el nombre bueno el del fichero o el del XML?

        String s1 = _getFileName(resource.getParentFile());
        String s2;
        try {
            s2 = m_xp_CompName.evaluate(doc.getDocumentElement());
        } catch (Exception ex) {
            Tracer._error("Evaluating Element Name XPathExpression", ex);
            s2 = null;
        }
        if (s2 != null && s1.equals(s2)) {
            return s1;
        } else {
            Tracer._warn("Element's name in File and XMLNode name don't match: '" + s1 + "' - '" + s2 + "', file:" + resource);
            return s1;
        }
    }

    @Override
    protected void _subinitXML() throws Exception {
        m_xp_CompName = m_xp.compile("./@name");
        m_xp_ALRefs = m_xp.compile("/*//applicationReference");
        m_xp_OPRefs = m_xp.compile("./*//operationReference");
    }

    @Override
    protected void parseDOM(Object param, Document doc, File prjFolder, File resource) throws Exception {

        Tracer._debug("Adding Scenario node");
        String sName = _getVegaElementName(doc, prjFolder, resource);
        Scenario scenario = new Scenario(sName, resource);
        NodeRegistry.add(scenario);

        Tracer._debug("Parsing referenced OPs & OIs");
        _parse_OperationRefs(scenario, doc, prjFolder, resource);
    }

    private void _parse_OperationRefs(Scenario scenario, Document doc, File prjFolder, File resource) throws Exception {

        NodeList als = (NodeList) m_xp_ALRefs.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < als.getLength(); n++) {

            String alName = m_xp.evaluate("@name", als.item(n));
            NodeList ops = (NodeList) m_xp_OPRefs.evaluate(als.item(n), XPathConstants.NODESET);
            for (int i = 0; i < ops.getLength(); i++) {

                String opeName = m_xp.evaluate("@name", ops.item(i));
                String opUID = P_Ope.calcUID(opeName, alName);

                BKSNode ope = NodeRegistry.getByUID(opUID);
                if (ope == null) {
                    // Creates a temporary SCENARIO_OPERATION to be resolved later node
                    ope = new SCN_Ope(scenario, opeName, alName);
                    NodeRegistry.add(ope);
                } else {
                    // We will just add references to P_Opes
                    if (ope.getType() == NodeType.P_Ope) {
                        scenario.getReferences().add(ope);
                        ope.getReferees().add(scenario);
                    }
                }

            }

        }

    }
}
