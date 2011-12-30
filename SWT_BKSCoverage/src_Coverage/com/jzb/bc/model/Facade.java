/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class Facade extends BKSNode {

    public static String calcUID(String pkgName, String facName) {
        return pkgName + "#" + facName;
    }

    public static Facade createPhantom(String pkgName, String facName, File refResource) {
        return new Facade(pkgName, facName, refResource, true);
    }

    public Facade() {
    }

    public Facade(String pkgName, String facName, File resource) {
        super(NodeType.Facade, calcUID(pkgName, facName), facName, calcUID(pkgName, facName), resource, false);
    }

    private Facade(String pkgName, String fiName, File refResource, boolean phantom) {
        super(NodeType.Facade, calcUID(pkgName, fiName), fiName, calcUID(pkgName, fiName), refResource, phantom);
    }

}
