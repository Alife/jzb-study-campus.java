/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class I_Ope extends BKSNode {

    public static String calcUID(String oiName, String alName) {
        return alName + "#" + oiName;
    }

    public static I_Ope createPhantom(String opName, String alName, File refResource) {
        return new I_Ope(opName, alName, refResource, true);
    }

    public I_Ope() {
    }

    public I_Ope(String oiName, String alName, File resource) {
        super(NodeType.I_Ope, calcUID(oiName, alName), oiName, calcUID(oiName, alName), resource, false);
    }

    private I_Ope(String oiName, String alName, File refResource, boolean phantom) {
        super(NodeType.I_Ope, calcUID(oiName, alName), oiName, calcUID(oiName, alName), refResource, phantom);
    }

}
