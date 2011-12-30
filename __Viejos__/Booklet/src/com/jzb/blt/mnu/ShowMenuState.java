/**
 * 
 */
package com.jzb.blt.mnu;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.jzb.blt.Constants;
import com.jzb.flow.CanvasState;
import com.jzb.flow.EnumCommand;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.GlobalContext;

/**
 * @author PS00A501
 * 
 */
public class ShowMenuState extends CanvasState {

    public static final String EV_SHOW_TEXT      = "showText";
    public static final String EV_SND_UPDATE     = "sendUpdate";
    public static final String EV_RCV_UPDATE     = "receiveUpdate";
    public static final String EV_CLEAR_STORAGE  = "clearStorage";
    public static final String EV_EDIT           = "edit";
    public static final String EV_CREATE_NEW     = "createNew";
    public static final String EV_DELETE         = "delete";

    private static final int   CMD_EXIT          = 0x0010;
    private static final int   CMD_SND_UPDATE    = 0x0020;
    private static final int   CMD_RCV_UPDATE    = 0x0030;
    private static final int   CMD_CLEAR_STORAGE = 0x0040;
    private static final int   CMD_EDIT          = 0x0050;
    private static final int   CMD_CREATE_NEW    = 0x0060;
    private static final int   CMD_DELETE        = 0x0070;

    private Image              m_imgDwnArrow;
    private Image              m_imgFile;
    private Image              m_imgFolder;

    private Image              m_imgUpArrow;

    private Font               m_font;
    private Font               m_boldFont;
    private int                m_charHeight;
    private int                m_numLines;
    private int                m_yOffsetIMG;
    private MenuItem           m_currentMenu;

    /**
     * 
     */
    public ShowMenuState() throws Exception {
        this(null, null);
    }

    public ShowMenuState(Flow owner, String name) throws Exception {
        super(owner, name);

        try {
            m_imgFolder = Image.createImage("/img/dir.png");
            m_imgFile = Image.createImage("/img/file.png");
            m_imgUpArrow = Image.createImage("/img/upArrow.png");
            m_imgDwnArrow = Image.createImage("/img/dwnArrow.png");
        } catch (Exception ex) {
            throw new Exception("Error loading menu images");
        }

        addCommand(new EnumCommand(CMD_EXIT, Event.EV_EXIT, "Exit", Command.EXIT, 1));
        addCommand(new EnumCommand(CMD_EDIT, EV_EDIT, "Edit", Command.SCREEN, 1));
        addCommand(new EnumCommand(CMD_CREATE_NEW, EV_CREATE_NEW, "Create Menu/Text", Command.SCREEN, 1));
        addCommand(new EnumCommand(CMD_DELETE, EV_DELETE, "Delete", Command.SCREEN, 1));
        addCommand(new EnumCommand(CMD_SND_UPDATE, EV_SND_UPDATE, "Send Update", Command.SCREEN, 1));
        addCommand(new EnumCommand(CMD_RCV_UPDATE, EV_RCV_UPDATE, "Receive Update", Command.SCREEN, 1));
        addCommand(new EnumCommand(CMD_CLEAR_STORAGE, EV_CLEAR_STORAGE, "Clear Storage", Command.SCREEN, 1));

        setCurrentFont(Font.SIZE_MEDIUM);
    }

    /**
     * @see com.jzb.flow.CanvasState#deactivate()
     */
    public void deactivate() {
        super.deactivate();
        GlobalContext.setData(Constants.GC_MENU_DATA, m_currentMenu);
    }

    public void resetMenu() {
        m_currentMenu = null;
    }

    /**
     * @see com.jzb.flow.CanvasState#getCmdEventData(com.jzb.flow.EnumCommand)
     */
    protected Object getCmdEventData(EnumCommand ec) {
        switch (ec.getCode()) {
            case CMD_DELETE:
            case CMD_EDIT:
                if (m_currentMenu.getSelectedChild() == m_currentMenu.getParent())
                    return null;
                else
                    return m_currentMenu.getSelectedChild();

            case CMD_CREATE_NEW:
                return m_currentMenu;

            default:
                return null;
        }
    }

    /**
     * @see com.jzb.flow.CanvasState#innerActivate(com.jzb.flow.Event)
     */
    protected void innerActivate(Event ev) {
        m_currentMenu = (MenuItem) GlobalContext.getData(Constants.GC_MENU_DATA);
    }

    protected void keyPressed(int keyCode) {
        processKey(keyCode, false);
    }

    protected void keyRepeated(int keyCode) {
        processKey(keyCode, true);
    }

