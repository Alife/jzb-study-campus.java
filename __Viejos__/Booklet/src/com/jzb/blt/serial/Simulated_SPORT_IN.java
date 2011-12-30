package com.jzb.blt.serial;

import java.io.IOException;
import java.io.InputStream;

public class Simulated_SPORT_IN extends InputStream {

    private InputStream m_fis;

    public Simulated_SPORT_IN() {
        m_fis = getClass().getResourceAsStream("/data/bin.data");
    }

    public int available() throws IOException {
        return m_fis.available();
    }

    public void close() throws IOException {
        m_fis.close();
    }

    public boolean equals(Object obj) {
        return m_fis.equals(obj);
    }

    public int hashCode() {
        return m_fis.hashCode();
    }

    public void mark(int readlimit) {
        m_fis.mark(readlimit);
    }

    public boolean markSupported() {
        return m_fis.markSupported();
    }

    public int read() throws IOException {
        return m_fis.read();
    }

    public int read(byte[] b) throws IOException {
        return m_fis.read(b);
    }

    public int read(byte[] arg0, int arg1, int arg2) throws IOException {
        return m_fis.read(arg0, arg1, arg2);
    }

    public void reset() throws IOException {
        m_fis.reset();
    }

    public long skip(long n) throws IOException {
        return m_fis.skip(n);
    }

    public String toString() {
        return m_fis.toString();
    }

}
