/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class CtxBean extends BKSNode {

    public static String calcUID(String pkg, String name) {
        return pkg + "." + name;
    }

    public static CtxBean createPhantom(String fname, File refResource) {
        return new CtxBean(fname, refResource, true);
    }

    public CtxBean() {
    }

    public CtxBean(String pkg, String name, File resource) {
        super(NodeType.CtxBean, calcUID(pkg, name), name, calcUID(pkg, name), resource, false);
    }

    private CtxBean(String fname, File refResource, boolean phantom) {
        super(NodeType.CtxBean, fname, fname, fname, refResource, phantom);
    }

}