    /**
     * @see com.jzb.flow.CanvasState#paint(javax.microedition.lcdui.Graphics)
     */
    protected void paint(Graphics gr) {

        // Erase the screen
        gr.setColor(0xFFFFFF);
        gr.fillRect(0, 0, getWidth(), getHeight());

        // Paint caption
        paintCaption(gr);

        // Draw Menu Items
        int selectedItem = m_currentMenu.getSelected();
        int topItem = m_currentMenu.getTop();

        for (int n = topItem; (n < m_currentMenu.getNumItems() && (n - topItem) < m_numLines); n++) {

            MenuItem mi = m_currentMenu.getChild(n);
            boolean selected = (n == selectedItem);
            int yIndex = m_charHeight + (n - topItem) * m_charHeight;

            if (selected) {
                gr.setColor(0x7A93E7);
                gr.fillRect(20, yIndex, getWidth() - 20, m_charHeight);
                gr.setFont(m_boldFont);
                gr.setColor(0xFFFFFF);
            } else {
                gr.setFont(m_font);
                gr.setColor(0x000000);
            }

            if (mi.isFile())
                gr.drawImage(m_imgFile, 2, yIndex + m_yOffsetIMG, Graphics.TOP | Graphics.LEFT);
            else
                gr.drawImage(m_imgFolder, 2, yIndex + m_yOffsetIMG, Graphics.TOP | Graphics.LEFT);

            if (n == 0 && m_currentMenu.isSubMenu())
                gr.drawString(MenuItem.PARENT_NAME, 20, yIndex, Graphics.TOP | Graphics.LEFT);
            else
                gr.drawString(mi.getAlias(), 20, yIndex, Graphics.TOP | Graphics.LEFT);

            paintRuler(gr);
        }

    }

    private void moveBack() {
        if (m_currentMenu.isSubMenu()) {
            m_currentMenu = m_currentMenu.getChild(0);
            repaint();
        }
    }

    private void moveDown() {

        int selectedItem = m_currentMenu.getSelected();
        int topItem = m_currentMenu.getTop();

        if (selectedItem < m_currentMenu.getNumItems() - 1) {
            selectedItem++;
            if (selectedItem - topItem >= m_numLines) {
                topItem++;
            }

            m_currentMenu.setSelected(selectedItem);
            m_currentMenu.setTop(topItem);

            repaint();
        }
    }

    private void moveUp() {
        int selectedItem = m_currentMenu.getSelected();
        int topItem = m_currentMenu.getTop();

        if (selectedItem > 0) {
            selectedItem--;
            if (selectedItem - topItem < 0) {
                topItem--;
            }

            m_currentMenu.setSelected(selectedItem);
            m_currentMenu.setTop(topItem);

            repaint();
        }
    }

    private void paintCaption(Graphics gr) {

        char alias[] = new char[m_currentMenu.getAlias().length()];
        m_currentMenu.getAlias().getChars(0, alias.length, alias, 0);

        gr.setColor(0x183185);
        gr.fillRect(0, 0, getWidth(), m_charHeight);
        gr.setColor(0xFFFFFF);
        gr.setFont(m_boldFont);
        int x = (getWidth() - m_boldFont.charsWidth(alias, 0, alias.length)) / 2;
        gr.drawChars(alias, 0, alias.length, x, 0, Graphics.TOP | Graphics.LEFT);
        gr.drawImage(m_imgFolder, x - 20, m_yOffsetIMG, Graphics.TOP | Graphics.LEFT);
    }

    private void paintRuler(Graphics gr) {
        if (m_currentMenu.getTop() > 0) {
            gr.drawImage(m_imgUpArrow, getWidth() - 14, m_charHeight + m_yOffsetIMG, Graphics.TOP | Graphics.LEFT);
        }

        if (m_currentMenu.getTop() + m_numLines < m_currentMenu.getNumItems()) {
            gr.drawImage(m_imgDwnArrow, getWidth() - 14, m_charHeight * (m_numLines) + m_yOffsetIMG, Graphics.TOP | Graphics.LEFT);
        }
    }

    private void processKey(int keyCode, boolean repeated) {
        int ga = getGameAction(keyCode);
        switch (ga) {
            case Canvas.UP:
                moveUp();
                break;

            case Canvas.DOWN:
                moveDown();
                break;

            case Canvas.LEFT:
                if (!repeated) {
                    moveBack();
                }
                break;

            case Canvas.RIGHT:
            case Canvas.FIRE:
                if (!repeated)
                    selectItem();
                break;
        }
    }

    private void selectItem() {
        MenuItem selItem = m_currentMenu.getSelectedChild();
        if (selItem.isFile()) {
            _fireEvent(EV_SHOW_TEXT, selItem);
        } else {
            m_currentMenu = selItem;
            repaint();
        }
    }

    private void setCurrentFont(int size) {

        m_font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, size);
        m_boldFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN | Font.STYLE_BOLD, size);

        m_charHeight = m_font.getHeight();
        m_numLines = (getHeight() / m_charHeight) - 1;
        m_yOffsetIMG = (m_charHeight - 16) / 2;
    }

}
