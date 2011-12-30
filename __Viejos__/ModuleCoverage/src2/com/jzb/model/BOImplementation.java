/**
 * Rigel Services Model Infrastructure, Version 1.0
 *
 * Copyright (C) 2002 ISBAN.
 * All Rights Reserved.
 *
 **/

package com.jzb.model;

import java.util.ArrayList;

/**
 * @author PS00A501
 *
 */
public class BOImplementation extends BaseEntity {
    
    private String m_package;
    private String m_component;
    private String m_method;
    private String m_methodID;
    
    private BusinessOperation m_ImplementedBO;
    private ArrayList<BusinessOperation> m_referencedBO_list = new ArrayList<BusinessOperation>();
    private ArrayList<String> m_usedBOI_GID_list = new ArrayList<String>();
    private ArrayList<BOImplementation> m_usedBOI_list = new ArrayList<BOImplementation>();
    
    public BOImplementation(String aPackage, String aComponent, String aMethod, String aMethodID) {
        super(calcID(aPackage, aComponent, aMethod, aMethodID));
        m_package=aPackage;
        m_component=aComponent;
        m_method=aMethod;
        m_methodID=aMethodID;
    }

    public static String calcID(String aPackage, String aComponent, String aMethod, String aMethodID) {
        return aPackage+"/"+aComponent+":"+aMethod+"["+aMethodID+"]";
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "BOImplementation "+calcID(m_package, m_component, m_method, m_methodID);
    }

    /**
     * @return the implementedBO_GID
     */
    public BusinessOperation getImplementedBO() {
        return m_ImplementedBO;
    }

    /**
     * @param implementedBO_GID the implementedBO_GID to set
     */
    public void setImplementedBO(BusinessOperation implementedBO) {
        m_ImplementedBO = implementedBO;
    }

    public void addReferencedBO_GID_list(ArrayList<BusinessOperation> all) {
        if(all.size()>0) {
            m_referencedBO_list.addAll(all);
        }
    }
    
    public void addReferencedBO(BusinessOperation bo) {
        m_referencedBO_list.add(bo);
    }
    
    public void addUsedBOI_GID_list(ArrayList<String> all) {
        m_usedBOI_GID_list.addAll(all);
    }
    
    public void addUsedBOI_GID(String boi_id) {
        m_usedBOI_GID_list.add(boi_id);
    }
    
    public void addUsedBOI_list(ArrayList<BOImplementation> all) {
        m_usedBOI_list.addAll(all);
    }
    
    public void addUsedBOI(BOImplementation boi) {
        m_usedBOI_list.add(boi);
    }

    /**
     * @return the usedBOI_GID_list
     */
    public ArrayList<String> getUsedBOI_GID_list() {
        return m_usedBOI_GID_list;
    }

    /**
     * @return the usedBOI_list
     */
    public ArrayList<BOImplementation> getUsedBOI_list() {
        return m_usedBOI_list;
    }

    /**
     * @return the referencedBO_list
     */
    public ArrayList<BusinessOperation> getReferencedBO_list() {
        return m_referencedBO_list;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return m_component;
    }

    /**
     * @return the package
     */
    public String getPackage() {
        return m_package;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return m_method;
    }

    /**
     * @return the methodID
     */
    public String getMethodID() {
        return m_methodID;
    }
    
    

}
