/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class CMethod extends BKSNode {

    public static String calcUID(String MethodUID, String QName) {
        return QName + "#" + MethodUID;
    }

    public static CMethod createPhantom(String methodUID, String QName, File refResource) {
        return new CMethod(methodUID, QName, refResource);
    }

    public CMethod() {
    }

    public CMethod(String methodUID, String methodName, String signature, String pkgName, String compName, File resource) {
        super(NodeType.CMethod, calcUID(methodUID, pkgName + "." + compName), methodName + signature, pkgName + "." + compName + "#" + methodName + signature, resource, false);
    }

    private CMethod(String methodUID, String QName, File refResource) {
        super(NodeType.CMethod, calcUID(methodUID, QName), "phantom", QName + "#phantom", refResource, true);
    }

}
