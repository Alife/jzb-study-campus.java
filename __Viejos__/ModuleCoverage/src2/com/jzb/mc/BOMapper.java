/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.mc;

import java.util.HashSet;

import com.jzb.model.BOImplementation;
import com.jzb.model.BusinessOperation;
import com.jzb.model.EntityRegistry;

/**
 * @author PS00A501
 */
public class BOMapper {
    
    public void mapBOs() throws Exception {
        for(BOImplementation boi:EntityRegistry.getInstance().getBOIs()) {
            if(boi.getImplementedBO()!=null) {
                HashSet<BOImplementation> visited = new HashSet<BOImplementation>();
                getReferences(boi, boi.getImplementedBO(), visited);
            }
        }
    }
    
    private void getReferences(BOImplementation boi, BusinessOperation rootBO, HashSet<BOImplementation> visited) throws Exception {
        if(boi==null) return;
        visited.add(boi);
        
        if(boi.getReferencedBO_list()!=null)
            rootBO.addBORef_list(boi.getReferencedBO_list());
        
        for(BOImplementation used:boi.getUsedBOI_list()) {
            if(!visited.contains(used)) {
                getReferences(used,rootBO, visited);
            }
        }
    }
}
