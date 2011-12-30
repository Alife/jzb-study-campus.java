/**
 * 
 */
package com.jzb.nsf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author n000013
 * 
 */
public class WindowsUtils {

    public static Set<String> listRunningProcesses() throws Exception {
        Set<String> processes = new HashSet<String>();

        String line;
        Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = input.readLine()) != null) {
            if (!line.trim().equals("")) {
                // keep only the process name
                line = line.substring(1);
                processes.add(line.substring(0, line.indexOf("\"")));
            }

        }
        input.close();
        
        return processes;
    }

}
