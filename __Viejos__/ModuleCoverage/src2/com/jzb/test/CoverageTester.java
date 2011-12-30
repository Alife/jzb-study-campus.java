/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.test;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.jzb.mc.BOMapper;
import com.jzb.model.BusinessOperation;
import com.jzb.model.EntityRegistry;
import com.jzb.readers.DependentProjectsReader;
import com.jzb.readers.ProjectReader;

/**
 * @author PS00A501
 */
public class CoverageTester {

    private String m_outputFile2N;
    private String m_outputFile3N;
    
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            CoverageTester me = new CoverageTester();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        m_outputFile2N="c:/temp/grafo/depen_2N.xgml";
        m_outputFile3N="c:/temp/grafo/depen_3N.xgml";
        
        File wkspPrjFile = new File("C:\\bks_ws\\SPP\\SPPHI_ENS");
        
        loadBusinessOperations(wkspPrjFile);
        loadBOImplementations(wkspPrjFile);
        
        BOMapper boMapper = new BOMapper();
        boMapper.mapBOs();
        
        //new TestFrame().show(true);
        printDependencies();
    }
    
    private void loadBusinessOperations(File wkspPrjFile) throws Exception {
        DependentProjectsReader dpreader = new DependentProjectsReader(true, false);
        HashSet prjSet=dpreader.getPrjSet(wkspPrjFile);
        
        ProjectReader prjReader = new ProjectReader(); 
        for(Iterator iter=prjSet.iterator();iter.hasNext();) {
            File prjBase = (File)iter.next();
            System.out.println(prjBase);
            prjReader.readPrjElements(prjBase);
        }
        EntityRegistry.getInstance().resolveFacadeIDs();
    }
    
    private void loadBOImplementations(File wkspPrjFile) throws Exception {
        DependentProjectsReader dpreader = new DependentProjectsReader(false, true);
        HashSet prjSet=dpreader.getPrjSet(wkspPrjFile);
        
        ProjectReader prjReader = new ProjectReader(); 
        for(Iterator iter=prjSet.iterator();iter.hasNext();) {
            File prjBase = (File)iter.next();
            System.out.println(prjBase);
            prjReader.readPrjElements(prjBase);
        }
        EntityRegistry.getInstance().resolveIDs();
        EntityRegistry.getInstance().resolvePresentationLogicIDs();
    }
    
    private void printDependencies() throws Exception {
        printDependencies3N();
        printDependencies2N();
    }
    
    private void printDependencies2N() throws Exception {
        FileWriter fwout=new FileWriter(m_outputFile2N);
        
        fwout.write("<section name=\"xgml\">\n");
        fwout.write("    <attribute key=\"Creator\" type=\"String\">yFiles</attribute>\n");
        fwout.write("    <attribute key=\"Version\" type=\"String\">2.4.2.2</attribute>\n");
        fwout.write("    <section name=\"graph\">\n");
        fwout.write("        <attribute key=\"hierarchic\" type=\"int\">1</attribute>\n");
        fwout.write("        <attribute key=\"label\" type=\"String\"></attribute>\n");
        fwout.write("        <attribute key=\"directed\" type=\"int\">1</attribute>\n");

        fwout.write("\n");
        printFacadeGroups(fwout);

        Collection<BusinessOperation> IFs=consolidateIFs();
        
        fwout.write("\n");
        int index=1;
        for(BusinessOperation bo:IFs) {
            bo.setAttribute("index", Integer.toString(index++));
            printNode(fwout, bo);
        }
        fwout.write("\n");
        for(BusinessOperation boOrig:IFs) {
            for(BusinessOperation boDest:boOrig.getBORefs()) {
                printEdge(fwout, boOrig.getAttribute("index"), boDest.getAttribute("index"));
            }
        }
        fwout.write("\n");
        fwout.write("    </section>\n");
        fwout.write("</section>\n");
        fwout.flush();
        fwout.close();
    }
    
    private Collection<BusinessOperation> consolidateIFs() {
        
        HashMap<String, BusinessOperation> IFs = new HashMap<String, BusinessOperation>();

        for(BusinessOperation bo:EntityRegistry.getInstance().getBOs()) {
            BusinessOperation ifBO=getIFBO(bo,IFs);
            for(BusinessOperation refBO:bo.getBORefs()) {
                ifBO.addBORef(getIFBO(refBO,IFs));
            }
        }
        
        return IFs.values();
    }
    
    private BusinessOperation getIFBO(BusinessOperation bo, HashMap<String, BusinessOperation> IFs) {
        String key="#IF#"+bo.getComponent();
        BusinessOperation ifBO=IFs.get(key);
        if(ifBO==null) {
            int pos=1+bo.getComponent().lastIndexOf('.');
            String methodName;
            if(pos>0) 
                methodName = bo.getComponent().substring(pos);
            else 
                methodName = bo.getComponent();
            ifBO = BusinessOperation.newInstance(false, bo.getFacade(),bo.getComponent(), methodName, key);
            ifBO.setAttribute("indexIF", bo.getAttribute("indexFacade"));
            IFs.put(key, ifBO);
        }
        return ifBO;
    }
    
    private void printDependencies3N() throws Exception {
        FileWriter fwout=new FileWriter(m_outputFile3N);
        
        fwout.write("<section name=\"xgml\">\n");
        fwout.write("    <attribute key=\"Creator\" type=\"String\">yFiles</attribute>\n");
        fwout.write("    <attribute key=\"Version\" type=\"String\">2.4.2.2</attribute>\n");
        fwout.write("    <section name=\"graph\">\n");
        fwout.write("        <attribute key=\"hierarchic\" type=\"int\">1</attribute>\n");
        fwout.write("        <attribute key=\"label\" type=\"String\"></attribute>\n");
        fwout.write("        <attribute key=\"directed\" type=\"int\">1</attribute>\n");

        fwout.write("\n");
        printFacadeGroups(fwout);

        fwout.write("\n");
        printIFGroups(fwout);

        fwout.write("\n");
        int index=1;
        for(BusinessOperation bo:EntityRegistry.getInstance().getBOs()) {
            bo.setAttribute("index", Integer.toString(index++));
            printNode(fwout, bo);
        }
        fwout.write("\n");
        for(BusinessOperation boOrig:EntityRegistry.getInstance().getBOs()) {
            for(BusinessOperation boDest:boOrig.getBORefs()) {
                printEdge(fwout, boOrig.getAttribute("index"), boDest.getAttribute("index"));
            }
        }
        fwout.write("\n");
        fwout.write("    </section>\n");
        fwout.write("</section>\n");
        fwout.flush();
        fwout.close();
    }
    
    private void printFacadeGroups(FileWriter fwout) throws Exception {
        
        HashMap<String, String> facadeIndexes = new HashMap<String, String>();
        int currentIndex=10000;
        for(BusinessOperation bo:EntityRegistry.getInstance().getBOs()) {
            
            String index=facadeIndexes.get(bo.getFacade());
            if(index==null) {
                index=Integer.toString(currentIndex++);
                facadeIndexes.put(bo.getFacade(),index);
                printGroupNode(fwout, index, bo.getFacade(),null);
            }
            bo.setAttribute("indexFacade", index);
        }
    }        
    
    private void printIFGroups(FileWriter fwout) throws Exception {
        
        HashMap<String, String> ifIndexes = new HashMap<String, String>();
        int currentIndex=50000;
        for(BusinessOperation bo:EntityRegistry.getInstance().getBOs()) {
            
            String index=ifIndexes.get(bo.getComponent());
            if(index==null) {
                index=Integer.toString(currentIndex++);
                ifIndexes.put(bo.getComponent(),index);
                printGroupNode(fwout, index, bo.getComponent(),bo.getAttribute("indexFacade"));
            }
            bo.setAttribute("indexIF", index);
        }
    }
    
    private void printGroupNode(FileWriter fwout, String id, String label, String parentID) throws Exception {
        
        fwout.write("    <section name=\"node\">\n");
        fwout.write("       <attribute key=\"id\" type=\"int\">"+id+"</attribute>\n");
        fwout.write("       <attribute key=\"label\" type=\"String\">"+label+"</attribute>\n");
        fwout.write("       <section name=\"graphics\">\n");
//        fwout.write("           <attribute key=\"x\" type=\"double\">411.875</attribute>\n");
//        fwout.write("           <attribute key=\"y\" type=\"double\">15.0</attribute>\n");
        fwout.write("           <attribute key=\"w\" type=\"double\">200.0</attribute>\n");
        fwout.write("           <attribute key=\"h\" type=\"double\">50.0</attribute>\n");
        fwout.write("           <attribute key=\"type\" type=\"String\">roundrectangle</attribute>\n");
        if(parentID==null)
            fwout.write("           <attribute key=\"fill\" type=\"String\">#9999FF</attribute>\n");
        else
            fwout.write("           <attribute key=\"fill\" type=\"String\">#CCFFCC</attribute>\n");
        fwout.write("           <attribute key=\"outline\" type=\"String\">#000000</attribute>\n");
        fwout.write("       </section>\n");
        fwout.write("       <section name=\"LabelGraphics\">\n");
        fwout.write("           <attribute key=\"text\" type=\"String\">"+label+"</attribute>\n");
        fwout.write("           <attribute key=\"fontSize\" type=\"int\">10</attribute>\n");
        fwout.write("           <attribute key=\"fontName\" type=\"String\">Dialog</attribute>\n");
        fwout.write("           <attribute key=\"anchor\" type=\"String\">t</attribute>\n");
        fwout.write("       </section>\n");
        fwout.write("       <attribute key=\"isGroup\" type=\"boolean\">true</attribute>\n");
        if(parentID!=null)
            fwout.write("       <attribute key=\"gid\" type=\"int\">"+parentID+"</attribute>\n");
        fwout.write("    </section>\n");
    }
    
    private void printNode(FileWriter fwout, BusinessOperation bo) throws Exception {
        fwout.write("    <section name=\"node\">\n");
        fwout.write("       <attribute key=\"id\" type=\"int\">"+bo.getAttribute("index")+"</attribute>\n");
        fwout.write("       <attribute key=\"label\" type=\"String\">"+bo.getGID()+"</attribute>\n");
        fwout.write("       <section name=\"graphics\">\n");
//        fwout.write("           <attribute key=\"x\" type=\"double\">411.875</attribute>\n");
//        fwout.write("           <attribute key=\"y\" type=\"double\">15.0</attribute>\n");
        fwout.write("           <attribute key=\"w\" type=\"double\">100.0</attribute>\n");
        fwout.write("           <attribute key=\"h\" type=\"double\">50.0</attribute>\n");
        fwout.write("           <attribute key=\"type\" type=\"String\">roundrectangle</attribute>\n");
        fwout.write("           <attribute key=\"fill\" type=\"String\">#FFCC99</attribute>\n");
        fwout.write("           <attribute key=\"outline\" type=\"String\">#000000</attribute>\n");
        fwout.write("       </section>\n");
        fwout.write("       <section name=\"LabelGraphics\">\n");
        fwout.write("           <attribute key=\"text\" type=\"String\">"+bo.getMethod()+"</attribute>\n");
        fwout.write("           <attribute key=\"fontSize\" type=\"int\">10</attribute>\n");
        fwout.write("           <attribute key=\"fontName\" type=\"String\">Dialog</attribute>\n");
        fwout.write("           <attribute key=\"anchor\" type=\"String\">c</attribute>\n");
        fwout.write("       </section>\n");
        String parentID = bo.getAttribute("indexIF");
        if(parentID!=null)
            fwout.write("       <attribute key=\"gid\" type=\"int\">"+parentID+"</attribute>\n");
        fwout.write("    </section>\n");
    }
    
    private void printEdge(FileWriter fwout, String orig, String dest) throws Exception {
        fwout.write("           <section name=\"edge\">\n");
        fwout.write("               <attribute key=\"source\" type=\"int\">"+orig+"</attribute>\n");
        fwout.write("               <attribute key=\"target\" type=\"int\">"+dest+"</attribute>\n");
        fwout.write("               <section name=\"graphics\">\n");
        fwout.write("                   <attribute key=\"fill\" type=\"String\">#000000</attribute>\n");
        fwout.write("                   <attribute key=\"targetArrow\" type=\"String\">standard</attribute>\n");
        fwout.write("               </section>\n");
        fwout.write("           </section>\n");
    }
}
