/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class B_Ope extends BKSNode {

    public static String calcUID(String MethodUID, String QName) {
        return QName + "#" + MethodUID;
    }

    public static B_Ope createPhantom(String methodUID, String QName, File refResource) {
        return new B_Ope(methodUID, QName, refResource);
    }

    public B_Ope() {
    }

    public B_Ope(String methodUID, String methodName, String signature, String pkgName, String fiName, File resource) {
        super(NodeType.B_Ope, calcUID(methodUID, pkgName + "." + fiName), methodName + signature, pkgName + "." + fiName + "#" + methodName + signature, resource, false);
    }

    private B_Ope(String methodUID, String QName, File refResource) {
        super(NodeType.B_Ope, calcUID(methodUID, QName), "phantom", QName + "#phantom", refResource, true);
    }

}
