/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class FInterface extends BKSNode {

    public static String calcUID(String pkgName, String fiName) {
        return pkgName + "#" + fiName;
    }

    public static FInterface createPhantom(String pkgName, String fiName, File refResource) {
        return new FInterface(pkgName, fiName, refResource, true);
    }

    public FInterface() {
    }

    public FInterface(String pkgName, String fiName, File resource) {
        super(NodeType.FInterface, calcUID(pkgName, fiName), fiName, calcUID(pkgName, fiName), resource, false);
    }

    private FInterface(String pkgName, String fiName, File refResource, boolean phantom) {
        super(NodeType.FInterface, calcUID(pkgName, fiName), fiName, calcUID(pkgName, fiName), refResource, phantom);
    }

}
