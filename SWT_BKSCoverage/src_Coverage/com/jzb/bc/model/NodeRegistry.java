/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class NodeRegistry {

    private static HashMap<NodeType, ArrayList<BKSNode>> m_nodesByType  = new HashMap<NodeType, ArrayList<BKSNode>>();
    private static HashMap<String, BKSNode>              m_nodesByUID   = new HashMap<String, BKSNode>();
    private static ArrayList<BKSNode>                    m_phantomNodes = new ArrayList<BKSNode>();

    public static void add(BKSNode node) throws Exception {

        ArrayList<BKSNode> nodesType = getByType(node.getType());

        BKSNode prev = m_nodesByUID.put(node.getUID(), node);
        if (prev == null) {
            nodesType.add(node);
        } else {
            if (!prev.isPhantom()) {
                // Deja el ultimo añadido de los dos... tratandolo como un fantasma para copiar sus relaciones
                Tracer._error("Duplicate UID in nodes:\nNode1: " + node + "\nNode2: " + prev);
                _replacePhantom(node, prev);
                nodesType.remove(prev);
                nodesType.add(node);
                // throw new Exception("Duplicate UID in nodes:\nNode1: " + node + "\nNode2: " + prev);
            } else {
                _replacePhantom(node, prev);
                m_phantomNodes.remove(prev);
                nodesType.remove(prev);
                nodesType.add(node);
            }
        }

        if (node.isPhantom()) {
            m_phantomNodes.add(node);
        }

    }

    public static void checkModel() {

        // Ejecutamos una serie de chequeos

        // Si han quedado nodos Phantom pendientes
        Tracer._debug("\n\n----- Checking pending Phantom nodes  ------------------------------------\n\n");
        for (BKSNode node : NodeRegistry.getPhantomNodes()) {
            Tracer._warn("CHK - Pending phantom node: " + node);
        }

        // Varios chequeos sobre las operaciones de negocio
        Tracer._debug("\n\n----- Checking B_Ope nodes  ----------------------------------------------\n\n");
        for (BKSNode node : NodeRegistry.getByType(NodeType.B_Ope)) {

            if (node.getOwners().size() == 0) {
                Tracer._warn("CHK - B_Ope without FInterface owner: " + node);
            }

            if (node.getImplementors().size() == 0) {
                int implSize = 0;
                for (BKSNode impl : node.getOwners()) {
                    implSize += impl.getImplementors().size();
                }
                if (implSize > 0) {
                    Tracer._warn("CHK - B_Ope without CMethod implementation: " + node);
                }
            }
        }

        // Varios chequeos sobre los FInterface
        Tracer._debug("\n\n----- Checking FInterface nodes  -----------------------------------------\n\n");
        for (BKSNode node : NodeRegistry.getByType(NodeType.FInterface)) {

            if (node.getOwns().size() == 0) {
                Tracer._warn("CHK - FInterface without any B_Ope: " + node);
            }

            if (node.getImplementors().size() == 0) {
                if (node.getReferees().size() == 0) {
                    Tracer._warn("CHK - FInterface without Component implementation and referees: " + node);
                } else {
                    Tracer._warn("CHK - FInterface without Component implementation: " + node);
                }
            }
        }

        // Varios chequeos sobre los CMethod
        Tracer._debug("\n\n----- Checking CMethod nodes  ----------------------------------------------\n\n");
        for (BKSNode node : NodeRegistry.getByType(NodeType.CMethod)) {

            if (node.getOwners().size() == 0) {
                Tracer._warn("CHK - CMethod without Component owner: " + node);
            }

        }

        // Varios chequeos sobre los Component
        Tracer._debug("\n\n----- Checking Component nodes  ------------------------------------------\n\n");
        for (BKSNode node : NodeRegistry.getByType(NodeType.Component)) {

            if (node.getOwns().size() == 0) {
                Tracer._warn("CHK - Component without any CMethod: " + node);
            }
        }

        // Varios chequeos sobre los CMethod
        Tracer._debug("\n\n----- Checking PBCMethod nodes  ----------------------------------------------\n\n");
        for (BKSNode node : NodeRegistry.getByType(NodeType.PBCMethod)) {

            if (node.getOwners().size() == 0) {
                Tracer._warn("CHK - PBCMethod without PreBin owner: " + node);
            }

        }

        // Varios chequeos sobre los Component
        Tracer._debug("\n\n----- Checking PreBin nodes  ------------------------------------------\n\n");
        for (BKSNode node : NodeRegistry.getByType(NodeType.PreBin)) {

            if (node.getOwns().size() == 0) {
                Tracer._warn("CHK - PreBin without any PBCMethod: " + node);
            }
        }

    }

    public static void clear() {
        m_nodesByUID.clear();
        m_nodesByType.clear();
        m_phantomNodes.clear();
    }

    public static Collection<BKSNode> getAllNodes() {
        return m_nodesByUID.values();
    }

    public static ArrayList<BKSNode> getByType(NodeType type) {

        ArrayList<BKSNode> nodes = m_nodesByType.get(type);
        if (nodes == null) {
            nodes = new ArrayList<BKSNode>();
            m_nodesByType.put(type, nodes);
        }
        return nodes;
    }

    public static BKSNode getByUID(String UID) {
        return m_nodesByUID.get(UID);
    }

    /**
     * @return the m_phantomNodes
     */
    public static ArrayList<BKSNode> getPhantomNodes() {
        return m_phantomNodes;
    }

    public static void loadData(File file) throws Exception {

        Tracer._debug("Storing registry info in: " + file);
        clear();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        int size = ois.readInt();
        for (int n = 0; n < size; n++) {
            BKSNode node = (BKSNode) ois.readObject();
            add(node);
        }
        ois.close();

    }

    public static void remove(BKSNode node) {
        ArrayList<BKSNode> nodesType = getByType(node.getType());
        nodesType.remove(node);
        m_nodesByUID.remove(node.getUID());
        m_phantomNodes.remove(node);
    }

    public static void removeAll(Collection<BKSNode> nodes) {

        for (BKSNode node : nodes) {
            ArrayList<BKSNode> nodesType = getByType(node.getType());
            nodesType.remove(node);
        }
        m_nodesByUID.values().removeAll(nodes);
        m_phantomNodes.removeAll(nodes);
    }

    public static void storeData(File file) throws Exception {

        Tracer._debug("Loading registry info from: " + file);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeInt(m_nodesByUID.values().size());
        for (BKSNode node : m_nodesByUID.values()) {
            oos.writeObject(node);
        }
        oos.close();

    }

    private static void _replacePhantom(BKSNode node, BKSNode phantom) {

        node.getImplements().addAll(phantom.getImplements());
        node.getImplementors().addAll(phantom.getImplementors());

        node.getReferences().addAll(phantom.getReferences());
        node.getReferees().addAll(phantom.getReferees());

        node.getOwns().addAll(phantom.getOwns());
        node.getOwners().addAll(phantom.getOwners());

        for (BKSNode n : node.getImplements()) {
            if (n.getImplementors().remove(phantom)) {
                n.getImplementors().add(node);
            }
        }

        for (BKSNode n : node.getImplementors()) {
            if (n.getImplements().remove(phantom)) {
                n.getImplements().add(node);
            }
        }

        for (BKSNode n : node.getReferences()) {
            if (n.getReferees().remove(phantom)) {
                n.getReferees().add(node);
            }
        }

        for (BKSNode n : node.getReferees()) {
            if (n.getReferences().remove(phantom)) {
                n.getReferences().add(node);
            }
        }

        for (BKSNode n : node.getOwners()) {
            if (n.getOwns().remove(phantom)) {
                n.getOwns().add(node);
            }
        }

        for (BKSNode n : node.getOwns()) {
            if (n.getOwners().remove(phantom)) {
                n.getOwners().add(node);
            }
        }
    }
}
