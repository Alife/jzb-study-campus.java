/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

/**
 * @author n63636
 * 
 */
public class PBCMethod extends BKSNode {

    private String            m_BOpe_UID;

    private ArrayList<String> m_FInterfaces;

    public static String calcUID(String MethodUID, String QName) {
        return QName + "#" + MethodUID;
    }

    public PBCMethod() {
    }

    public PBCMethod(String methodUID, String methodName, String pkgName, String compName, String BOpe_UID, ArrayList<String> FInterfaces, File resource) {
        super(NodeType.PBCMethod, calcUID(methodUID, pkgName + "." + compName), methodName + "(PreBin)", pkgName + "." + compName + "#" + methodName + "(PreBin)", resource, false);
        m_BOpe_UID = BOpe_UID;
        m_FInterfaces = FInterfaces;
    }

    /**
     * @return the bOpe_UID
     */
    public String getBOpe_UID() {
        return m_BOpe_UID;
    }

    /**
     * @return the fInterfaces
     */
    public ArrayList<String> getFInterfaces() {
        return m_FInterfaces;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        m_BOpe_UID = in.readUTF();

        m_FInterfaces = new ArrayList<String>();
        int len = in.readInt();
        for (int n = 0; n < len; n++) {
            String s = in.readUTF();
            m_FInterfaces.add(s);
        }
    }

    /**
     * @param bOpeUID
     *            the bOpe_UID to set
     */
    public void setBOpe_UID(String bOpeUID) {
        m_BOpe_UID = bOpeUID;
    }

    /**
     * @param fInterfaces
     *            the fInterfaces to set
     */
    public void setFInterfaces(ArrayList<String> fInterfaces) {
        m_FInterfaces = fInterfaces;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeUTF(m_BOpe_UID);
        if (m_FInterfaces == null) {
            out.writeInt(0);
        } else {
            out.writeInt(m_FInterfaces.size());
            for (String s : m_FInterfaces) {
                out.writeUTF(s);
            }
        }
    }

}
