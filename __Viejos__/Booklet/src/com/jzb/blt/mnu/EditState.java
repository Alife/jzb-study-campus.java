/**
 * 
 */
package com.jzb.blt.mnu;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

import com.jzb.blt.StoreManager;
import com.jzb.blt.txt.TextData;
import com.jzb.blt.txt.TextManager;
import com.jzb.flow.EnumCommand;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.flow.FormState;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class EditState extends FormState {

    private TextField m_tfName;
    private TextField m_tfText;

    private MenuItem  m_menuItem;

    /**
     * 
     */
    public EditState() {
        this(null, null, null);
    }

    /**
     * @param owner
     * @param name
     * @param title
     */
    public EditState(Flow owner, String name, String title) {
        super(owner, name, title);
    }

    public void commandAction(Command cmd, Displayable disp) {

        if (cmd instanceof EnumCommand) {
            EnumCommand ec = (EnumCommand) cmd;
            if (ec.getCode() == Command.OK) {
                saveInfo();
            } else {
                super.commandAction(cmd, disp);
            }
        }
    }

    /**
     * @see com.jzb.flow.FormState#createItems()
     */
    protected void createItems() {

        m_tfName = new TextField("Name:", "", 80, TextField.ANY | TextField.SENSITIVE);
        append(m_tfName);

        m_tfText = new TextField("Text:", "", 65536, TextField.ANY | TextField.SENSITIVE);
        append(m_tfText);

        addCommand(EnumCommand.ECMD_OK);
        addCommand(EnumCommand.ECMD_CANCEL);
    }

    /**
     * @see com.jzb.flow.FormState#innerActivate(com.jzb.flow.Event)
     */
    protected void innerActivate(Event ev) {

        m_menuItem = (MenuItem) ev.getData();
        if (m_menuItem == null) {
            _fireEvent(Event.EV_CANCEL);
        } else {
            m_tfName.setString(m_menuItem.getAlias());
            if (m_menuItem.isFile()) {
                m_tfText.setConstraints(TextField.ANY | TextField.SENSITIVE);
                m_tfText.setString(getText(m_menuItem));
            } else {
                m_tfText.setConstraints(TextField.UNEDITABLE);
                m_tfText.setString("");
            }
        }
    }

    private String getText(MenuItem mi) {
        try {
            TextData td = TextManager.loadTextData(mi.getAlias(), mi.getFileRecordIndex());
            return td.getStrData();
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error reading text to be edited: " + Utils.getExceptionMsg(ex));
            return "";
        }
    }

    private boolean saveFileInfo() {
        try {
            if (!m_menuItem.isFile())
                return false;

            String txt = m_tfText.getString();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.write(txt.getBytes());
            dos.close();
            byte buffer[] = baos.toByteArray();

            StoreManager.updateFile(m_menuItem.getFileRecordIndex(), buffer);

            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error saving file info: " + Utils.getExceptionMsg(ex));
            return true;
        }
    }

    private void saveInfo() {
        boolean eventAlreadySent;

        eventAlreadySent = saveMenuInfo();
        eventAlreadySent = saveFileInfo();

        if (!eventAlreadySent)
            _fireEvent(Event.EV_OK);
    }

    private boolean saveMenuInfo() {
        try {
            m_menuItem.setAlias(m_tfName.getString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            m_menuItem.getRootParent().writeExternal(dos);
            dos.close();
            byte buffer[] = baos.toByteArray();

            StoreManager.saveMenu(buffer);

            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error saving menu info: " + Utils.getExceptionMsg(ex));
            return true;
        }
    }

}
