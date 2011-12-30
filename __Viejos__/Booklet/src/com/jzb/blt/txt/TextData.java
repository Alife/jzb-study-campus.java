package com.jzb.blt.txt;

import java.util.Vector;

import javax.microedition.lcdui.Font;

public class TextData {

    // -----------------------------------------------------------------
    public class LinePointer {

        private int m_offset;
        private int m_size;
        public boolean m_isTitle;

        public LinePointer(int lOffset, int lSize, boolean isTitle) {
            m_offset = lOffset;
            m_size = lSize;
            m_isTitle=isTitle;
        }

        public String toString() {
            return "Offset = " + m_offset + " / size = " + m_size;
        }

        public int getOffset() {
            return m_offset;
        }

        public int getSize() {
            return m_size;
        }

        public char[] getBuffer() {
            return m_data;
        }
    }

    // -----------------------------------------------------------------

    private String m_alias;
    private char   m_data[];
    private Vector m_linePointers = new Vector();
    private int    m_size         = 0;

    public TextData(String alias, char data[], int size) {
        m_alias = alias;
        m_size = size;
        m_data = data;
    }

    public String getAlias() {
        return m_alias;
    }

    public LinePointer getLineText(int index) {
        LinePointer lp = (LinePointer) m_linePointers.elementAt(index);
        return lp;
        // String str = new String(m_data, lp.offset, lp.size);
        // return str;
    }

    public int getNumLines() {
        return m_linePointers.size();
    }

    public String getStrData() {
        return new String(m_data, 0, m_size);
    }

    public void printLines() {
        for (int n = 0; n < m_linePointers.size(); n++) {
            LinePointer lp = (LinePointer) m_linePointers.elementAt(n);
            for (int i = 0; i < lp.m_size; i++) {
                System.out.print(m_data[lp.m_offset + i]);
            }
            System.out.println();
        }
    }

    public void repaginate(Font font, int lineWidth, int charWidths[]) {

        m_linePointers.removeAllElements();

        boolean isTitle = false;
        
        int offset = 0;
        char cc = 0;
        int cWidth = 0;
        int spaceWidth = 0;
        int spaceOffset = 0;
        int lastLineOffset = 0;

        while (offset < m_size) {
            cc = m_data[offset];
            offset++;
            if(cc=='¬') {
                // special character for Titles
                isTitle = true;
                lastLineOffset++;
                cWidth=0;
            }
            if (cc < 256) {
                cWidth += charWidths[cc];
            } else {
                cWidth += font.charWidth(cc);
            }

            // check if it's a new line char
            if (cc == '\n') {
                m_linePointers.addElement(new LinePointer(lastLineOffset, offset - lastLineOffset - 1, isTitle));
                lastLineOffset = offset;
                cWidth = 0;
                spaceWidth = 0;
                isTitle = false;
                continue;
            }

            // Take note about spaces
            if (cc == ' ') {
                spaceWidth = cWidth;
                spaceOffset = offset;
            }

            // If line width has been reached or there is no more data, new line is created
            if (cWidth >= lineWidth) {

                // just if width is larger than maximum, remove last character
                // added
                if (cWidth > lineWidth) {
                    offset--;
                    if (cc < 256)
                        cWidth -= charWidths[cc];
                    else
                        cWidth -= font.charWidth(cc);
                }

                // If the new line cut a word and is small enough, offset is
                // moved back
                char nc = m_data[offset];
                if ((cWidth - spaceWidth <= (lineWidth / 2)) && (cc != ' ' && cc != '\n') && (nc != ' ' && nc != '\n')) {
                    offset = spaceOffset;
                }
                m_linePointers.addElement(new LinePointer(lastLineOffset, offset - lastLineOffset, isTitle));
                lastLineOffset = offset;
                cWidth = 0;
                spaceWidth = 0;
            }

        }

        if((m_size-lastLineOffset)>0) {
            m_linePointers.addElement(new LinePointer(lastLineOffset, m_size-lastLineOffset, isTitle));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(m_data, 0, m_size);
        return sb.toString();
    }

}