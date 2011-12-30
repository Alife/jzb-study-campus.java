/**
 * 
 */
package com.jzb.ja.map;

import java.util.HashMap;
import java.util.StringTokenizer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import com.jzb.ja.map.data.DataFactory;

/**
 * @author n63636
 * 
 */
public class CompMapper {

    public static abstract class BaseInnerCompMapper {

        public MapData[] m_mappings;

        public BaseInnerCompMapper() {
        }

        public abstract void map(java.util.HashMap dataIn, java.util.HashMap dataOut) throws Exception;
    };

    private MapData[]           m_mappings;
    private BaseInnerCompMapper m_innerCompMapper;

    public CompMapper(MapData[] mappings) throws Exception {
        compileInfo();
    }

    private void compileInfo() throws Exception {

        m_mappings = DataFactory.createMapper();
        String methodCode = _createMethodCode();

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("java.util");
        pool.importPackage("com.jzb.ja.map");
        CtClass bcc = pool.getCtClass("com.jzb.ja.map.CompMapper$BaseInnerCompMapper");
        CtClass cc = pool.makeClass("InnerCompMapper", bcc);
        CtMethod cm = CtNewMethod.make(methodCode, cc);
        cc.addMethod(cm);
        Class c = cc.toClass();

        m_innerCompMapper = (BaseInnerCompMapper) c.newInstance();
        m_innerCompMapper.m_mappings = m_mappings;
    }

    private String _createMethodCode() {

        String methodCode = "public void map(java.util.HashMap dataIn, java.util.HashMap dataOut) throws Exception {\n";
        methodCode += "String mapLine = \"\";\n";
        methodCode += "Object o1;\n";
        methodCode += "TD td;\n";
        
                
        methodCode += "try {\n";

        int mapIndex = 0;
        for (MapData mapping : m_mappings) {

            String line;
            int items;
            StringTokenizer st;

            methodCode += "\nmapLine = \"" + mapping.toString() + "\";\n";

            // -------------------------------------------------
            line = "dataIn";
            items = 0;
            st = new StringTokenizer(mapping.origin, ".");
            while (st.hasMoreTokens()) {
                if (items > 0) {
                    line = "((HashMap)" + line + ")";
                }
                line += ".get(\"" + st.nextToken() + "\")";
                items++;
            }
            methodCode += "o1 = " + line + ";\n";

            // -------------------------------------------------
            line = "dataOut";
            items = 0;
            st = new StringTokenizer(mapping.dest, ".");
            while (st.hasMoreTokens()) {
                if (items > 0) {
                    line = "((HashMap)" + line + ")";
                }
                line += ".get(\"" + st.nextToken() + "\")";
                items++;
            }
            methodCode += "td = (TD)" + line + ";\n";

            // -------------------------------------------------
            methodCode += "td.setValue(m_mappings[" + mapIndex + "].converter.convertFrom(o1));\n";
            mapIndex++;
        }

        methodCode += "\n} catch(Exception ex) {\n";
        methodCode += "  throw new Exception(\"Error mapping: \"+mapLine,ex);";
        methodCode += "\n}";
        methodCode += "\n}";

        // System.out.println(methodCode);

        return methodCode;
    }

    public void map(HashMap dataIn, HashMap dataOut) throws Exception {
        m_innerCompMapper.map(dataIn, dataOut);
        // System.out.println("Mapper done!");
    }
}
