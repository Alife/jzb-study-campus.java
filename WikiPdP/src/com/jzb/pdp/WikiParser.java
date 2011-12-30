/**
 * 
 */
package com.jzb.pdp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author PS00A501
 * 
 */
public class WikiParser {

    public  static class PrjData {

        public String             title   = "";
        public String             JP      = "";
        public String             wikireq = "";
        public String             desc    = "";
        public ArrayList<PrjData> subPrjs = new ArrayList<PrjData>();

        /**
         * @see java.lang.Object#toString()
         */
        private String toString2(String padding) {
            String s = "";

            s += padding + "title = " + title + "\n";
            s += padding + "JP = " + JP + "\n";
            s += padding + "wikireq = " + wikireq + "\n";
            s += padding + "desc = " + desc + "\n";
            for (PrjData i : subPrjs) {
                s += i.toString2("  ");
            }
            return s;
        }

        public String toString() {
            return toString2("");
        }

        private String toXML2(String padding) {
            String s = "";
            s += padding + "<prj>\n";
            s += padding + "  <title>" + title + "</title>\n";
            s += padding + "  <JP>" + JP + "</JP>\n";
            s += padding + "  <wikireq>" + wikireq + "</wikireq>\n";
            s += padding + "  <desc><![CDATA[" + desc + "]]><desc>\n";
            if (subPrjs.size() > 0) {
                s += padding + "  <subPrjs>\n";
                for (PrjData i : subPrjs) {
                    s += i.toXML2("  ");
                }
                s += padding + "  </subPrjs>\n";
            }
            s += padding + "</prj>\n";
            return s;
        }

        public String toXML() {
            return toXML2("");
        }

    }

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
            WikiParser me = new WikiParser();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    private PrjData readProject(BufferedReader br) throws Exception {
        PrjData p = new PrjData();
        for (;;) {
            String line = br.readLine();
            if (line.equals("}}"))
                break;
            if (line.startsWith("|Titulo ="))
                p.title = line.substring(9).trim();
            if (line.startsWith("|JP = "))
                p.JP = line.substring(5).trim();
            if (line.startsWith("|WikiReq ="))
                p.wikireq = line.substring(10).trim();
        }
        return p;
    }

    private String trimNL(String s) {
        int n = s.length() - 1;
        while (n > 0) {
            if (s.charAt(n) != '\n')
                break;
            n--;
        }
        if(n<0) n=0;
        return s.substring(0, n);
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

        ArrayList<PrjData> prjs = parseData();
        printWikiText_FMT2(prjs);
    }

    public ArrayList<PrjData> parseData() throws Exception {

        String line;
        PrjData currentPrj = null;
        PrjData parentPrj = null;
        ArrayList<PrjData> prjs = new ArrayList<PrjData>();

        BufferedReader br = new BufferedReader(new FileReader("D:\\JZarzuela\\D_S_Escritorio\\wikitext_mod.txt"));

        while (br.ready()) {
            line = br.readLine().trim();
            if (line.equals("{{ProyectoPDP")) {
                parentPrj = null;
                currentPrj = readProject(br);
                prjs.add(currentPrj);
            } else if (line.equals("{{ProyectoPDP2")) {
                if (parentPrj == null)
                    parentPrj = currentPrj;
                currentPrj = readProject(br);
                parentPrj.subPrjs.add(currentPrj);
            } else {
                currentPrj.desc += line + "\n";
            }
        }

        for(PrjData i:prjs) {
            i.desc=trimNL(i.desc);
            for(PrjData j:i.subPrjs) j.desc=trimNL(j.desc);
        }
        
        br.close();

        return prjs;
    }

