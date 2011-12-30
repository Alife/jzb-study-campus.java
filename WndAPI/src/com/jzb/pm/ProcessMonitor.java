/**
 * 
 */
package com.jzb.pm;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author n000013
 * 
 */
public class ProcessMonitor {

    private ProcessBuilder m_pb;
    private Process        m_proc = null;

    public ProcessMonitor(String... command) {
        this(null, null, command);
    }

    public ProcessMonitor(String workingDir, Map<String, String> env, String... command) {

        m_pb = new ProcessBuilder(command);

        if (workingDir != null) {
            m_pb.directory(new File(workingDir));
        }

        if (env != null) {
            m_pb.environment().putAll(env);
        }
    }

    public void start() throws IOException {
        if (m_proc == null) {
            m_proc = m_pb.start();
        }
    }

    public void stop() {
        if (m_proc != null) {
            m_proc.destroy();
            m_proc = null;
        }
    }

    public int exitValue() throws IllegalThreadStateException {
        if (m_proc != null)
            return m_proc.exitValue();
        else
            throw new IllegalThreadStateException("Process was not created");
    }

    public boolean isRunning() {
        if (m_proc != null) {
            try {
                m_proc.exitValue();
                return false;
            } catch (IllegalThreadStateException ex) {
                return true;
            }
        } else {
            return false;
        }
    }

    private void pev() {
        try {
            Thread.currentThread().sleep(100);
            System.out.println("--------------------------------------------");
            System.out.println(m_proc.exitValue());
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
