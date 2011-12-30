/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class BException extends BKSNode {

    public static String calcUID(String pkg, String name) {
        return pkg + "." + name;
    }

    public static BException createPhantom(String pkg, String name, File refResource) {
        return new BException(pkg, name, refResource, true);
    }

    public BException() {
    }

    public BException(String pkg, String name, File resource) {
        super(NodeType.BException, calcUID(pkg, name), name, calcUID(pkg, name), resource, false);
    }

    private BException(String pkg, String name, File refResource, boolean phantom) {
        super(NodeType.BException, calcUID(pkg, name), name, calcUID(pkg, name), refResource, phantom);
    }

}
