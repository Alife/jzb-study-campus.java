/**
 * 
 */
package com.jzb.nc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author n63636
 * 
 */
public class FindClones {

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
            FindClones me = new FindClones();
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

        
        File outputFolder = new File("/Users/jzarzuela/Documents/java-Campus/_Util_FindDuplicatedFiles/out");
        
        PrintStream ps = new PrintStream(new File(outputFolder,"../clones.txt"));
        PrintStream _sys_out_=System.out;
        System.setOut(ps);

        
        System.out.println("*** Procesing data");
        for (File f : outputFolder.listFiles()) {
            _readFileData(f);
        }

        ArrayList<ArrayList<String>> clones = new ArrayList<ArrayList<String>>();
        System.out.println("*** Searching for clones");
        for (Map.Entry<String, ArrayList<String>> entry : m_data.entrySet()) {
            if (entry.getValue().size() > 1) {
                clones.add(entry.getValue());
            }
        }

        System.out.println("*** Sorting 1");
        for(ArrayList<String> values:clones) {
            Collections.sort(values);
        }
        
        System.out.println("*** Sorting 2");
        Comparator comp = new Comparator<ArrayList<String>>() {

            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return o1.get(0).compareTo(o2.get(0));
            }
        };
        Collections.sort(clones, comp);

        System.out.println("*** Printing info");
        for (ArrayList<String> array : clones) {
            System.out.println();
            for (String fname : array) {
                System.out.println(fname);
            }
        }
        
        ps.close();
        System.setOut(_sys_out_);

    }

    private HashMap<String, ArrayList<String>> m_data = new HashMap<String, ArrayList<String>>();

    private ArrayList<String> _getArray(String hash) {
        ArrayList<String> array = m_data.get(hash);
        if (array == null) {
            array = new ArrayList<String>();
            m_data.put(hash, array);
        }
        return array;
    }

    private void _readFileData(File f) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(f));
        while (br.ready()) {
            String line = br.readLine();

            int p1 = line.lastIndexOf(',');
            String fname = line.substring(0, p1);
            String hash = line.substring(p1 + 2);

            _getArray(hash).add(fname);
        }
        br.close();
    }
}
