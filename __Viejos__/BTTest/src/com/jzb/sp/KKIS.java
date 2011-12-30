package com.jzb.sp;

import java.io.IOException;
import java.io.InputStream;

public class KKIS extends InputStream {

    private String m_text = "Hola Caracola";
    private int    m_pos  = -1;

    public int read() throws IOException {
        if (m_pos == -1) {
            m_pos = 0;
            return m_text.length();
        } else {
            if (m_pos >= m_text.length())
                return -1;
            else
                return (int) m_text.charAt(m_pos++);
        }
    }

}
