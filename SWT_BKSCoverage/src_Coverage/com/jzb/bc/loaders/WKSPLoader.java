/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.bc.model.BKSNode;
import com.jzb.bc.model.B_Ope;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.bc.model.NodeType;
import com.jzb.bc.model.PBCMethod;
import com.jzb.bc.model.PreBin;
import com.jzb.bc.model.SCN_Ope;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class WKSPLoader {

    public static WKSPLoader    _inst              = new WKSPLoader();

    private final static String FILTERED_NATURES[] = { "com.isb.vega.model.preassembly.preAssemblyNature", "com.isb.vega.model.binarypreassembly.binaryPreAssemblyNature",
            "com.isb.vega.model.block.blockNature" };

    private DocumentBuilder     m_builder;

    private XPath               m_xp;

    private XPathExpression     m_xp_Natures;

    public void load(File baseFolder) throws Exception {

        Tracer._info("Parsing WorkSpace: " + baseFolder);

        _initXMLParser();

        for (File prjFolder : baseFolder.listFiles()) {

            if (!_hasDotProject(prjFolder)) {
                Tracer._debug("Skipping non Eclipse project folder: " + prjFolder.getName());
                continue;
            }

            if (_isVegaProject(prjFolder) && _hasAcceptedNature(prjFolder)) {
                Tracer._info("Iterating Vega project folder: " + prjFolder.getName());
                _iterateVegaProjectFolders(prjFolder, new File(prjFolder, "vega"));
            } else if (_isPreBin(prjFolder)) {
                Tracer._info("Iterating Binary PreAssembly project folder: " + prjFolder.getName());
                _iteratePreBinProjectFolder(prjFolder, prjFolder);
            }

        }

        // Hay que resolver las implementaciones de metodos de componentes sobre B_Ope
        _resolveBOpeImplementations();

        // Hay que resolver las referencias a Operaciones desde los Escenarios
        SCN_Ope.resolveScenarioOpeRefs();

    }

    private boolean _hasAcceptedNature(File prjFolder) throws Exception {
        HashSet<String> natures = _readNatures(prjFolder);
        for (String nat : FILTERED_NATURES) {
            if (natures.contains(nat))
                return false;
        }
        return true;

    }

    private boolean _hasDotProject(File folder) {

        File f = new File(folder, ".project");
        return f.exists();
    }

    private void _initXMLParser() throws Exception {

        m_xp = XPathFactory.newInstance().newXPath();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        m_builder = factory.newDocumentBuilder();

        m_xp_Natures = m_xp.compile("/*/natures/nature/text()");
    }

    private boolean _isPreBin(File prjFolder) throws Exception {
        HashSet<String> natures = _readNatures(prjFolder);
        return natures.contains("com.isb.vega.model.binarypreassembly.binaryPreAssemblyNature");

    }

    private boolean _isVegaProject(File folder) {

        File f = new File(folder, "vega");
        return f.exists();
    }

    private void _iteratePreBinProjectFolder(File prjFolder, File folder) throws Exception {

        PreBin preBin = new PreBin(prjFolder.getName(), new File(prjFolder, "PREBIN_descriptor.xml"));
        NodeRegistry.add(preBin);

        for (File f : folder.listFiles()) {
            if (f.isFile()) {
                ILoader loader = LoadersRegistry.getLoader(prjFolder, f);
                if (loader != null) {
                    Tracer._debug("Processing Vega Resource: " + f.getName());
                    loader.load(preBin, prjFolder, f);
                }
            }
        }

    }

    private void _iterateVegaProjectFolders(File prjFolder, File folder) throws Exception {

        for (File f : folder.listFiles()) {

            if (f.isDirectory()) {
                if (!f.getName().equals("CVS")) {
                    _iterateVegaProjectFolders(prjFolder, f);
                }
            } else {
                ILoader loader = LoadersRegistry.getLoader(prjFolder, f);
                if (loader != null) {
                    Tracer._debug("Processing Vega Resource: " + f.getName());
                    loader.load(prjFolder, f);
                }
            }
        }

    }

    private HashSet<String> _readNatures(File folder) throws Exception {

        HashSet<String> natures = new HashSet<String>();

        File resource = new File(folder, ".project");
        m_builder.reset();
        Document document = m_builder.parse(resource);
        NodeList nl = (NodeList) m_xp_Natures.evaluate(document, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            String nat = nl.item(n).getNodeValue();
            natures.add(nat);
        }

        return natures;

    }

    private void _resolveBOpeImplementations() {

        Tracer._info("Resolving B_Ope implementations");
        for (BKSNode fi : NodeRegistry.getByType(NodeType.FInterface)) {
            for (BKSNode bope : fi.getOwns()) {
                if (bope.getType() != NodeType.B_Ope)
                    continue;

                BKSNode cmethod = _searchImplCMethod(fi, bope);
                if (cmethod == null) {
                    cmethod = _searchImplPBCMethod(fi, bope);
                }

                if (cmethod != null) {
                    bope.getImplementors().add(cmethod);
                    cmethod.getImplements().add(bope);
                } else {
                    Tracer._warn("B_Ope doesn't have implementation: " + bope);
                }

            }
        }

    }

    private BKSNode _searchImplCMethod(BKSNode fi, BKSNode bope) {

        for (BKSNode comp : fi.getImplementors()) {

            if (comp.getType() != NodeType.Component)
                continue;

            for (BKSNode cmethod : comp.getOwns()) {

                if (cmethod.getType() != NodeType.CMethod)
                    continue;

                if (bope.getSName().equals(cmethod.getSName())) {
                    return cmethod;
                }
            }
        }
        return null;
    }

    private BKSNode _searchImplPBCMethod(BKSNode fi, BKSNode bope) {

        for (BKSNode prebin : fi.getImplementors()) {

            if (prebin.getType() != NodeType.PreBin)
                continue;

            for (BKSNode node : prebin.getOwns()) {

                if (node.getType() != NodeType.PBCMethod)
                    continue;

                PBCMethod pbcmethod = (PBCMethod) node;
                for (String qname : pbcmethod.getFInterfaces()) {
                    String uid = B_Ope.calcUID(pbcmethod.getBOpe_UID(), qname);
                    if (bope.getUID().equals(uid)) {
                        return pbcmethod;
                    }
                }

            }
        }
        return null;
    }
}
