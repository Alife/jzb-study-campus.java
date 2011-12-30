/**
 * 
 */
package com.jzb.bc;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.jzb.bc.loaders.WKSPLoader;
import com.jzb.bc.model.BKSNode;
import com.jzb.bc.model.NodeRegistry;
import com.jzb.bc.model.NodeType;
import com.jzb.bc.model.Scenario;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class CoverageTester {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            CoverageTester me = new CoverageTester();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    @SuppressWarnings("unused")
    public void doIt(String[] args) throws Exception {

        final boolean READ_FROM_STORAGE = Boolean.TRUE;
        final boolean DONT_READ_FROM_STORAGE = Boolean.FALSE;

        File storage = new File("C:\\WKSPs\\Ganymede-Java\\BKSCoverage\\resources\\wksp.out");
        File WSBasePath = new File("C:\\WKSPs\\wsad_workspace_SP2F04(limpio)");
        // File WSBasePath = new File("C:\\WKSPs\\RAD_COCTOF_VACIO");

        checkWKSP(WSBasePath, storage, READ_FROM_STORAGE);
    }

    public void checkWKSP(final File WKSPFolder, final File storageFile, final boolean loadFromStorage) throws Exception {

        if (!loadFromStorage) {
            WKSPLoader._inst.load(WKSPFolder);
            NodeRegistry.storeData(storageFile);
        } else {
            NodeRegistry.loadData(storageFile);
        }
        NodeRegistry.checkModel();

        HashSet<BKSNode> unused = new HashSet<BKSNode>();
        Tracer._debug("Model usage iteration...");
        _checkUnused(unused);
        _printUsage(WKSPFolder, unused);

        _reviewUnusedNodes(-1, unused, WKSPFolder, storageFile);

    }

    private void _printUsage(File WKSPFolder, HashSet<BKSNode> unused) throws Exception {

        Set<String> initialPrjs = _calcPrjFolders(WKSPFolder);

        HashMap<NodeType, Integer> initialSize = new HashMap<NodeType, Integer>();
        for (NodeType type : NodeType.values()) {
            initialSize.put(type, NodeRegistry.getByType(type).size());
        }

        NodeRegistry.removeAll(unused);

        Set<String> finalPrjs = _calcPrjFolders(WKSPFolder);

        Tracer._info("---- % De uso --------------------------------------");
        for (NodeType type : NodeType.values()) {
            int size = NodeRegistry.getByType(type).size();
            int iSize = initialSize.get(type);
            int ratio = (int) Math.round(100.0 * size / iSize);
            Tracer._info(type + " - " + size + " de " + iSize + " (" + ratio + "%)");
        }

        Tracer._info("---- PRJs sin uso --------------------------------------");
        initialPrjs.removeAll(finalPrjs);
        for (String s : initialPrjs) {
            Tracer._info(s);
        }

    }

    private void _reviewUnusedNodes(int lastUsedSize, HashSet<BKSNode> unused, File WKSPFolder, File storageFile) throws Exception {

        TreeSet<String> usedPrjFolders = new TreeSet<String>();
        for (BKSNode node : NodeRegistry.getAllNodes()) {
            File f = node.getResource();
            if (f != null) {
                File fprj = f;
                while (!fprj.getParent().equals(WKSPFolder.getPath())) {
                    fprj = fprj.getParentFile();
                }
                usedPrjFolders.add(fprj.getAbsolutePath());
            }
        }

        if (lastUsedSize == usedPrjFolders.size()) {
            _printUsage(WKSPFolder, unused);
            return;
        }

        NodeRegistry.loadData(storageFile);

        BKSNode pinNode = new Scenario("PinNode", new File("PinNode"));
        // NodeRegistry.add(pinNode);
        for (BKSNode node : NodeRegistry.getAllNodes()) {
            File f = node.getResource();
            if (f != null) {
                File fprj = f;
                while (!fprj.getParent().equals(WKSPFolder.getPath())) {
                    fprj = fprj.getParentFile();
                }
                if (usedPrjFolders.contains(fprj.getAbsolutePath())) {
                    pinNode.getReferences().add(node);
                    node.getReferees().add(pinNode);
                }
            }
        }

        HashSet<BKSNode> new_unused = new HashSet<BKSNode>();
        Tracer._debug("Model usage re-iteration...");
        _checkUnused(new_unused);

        _reviewUnusedNodes(usedPrjFolders.size(), new_unused, WKSPFolder, storageFile);

    }

    private Set<String> _calcPrjFolders(File wkspBasePath) {

        TreeSet<String> prjFolders = new TreeSet<String>();

        int basePathIndex = wkspBasePath.getAbsolutePath().length() + 1;
        for (BKSNode node : NodeRegistry.getAllNodes()) {
            File f = node.getResource();
            if (f != null) {
                String prjName = f.getAbsolutePath().substring(basePathIndex);
                int p1 = prjName.indexOf(File.separatorChar);
                prjName = prjName.substring(0, p1);
                prjFolders.add(prjName);
            }
        }

        return prjFolders;
    }

    private void _checkUnused(HashSet<BKSNode> unused) {

        boolean keepIterating = false;
        for (BKSNode node : NodeRegistry.getAllNodes()) {

            if (!unused.contains(node) && node.getReferees().size() == 0 && node.getImplements().size() == 0 && node.getOwns().size() == 0) {

                // No podemos quitar el Scenario o se irían todas las OPs y de ahí en cadena
                if (node.getType() == NodeType.Scenario) {
                    continue;
                }

                unused.add(node);
                keepIterating = true;

                // Elimina sus referencias
                for (BKSNode ref : node.getReferences()) {
                    ref.getReferees().remove(node);
                }
                node.getReferences().clear();

                // Elimina sus implementaciones
                for (BKSNode impl : node.getImplementors()) {
                    impl.getImplements().remove(node);
                }
                node.getImplementors().clear();

                // Elimina sus dueños
                for (BKSNode owner : node.getOwners()) {
                    owner.getOwns().remove(node);
                }
                node.getOwners().clear();

            }
        }

        if (keepIterating)
            _checkUnused(unused);

    }

}
