/**
 * 
 */
package com.jzb.bc.model;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

/**
 * @author n63636
 * 
 */
public abstract class BKSNode implements Externalizable {

    // Clase utilizada para poder deserializar nodos referenciados que seran leidos despues en el Stream de datos
    protected static class RExtRef extends BKSNode {

        public RExtRef(NodeType type, String uid) {
            super(type, uid, "RExtRef", "RExtRef", null, true);
        }
    }

    private String             m_fname;

    private ArrayList<BKSNode> m_implementors = new ArrayList<BKSNode>();

    private ArrayList<BKSNode> m_implements   = new ArrayList<BKSNode>();

    private boolean            m_isPhantom;

    private ArrayList<BKSNode> m_owners       = new ArrayList<BKSNode>();

    private ArrayList<BKSNode> m_owns         = new ArrayList<BKSNode>();

    private ArrayList<BKSNode> m_referees     = new ArrayList<BKSNode>();
    private ArrayList<BKSNode> m_references   = new ArrayList<BKSNode>();

    private File               m_resource;
    private String             m_sname;

    private NodeType           m_type;

    private String             m_UID;

    public BKSNode() {
    }

    protected BKSNode(NodeType type, String UID, String sname, String fname, File resource, boolean isPhantom) {
        m_type = type;
        m_UID = UID;
        m_sname = sname;
        m_fname = fname;
        m_resource = resource;
        m_isPhantom = isPhantom;
    }

    /**
     * @return the fname
     */
    public String getFName() {
        return m_fname;
    }

    /**
     * @return the implementors
     */
    public ArrayList<BKSNode> getImplementors() {
        return m_implementors;
    }

    /**
     * @return the implements
     */
    public ArrayList<BKSNode> getImplements() {
        return m_implements;
    }

    /**
     * @return the owners
     */
    public ArrayList<BKSNode> getOwners() {
        return m_owners;
    }

    /**
     * @return the owns
     */
    public ArrayList<BKSNode> getOwns() {
        return m_owns;
    }

    /**
     * @return the referees
     */
    public ArrayList<BKSNode> getReferees() {
        return m_referees;
    }

    /**
     * @return the references
     */
    public ArrayList<BKSNode> getReferences() {
        return m_references;
    }

    /**
     * @return the resource
     */
    public File getResource() {
        return m_resource;
    }

    /**
     * @return the sname
     */
    public String getSName() {
        return m_sname;
    }

    /**
     * @return the type
     */
    public NodeType getType() {
        return m_type;
    }

    /**
     * @return the uID
     */
    public String getUID() {
        return m_UID;
    }

    /**
     * @return the isPhantom
     */
    public boolean isPhantom() {
        return m_isPhantom;
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_type = (NodeType) in.readObject();
        m_UID = in.readUTF();
        m_sname = in.readUTF();
        m_fname = in.readUTF();
        String fileName = in.readUTF();
        if (fileName.length() > 0) {
            m_resource = new File(fileName);
        } else {
            m_resource = null;
        }
        m_isPhantom = in.readBoolean();

        try {
            _readCollection(m_references, in);
            _readCollection(m_referees, in);
            _readCollection(m_implements, in);
            _readCollection(m_implementors, in);
            _readCollection(m_owns, in);
            _readCollection(m_owners, in);
        } catch (Exception ex) {
            throw new IOException("Error reading collection", ex);
        }
    }

    /**
     * @param fname
     *            the fname to set
     */
    public void setFName(String fname) {
        m_fname = fname;
    }

    /**
     * @param implementors
     *            the implementors to set
     */
    public void setImplementors(ArrayList<BKSNode> implementors) {
        m_implementors = implementors;
    }

    /**
     * @param implements1
     *            the implements to set
     */
    public void setImplements(ArrayList<BKSNode> implements1) {
        m_implements = implements1;
    }

    /**
     * @param owners
     *            the owners to set
     */
    public void setOwners(ArrayList<BKSNode> owners) {
        m_owners = owners;
    }

    /**
     * @param owns
     *            the owns to set
     */
    public void setOwns(ArrayList<BKSNode> owns) {
        m_owns = owns;
    }

    /**
     * @param isPhantom
     *            the isPhantom to set
     */
    public void setPhantom(boolean isPhantom) {
        m_isPhantom = isPhantom;
    }

    /**
     * @param referees
     *            the referees to set
     */
    public void setReferees(ArrayList<BKSNode> referees) {
        m_referees = referees;
    }

    /**
     * @param references
     *            the references to set
     */
    public void setReferences(ArrayList<BKSNode> references) {
        m_references = references;
    }

    /**
     * @param resource
     *            the resource to set
     */
    public void setResource(File resource) {
        m_resource = resource;
    }

    /**
     * @param sname
     *            the sname to set
     */
    public void setSName(String sname) {
        m_sname = sname;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(NodeType type) {
        m_type = type;
    }

    /**
     * @param uID
     *            the uID to set
     */
    public void setUID(String uID) {
        m_UID = uID;
    }

    @Override
    public String toString() {
        if (m_isPhantom)
            return "PHANTOM NODE - Type:" + m_type + ", UID:'" + m_UID + "', sName:'" + m_sname + "', fName:'" + m_fname + "', file:'" + m_resource + "'";
        else
            return "Type:" + m_type + ", UID:'" + m_UID + "', sName:'" + m_sname + "', fName:'" + m_fname + "', file:'" + m_resource + "'";
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(m_type);
        out.writeUTF(m_UID);
        out.writeUTF(m_sname);
        out.writeUTF(m_fname);
        if (m_resource != null)
            out.writeUTF(m_resource.getAbsolutePath());
        else
            out.writeUTF("");
        out.writeBoolean(m_isPhantom);

        _writeCollection(m_references, out);
        _writeCollection(m_referees, out);
        _writeCollection(m_implements, out);
        _writeCollection(m_implementors, out);
        _writeCollection(m_owns, out);
        _writeCollection(m_owners, out);
    }

    private void _readCollection(ArrayList<BKSNode> col, ObjectInput in) throws Exception {

        int size = in.readInt();
        for (int n = 0; n < size; n++) {
            NodeType type = (NodeType) in.readObject();
            String uid = in.readUTF();

            BKSNode node = NodeRegistry.getByUID(uid);
            if (node == null) {
                node = new RExtRef(type, uid);
                NodeRegistry.add(node);
            }
            col.add(node);
        }
    }

    private void _writeCollection(ArrayList<BKSNode> col, ObjectOutput out) throws IOException {
        out.writeInt(col.size());
        for (BKSNode node : col) {
            out.writeObject(node.m_type);
            out.writeUTF(node.m_UID);
        }
    }
}
