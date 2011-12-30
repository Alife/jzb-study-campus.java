/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * @author PS00A501
 */
public class EntityRegistry {

    private HashMap<String, ArrayList<String>> m_facadeRefs = new HashMap<String, ArrayList<String>>();
    
    private HashSet<String> m_scenarioRefs = new HashSet<String>();

    private HashMap<String, BusinessOperation> m_bos = new HashMap<String, BusinessOperation>();

    private HashMap<String, BOImplementation> m_bois = new HashMap<String, BOImplementation>();

    private static EntityRegistry s_instance = new EntityRegistry();

    /**
     * @return the instance
     */
    public static EntityRegistry getInstance() {
        return s_instance;
    }

    public void addBusinessOperation(BusinessOperation entity) {
        if (m_bos.containsKey(entity.getGID())) {
            System.out.println("**** Ya existe BO con esa clave: " + entity);
        }
        m_bos.put(entity.getGID(), entity);
    }

    public BusinessOperation getBusinessOperation(String GID) {
        return m_bos.get(GID);
    }

    public void addBOImplementation(BOImplementation entity) {
        if (m_bois.containsKey(entity.getGID())) {
            System.out.println("**** Ya existe BOI con esa clave: " + entity);
        }
        m_bois.put(entity.getGID(), entity);
    }

    /**
     * @return the bois
     */
    public Collection<BOImplementation> getBOIs() {
        return m_bois.values();
    }

    /**
     * @return the bos
     */
    public Collection<BusinessOperation> getBOs() {
        return m_bos.values();
    }

    public BOImplementation getBOImplementation(String GID) {
        return m_bois.get(GID);
    }

    public void addEntity(BaseEntity entity) {
        if (entity instanceof BusinessOperation)
            m_bos.put(entity.getGID(), (BusinessOperation) entity);
        else if (entity instanceof BOImplementation)
            m_bois.put(entity.getGID(), (BOImplementation) entity);
        else
            System.out.println("Tipo de entidad desconocida:" + entity);
    }

    public void removeEntity(BaseEntity entity) {
        if (entity instanceof BusinessOperation)
            m_bos.remove(entity.getGID());
        else if (entity instanceof BOImplementation)
            m_bois.remove(entity.getGID());
        else
            System.out.println("Tipo de entidad desconocida:" + entity);
    }
    
    public void addFacadeRef(String facadeName, String ifName) {
        ArrayList<String> refs=m_facadeRefs.get(facadeName);
        if(refs==null) {
            refs=new ArrayList<String>();
            m_facadeRefs.put(facadeName, refs);
        }
        refs.add(ifName);
    }
    
    public void addScenarioRef(String op_oi_name) {
        m_scenarioRefs.add(op_oi_name);
    }
    
    
    public void resolvePresentationLogicIDs() {

        HashSet<BOImplementation> rootOPs = new HashSet<BOImplementation>();
        HashSet<BOImplementation> refOPs = new HashSet<BOImplementation>();
        
        for(BOImplementation boi: m_bois.values()) {
            if(!boi.getMethod().equals("OP_init")) continue;
            String boiName=boi.getPackage()+"."+boi.getComponent();
            for(String op_oi_name: m_scenarioRefs) {
                if(boiName.equals(op_oi_name)) {
                    rootOPs.add(boi);
                    refOPs.addAll(boi.getUsedBOI_list());
                    break;
                }
            }
        }
        
        rootOPs.removeAll(refOPs);
        for(BOImplementation boi:rootOPs) {
            String boiName=boi.getPackage()+"."+boi.getComponent();
            BusinessOperation presentation=BusinessOperation.newInstance("M_LP", boi.getPackage(), boi.getComponent(), "M_LP#"+boiName);
            boi.setImplementedBO(presentation);
        }
    }

    public void VER_DEPEN_OPS_resolvePresentationLogicIDs() {

        HashSet<BOImplementation> rootOPs = new HashSet<BOImplementation>();
        HashSet<BOImplementation> refOPs = new HashSet<BOImplementation>();
        
        for(BOImplementation boi: m_bois.values()) {
            if(!boi.getMethod().equals("OP_init")) continue;
            String boiName=boi.getPackage()+"."+boi.getComponent();
            for(String op_oi_name: m_scenarioRefs) {
                if(boiName.equals(op_oi_name)) {
                    
                    BusinessOperation presentation=BusinessOperation.newInstance("M_LP", boi.getPackage(), boi.getComponent(), "M_LP#"+boiName);
                    boi.setImplementedBO(presentation);
                    
                    rootOPs.add(boi);
                    refOPs.addAll(boi.getUsedBOI_list());
                    for(BOImplementation refboi:boi.getUsedBOI_list()) {
                        String boiNameRef=refboi.getPackage()+"."+refboi.getComponent();
                        BusinessOperation presentationRef=BusinessOperation.newInstance("M_LP", refboi.getPackage(), refboi.getComponent(), "M_LP#"+boiNameRef);
                        
                        refboi.addReferencedBO(presentationRef);
                    }
                    break;
                }
            }
        }
        
        //rootOPs.removeAll(refOPs);
//        for(BOImplementation boi:rootOPs) {
//            String boiName=boi.getPackage()+"."+boi.getComponent();
//            BusinessOperation presentation=BusinessOperation.newInstance("M_LP", boi.getPackage(), boi.getComponent(), "M_LP#"+boiName);
//            boi.setImplementedBO(presentation);
//        }
    }
    
    public void resolveFacadeIDs() {
        for(Map.Entry<String, ArrayList<String>> facadeEntry:m_facadeRefs.entrySet()) {
            for(String fullName:facadeEntry.getValue()) {
                for(BusinessOperation bo:m_bos.values()) {
                    if(bo.getComponent().equals(fullName))
                        bo.setFacade(facadeEntry.getKey());
                }
            }
        }
    }
    
    
    public void resolveIDs() throws Exception {
        for (Map.Entry<String, BOImplementation> entry : m_bois.entrySet()) {
            for(String boi_GID:entry.getValue().getUsedBOI_GID_list()) {
                BOImplementation boi=m_bois.get(boi_GID);
                if(boi==null) {
                    System.out.println("*** no se encuentra elemento usado: "+boi_GID);
                }
                else {
                    entry.getValue().addUsedBOI(boi);
                }
            }
        }
    }

}
