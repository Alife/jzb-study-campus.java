/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author PS00A501
 */
public class BusinessOperation extends BaseEntity {

    private String                     m_facade;

    private String                     m_component;

    private String                     m_method;

    private String                     m_methodID;

    private HashSet<BusinessOperation> m_BORefs     = new HashSet<BusinessOperation>();

    private HashMap<String, String>    m_attributes = new HashMap<String, String>();

    public BusinessOperation(String aFacade, String aComponent, String aMethod, String aMethodID) {
        this(true, aFacade, aComponent, aMethod, aMethodID);
    }

    public BusinessOperation(boolean addToReg, String aFacade, String aComponent, String aMethod, String aMethodID) {
        super(addToReg, calcID(aFacade, aComponent, aMethod, aMethodID));
        m_facade = aFacade;
        m_component = aComponent;
        m_method = aMethod;
        m_methodID = aMethodID;
    }

    public static String calcID(String aFacade, String aComponent, String aMethod, String aMethodID) {
        // return aFacade + "/" + aComponent + ":" + aMethod + "[" + aMethodID + "]";
        return aMethodID;
    }

    public static BusinessOperation newInstance(String aFacade, String aComponent, String aMethod, String aMethodID) {
        String GID = BusinessOperation.calcID(aFacade, aComponent, aMethod, aMethodID);
        BusinessOperation inst = (BusinessOperation) EntityRegistry.getInstance().getBusinessOperation(GID);
        if (inst == null) {
            inst = new BusinessOperation(aFacade, aComponent, aMethod, aMethodID);
        }
        return inst;
    }

    public static BusinessOperation newInstance(boolean addToReg, String aFacade, String aComponent, String aMethod, String aMethodID) {
        String GID = BusinessOperation.calcID(aFacade, aComponent, aMethod, aMethodID);
        BusinessOperation inst = (BusinessOperation) EntityRegistry.getInstance().getBusinessOperation(GID);
        if (inst == null) {
            inst = new BusinessOperation(addToReg, aFacade, aComponent, aMethod, aMethodID);
        }
        return inst;
    }

    public static BusinessOperation searchInstance(String aComponent, String aMethod) throws Exception {
        BusinessOperation theBO = null;
        for (BusinessOperation bo : EntityRegistry.getInstance().getBOs()) {
            if (bo.getComponent().equals(aComponent) && bo.getMethod().equals(aMethod)) {
                if (theBO != null) {
                    throw new Exception("Metodo sobrecargado... no se cual es: " + aComponent + ":" + aMethod);
                }
                theBO = bo;
            }
        }
        return theBO;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "BusinessOperation  " + m_facade + "/" + m_component + ":" + m_method + "[" + m_methodID + "]";
    }

    public void addBORef(BusinessOperation bo) {
        m_BORefs.add(bo);
    }

    public void addBORef_list(ArrayList<BusinessOperation> bos) {
        if (bos.size() > 0) {
            m_BORefs.addAll(bos);
        }
    }

    public HashSet<BusinessOperation> getBORefs() {
        return m_BORefs;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return m_component;
    }

    /**
     * @return the facade
     */
    public String getFacade() {
        return m_facade;
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

    public void setAttribute(String name, String value) {
        m_attributes.put(name, value);
    }

    public String getAttribute(String name) {
        return m_attributes.get(name);
    }

    /**
     * @param facade
     *            the facade to set
     */
    public void setFacade(String facade) {
        m_facade = facade;
    }
}
