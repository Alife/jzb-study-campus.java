/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class Component extends BKSNode {

    public static String calcUID(String pkgName, String compName) {
        return pkgName + "#" + compName;
    }

    public static Component createPhantom(String pkgName, String compName, File refResource) {
        return new Component(pkgName, compName, refResource, true);
    }

    public Component() {
    }

    public Component(String pkgName, String compName, File resource) {
        super(NodeType.Component, calcUID(pkgName, compName), compName, calcUID(pkgName, compName), resource, false);
    }

    private Component(String pkgName, String compName, File refResource, boolean phantom) {
        super(NodeType.Component, calcUID(pkgName, compName), compName, calcUID(pkgName, compName), refResource, phantom);
    }

}
