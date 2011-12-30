/**
 * 
 */
package org.xmlpull.v1;

public class XmlPullParserException extends Exception {

    protected Throwable detail;

    protected int       row;

    protected int       column;

    public XmlPullParserException(String s) {
        super(s);
        row = -1;
        column = -1;
    }

    public XmlPullParserException(String msg, XmlPullParser parser, Throwable chain) {
        super((msg != null ? msg + " " : "") + (parser != null ? "(position:" + parser.getPositionDescription() + ") " : "") + (chain != null ? "caused by: " + chain : ""));
        row = -1;
        column = -1;
        if (parser != null) {
            row = parser.getLineNumber();
            column = parser.getColumnNumber();
        }
        detail = chain;
    }

    public int getColumnNumber() {
        return column;
    }

    public Throwable getDetail() {
        return detail;
    }
    public int getLineNumber() {
        return row;
    }
    public void printStackTrace() {
        if (detail == null)
            super.printStackTrace();
        else
            synchronized (System.err) {
                System.err.println(super.getMessage() + "; nested exception is:");
                detail.printStackTrace();
            }
    }
}