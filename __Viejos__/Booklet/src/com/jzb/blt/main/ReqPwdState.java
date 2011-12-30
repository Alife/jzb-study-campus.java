/**
 * 
 */
package com.jzb.blt.main;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.jzb.blt.StoreManager;
import com.jzb.flow.CanvasState;
import com.jzb.flow.EnumCommand;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.Utils;
import com.jzb.md5.MD5Digest;

/**
 * @author PS00A501
 * 
 */
public class ReqPwdState extends CanvasState {

    private static final int MAX_PWD_LENGTH = 6;

    private Image            m_imgSignOn;
    private Image            m_imgSignOnKey;

    private String           m_strPWD;

    private int              m_times;

    private int              m_centerPos;
    private int              m_imgYPos;
    private int              m_txtYPos;
    private int              m_keyWidth;
    private int              m_keyHeight;

    private byte             m_pwdMD5[];

    /**
     * 
     */
    public ReqPwdState() throws Exception {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public ReqPwdState(Flow owner, String name) throws Exception {
        super(owner, name);

        m_imgSignOn = Image.createImage("/img/signOn.png");
        m_imgSignOnKey = Image.createImage("/img/signOnKey.png");

        addCommand(EnumCommand.ECMD_OK);
        addCommand(EnumCommand.ECMD_CANCEL);

        m_centerPos = getWidth() / 2;
        m_imgYPos = 20;
        m_txtYPos = 20 + m_imgYPos + m_imgSignOn.getHeight();
        m_keyWidth = m_imgSignOnKey.getWidth() + 2;
        m_keyHeight = m_imgSignOnKey.getHeight();

    }

    /**
     * @see com.jzb.flow.CanvasState#innerActivate(com.jzb.flow.Event)
     */
    protected void innerActivate(Event ev) {
        m_times = 0;
        m_strPWD = "";
        readPrevPwd();
        if (m_pwdMD5 == null)
            setTitle("Enter NEW password:");
        else
            setTitle("Enter password:");
    }

    /**
     * @see com.jzb.flow.CanvasState#paint(javax.microedition.lcdui.Graphics)
     */
    protected void paint(Graphics gr) {
        gr.setColor(0xFFFFFF);
        gr.fillRect(0, 0, getWidth(), getHeight());
        gr.setColor(0x000000);
        gr.drawImage(m_imgSignOn, m_centerPos, m_imgYPos, Graphics.TOP | Graphics.HCENTER);
        gr.drawRect(5, m_txtYPos - 5, getWidth() - 10, m_keyHeight+10);
        gr.setColor(0xFF0000);
        int xPos = (getWidth() - (m_strPWD.length() * m_keyWidth)) / 2;
        for (int n = 0; n < m_strPWD.length(); n++) {
            gr.drawImage(m_imgSignOnKey, xPos, m_txtYPos, Graphics.TOP | Graphics.LEFT);
            xPos += m_keyWidth;
        }
    }

    /**
     * @see javax.microedition.lcdui.Canvas#keyPressed(int)
     */
    protected void keyPressed(int keyCode) {
        if (keyCode >= Canvas.KEY_POUND && keyCode <= Canvas.KEY_NUM9 && m_strPWD.length() < MAX_PWD_LENGTH) {
            m_strPWD += (char) keyCode;
            repaint();
        }
    }

    /**
     * @see com.jzb.flow.CanvasState#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (EnumCommand.ECMD_OK.equals(cmd)) {
            if (m_pwdMD5 == null)
                savePWD();
            else
                checkPWD();
        } else if (EnumCommand.ECMD_CANCEL.equals(cmd)) {
            _fireEvent(Event.EV_CANCEL);
        }
    }

    private void checkPWD() {
        try {
            byte bufferMD5[] = getMD5(m_strPWD.getBytes());

            if (areEquals(m_pwdMD5, bufferMD5)) {
                StoreManager.setPWDMD5(reverse(bufferMD5));
                _fireEvent(Event.EV_OK);
            } else {
                Utils.showMessage("Error", "Password is not correct");
                m_strPWD = "";
                m_times++;
                if (m_times >= 3) {
                    _fireEvent(Event.EV_CANCEL);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error checking password: " + Utils.getExceptionMsg(ex));
        }
    }

    private void savePWD() {
        try {
            byte md5Pwd[] = getMD5(m_strPWD.getBytes());
            StoreManager.savePWD(md5Pwd);
            StoreManager.setPWDMD5(reverse(md5Pwd));
            _fireEvent(Event.EV_OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error saving password: " + Utils.getExceptionMsg(ex));
        }
    }

    private byte[] reverse(byte buffer[]) {
        byte b[] = new byte[buffer.length];
        int p = b.length - 1;
        for (int n = 0; n < b.length; n++) {
            b[n] = buffer[p];
            p--;
        }
        return b;

    }

    private byte[] getMD5(byte text[]) {
        MD5Digest md5 = new MD5Digest();
        md5.update(text, 0, text.length);
        byte md5Pwd[] = new byte[md5.getDigestSize()];
        md5.doFinal(md5Pwd, 0);
        return md5Pwd;
    }

    private void readPrevPwd() {
        try {
            m_pwdMD5 = StoreManager.loadPWD();
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error reading previous password: " + Utils.getExceptionMsg(ex));
        }
    }

    private boolean areEquals(byte b1[], byte b2[]) {
        if (b1.length != b2.length)
            return false;
        for (int n = 0; n < b1.length; n++) {
            if (b1[n] != b2[n])
                return false;
        }
        return true;
    }

}
