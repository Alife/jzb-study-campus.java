/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class P_Ope extends BKSNode {

    public static String calcUID(String opName, String alName) {
        return alName + "#" + opName;
    }

    public static P_Ope createPhantom(String opName, String alName, File refResource) {
        return new P_Ope(opName, alName, refResource, true);
    }

    public P_Ope() {
    }

    public P_Ope(String opName, String alName, File resource) {
        super(NodeType.P_Ope, calcUID(opName, alName), opName, calcUID(opName, alName), resource, false);
    }

    private P_Ope(String opName, String alName, File refResource, boolean phantom) {
        super(NodeType.P_Ope, calcUID(opName, alName), opName, calcUID(opName, alName), refResource, phantom);
    }

}
