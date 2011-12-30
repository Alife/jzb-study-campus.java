/**
 * 
 */
package com.jzb.ipa.ssh;

import com.jcraft.jsch.SftpProgressMonitor;

public class MyMonitor2 implements SftpProgressMonitor {

    private long m_cur  = 0;
    private int  m_iter = 0;
    private long m_max;

    public MyMonitor2(long max) {
        m_max = max;
    }

    public boolean count(long count) {
        // System.out.println("-- MT --> Test1.MyMonitor.count(" + count + ")");
        m_cur += count;
        if (m_max > 0) {
            long p = 100L * m_cur / m_max;
            System.out.print(p + "% ");
        } else {
            System.out.print(m_cur + " ");
        }
        m_iter = (m_iter + 1) % 10;
        if (m_iter == 0)
            System.out.println();
        return true;
    }

    public void end() {
        System.out.println("-- MT --> Test1.MyMonitor.end()");
    }

    public void init(int op, String src, String dest, long max) {
        m_cur = 0;
        m_max = max;
        System.out.println("-- MT --> Test1.MyMonitor.init(" + op + ", " + src + ", " + dest + ", " + max + ")");
    }
}