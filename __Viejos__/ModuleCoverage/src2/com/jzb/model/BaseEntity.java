/**
 * Rigel Services Model Infrastructure, Version 1.0
 *
 * Copyright (C) 2002 ISBAN.
 * All Rights Reserved.
 *
 **/

package com.jzb.model;

/**
 * @author PS00A501
 *
 */
public class BaseEntity {
    
    private String m_GID;
    
    public BaseEntity(String GID) {
        this(true, GID);
    }
    
    public BaseEntity(boolean addToResgitry, String GID) {
        m_GID=GID;
        if(addToResgitry) EntityRegistry.getInstance().addEntity(this);
    }
    
    public String getGID() {
        return m_GID;
    }
}
