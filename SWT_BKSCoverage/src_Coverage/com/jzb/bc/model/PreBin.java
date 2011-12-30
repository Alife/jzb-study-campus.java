/**
 * 
 */
package com.jzb.bc.model;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class PreBin extends BKSNode {

    public PreBin() {
    }

    public PreBin(String qname, File resource) {
        super(NodeType.PreBin, qname, qname, qname, resource, false);
    }

}
