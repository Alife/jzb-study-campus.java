/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class LoadersRegistry {

    private static class Ext_LRule implements LRule {

        private String  m_exts[];
        private ILoader m_loader;

        public Ext_LRule(ILoader loader, String... exts) {
            m_loader = loader;
            m_exts = new String[exts.length];
            for (int n = 0; n < exts.length; n++) {
                m_exts[n] = exts[n].toLowerCase();
            }
        }

        public boolean apply(File prjFolder, File resource) {
            if (resource == null)
                return false;

            String fname = resource.getName().toLowerCase();
            for (String ext : m_exts) {
                if (fname.endsWith(ext))
                    return true;
            }

            return false;
        }

        public ILoader getLoader() {
            return m_loader;
        }
    }

    private static class ExtCont_LRule implements LRule {

        private String  m_exts[];
        private String  m_idStr;
        private ILoader m_loader;

        public ExtCont_LRule(ILoader loader, String idStr, String... exts) {
            m_loader = loader;
            m_idStr = idStr;
            m_exts = new String[exts.length];
            for (int n = 0; n < exts.length; n++) {
                m_exts[n] = exts[n].toLowerCase();
            }
        }

        public boolean apply(File prjFolder, File resource) {
            if (resource == null)
                return false;

            String fname = resource.getName().toLowerCase();
            for (String ext : m_exts) {
                if (fname.endsWith(ext)) {
                    return _checkFileHeader(resource);
                }
            }

            return false;
        }

        public ILoader getLoader() {
            return m_loader;
        }

        private boolean _checkFileHeader(File resource) {

            try {
                char buffer[] = new char[256];
                FileReader fr = new FileReader(resource);
                int l = fr.read(buffer);
                fr.close();
                StringBuffer sb = new StringBuffer(l);
                sb.append(buffer);
                return sb.indexOf(m_idStr) >= 0;
            } catch (Exception ex) {
                Tracer._error("Reading VegaElement type from file: " + resource, ex);
                return false;
            }
        }

    }

    private static interface LRule {

        public boolean apply(File prjFolder, File resource);

        public ILoader getLoader();
    }

    private static ArrayList<LRule> s_loaders = new ArrayList<LRule>();

    static {
        _init();
    }

    public static ILoader getLoader(File prjFolder, File resource) {
        for (LRule rule : s_loaders) {
            if (rule.apply(prjFolder, resource))
                return rule.getLoader();
        }
        return null;
    }

    private static void _init() {
        s_loaders.add(new ExtCont_LRule(new FInterface_Loader(), "<facadeComponent", ".xmlvb"));
        s_loaders.add(new ExtCont_LRule(new Component_Loader(), "<appAdapterComponent", ".xmlvb"));
        s_loaders.add(new ExtCont_LRule(new Component_Loader(), "<sqlComponent", ".xmlvb"));
        s_loaders.add(new ExtCont_LRule(new Component_Loader(), "<javaComponent", ".xmlvb"));
        s_loaders.add(new ExtCont_LRule(new CtxBean_Loader(), "<contextBean", ".xmlvb"));
        s_loaders.add(new ExtCont_LRule(new BException_Loader(), "<businessException", ".xmlvb"));
        s_loaders.add(new Ext_LRule(new OP_Loader(), ".xmlopp"));
        s_loaders.add(new Ext_LRule(new OI_Loader(), ".xmlopi"));
        s_loaders.add(new Ext_LRule(new Scenario_Loader(), ".xmlscen"));
        s_loaders.add(new Ext_LRule(new Facade_Loader(), ".xmlfac"));

        s_loaders.add(new ExtCont_LRule(new PreBin_BLBlock_Loader(), "<blBlock", ".xml"));

    }

}
