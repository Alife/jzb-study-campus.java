package com.jzb.blt.serial;

import java.io.IOException;
import java.io.InputStream;

public class Simulated_SPORT_IN2 extends InputStream {

    public Simulated_SPORT_IN2() {
    }

    public int available() throws IOException {
        return 1;
    }

    public void close() throws IOException {
    }


    public void mark(int readlimit) {
    }

    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        return 'X';
    }

    public int read(byte[] b) throws IOException {
        b[0]='X';
        return 1;
    }

    public int read(byte[] b, int arg1, int arg2) throws IOException {
        b[arg1]='X';
        return 1;
    }

    public void reset() throws IOException {
    }

    public long skip(long n) throws IOException {
        return 0;
    }


}
