/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class Scenario extends BKSNode {

    public static Scenario createPhantom(String name, File refResource) {
        return new Scenario(name, refResource, true);
    }

    public Scenario() {
    }

    public Scenario(String name, File resource) {
        super(NodeType.Scenario, name, name, name, resource, false);
    }

    private Scenario(String name, File refResource, boolean phantom) {
        super(NodeType.Scenario, name, name, name, refResource, phantom);
    }

}