    //-------------------------------------------------------------------------------
    private void printWikiText_FMT0(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");
        System.out.println("{| cellpadding=\"4\" cellspacing=\"0\" style=\"border:1px solid #A0A0A0\"");
        System.out.println("|- style=\"text-align:center; background:#E0E0E0\"");
        System.out.println("| '''Nombre''' || '''Descripción'''");

        for (PrjData i : list) {

            if (i.subPrjs.size() == 0) {
                System.out.println("\n\n|- valign=\"center\" style=\"text-align:left;\"");
                System.out.println("| style=\"border-top:1px solid #A0A0A0\" |");
                System.out.println("<h3>"+i.title+"</h3>");
                System.out.println(": JP: "+i.JP);
                System.out.println("| style=\"border-top:1px solid #A0A0A0; border-left:1px solid #A0A0A0;\" |");
                System.out.println(i.desc);
            }
            else {
                System.out.println("\n\n|- valign=\"top\" style=\"text-align:left;\"");
                System.out.println("| style=\"border-top:1px solid #A0A0A0\" |");
                System.out.println("<h3>"+i.title+"</h3>");
                System.out.println("| style=\"border-top:1px solid #A0A0A0; border-left:1px solid #A0A0A0;\" | &nbsp;");
                for (PrjData j : i.subPrjs) {
                    System.out.println("\n\n|- valign=\"center\" style=\"text-align:left;\"");
                    System.out.println("| style=\"border:0px solid #A0A0A0\" |");
                    System.out.println(": <h4>"+j.title+"</h4>");
                    System.out.println(":: "+j.JP);
                    System.out.println("| style=\"border-top:1px solid #A0A0A0; border-left:1px solid #A0A0A0;\" |");
                    System.out.println(j.desc);
                }
            }
        }

        System.out.println("|}");
    }

    //-------------------------------------------------------------------------------
    private void printWikiText_FMT1(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");
        System.out.println("{| cellpadding=\"4\" cellspacing=\"0\" style=\"border:1px solid #A0A0A0\"");
        System.out.println("|- style=\"text-align:center; background:#E0E0E0\"");
        System.out.println("| '''Nombre''' || '''Descripción'''");

        for (PrjData i : list) {

            if (i.subPrjs.size() == 0) {
                System.out.println("{{FMT1_PRJ_PDP1");
                System.out.println("| Titulo = "+i.title);
                System.out.println("| JP = "+i.JP);
                System.out.println("| Descripcion = \n"+i.desc);
                System.out.println("}}");
            }
            else {
                System.out.println("{{FMT1_PRJ_PDP2");
                System.out.println("| Titulo = "+i.title);
                System.out.println("| JP = "+i.JP);
                System.out.println("| Descripcion = \n"+i.desc);
                System.out.println("}}");
                for (PrjData j : i.subPrjs) {
                    System.out.println("{{FMT1_PRJ_PDP3");
                    System.out.println("| Titulo = "+j.title);
                    System.out.println("| JP = "+j.JP);
                    System.out.println("| Descripcion = \n"+j.desc);
                    System.out.println("}}");
                }
            }
        }

        System.out.println("|}");
    }
    
    //-------------------------------------------------------------------------------
    private void printWikiText_FMT2(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");

        for (PrjData i : list) {

            if (i.subPrjs.size() == 0) {
                System.out.println("{{FMT2_PRJ_PDP1");
                System.out.println("| Titulo = "+i.title);
                System.out.println("| JP = "+i.JP);
                System.out.println("| Descripcion = \n"+i.desc);
                System.out.println("}}\n\n");
            }
            else {
                System.out.println("{{FMT2_PRJ_PDP2");
                System.out.println("| Titulo = "+i.title);
                System.out.println("| JP = "+i.JP);
                System.out.println("| Descripcion = \n"+i.desc);
                System.out.println("}}\n\n");
                for (PrjData j : i.subPrjs) {
                    System.out.println("{{FMT2_PRJ_PDP3");
                    System.out.println("| Titulo = "+j.title);
                    System.out.println("| JP = "+j.JP);
                    System.out.println("| Descripcion = \n"+j.desc);
                    System.out.println("}}\n\n");
                }
            }
        }
    }

    //-------------------------------------------------------------------------------
    private void printWikiText_FMT3(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");

        for (PrjData i : list) {

            if (i.subPrjs.size() == 0) {
                System.out.println("{{FMT3_PRJ_PDP1");
                System.out.println("| Titulo = "+i.title);
                System.out.println("| JP = "+i.JP);
                System.out.println("| Descripcion = \n"+i.desc);
                System.out.println("}}\n\n");
            }
            else {
                System.out.println("{{FMT3_PRJ_PDP2");
                System.out.println("| Titulo = "+i.title);
                System.out.println("| JP = "+i.JP);
                System.out.println("| Descripcion = \n"+i.desc);
                System.out.println("}}\n\n");
                for (PrjData j : i.subPrjs) {
                    System.out.println("{{FMT3_PRJ_PDP3");
                    System.out.println("| Titulo = "+j.title);
                    System.out.println("| JP = "+j.JP);
                    System.out.println("| Descripcion = \n"+j.desc);
                    System.out.println("}}\n\n");
                }
            }
        }
    }


}
