/**
 * 
 */
package com.jzb.blt.txt;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.jzb.blt.Constants;
import com.jzb.flow.CanvasState;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.GlobalContext;

/**
 * @author PS00A501
 * 
 */
public class ShowTextState extends CanvasState implements CommandListener {

    private static final int     FONT_SIZES[]   = { Font.SIZE_SMALL, Font.SIZE_MEDIUM, Font.SIZE_LARGE };

    private static final Command s_cmdExit      = new Command("Exit", Command.EXIT, 1);
    private static final Command s_cmdBack      = new Command("Back", Command.BACK, 1);

    private Font                 m_titleFont;
    private Font                 m_boldFont;
    private Font                 m_font;
    
    private int                  m_fontSize;

    private int                  m_charHeight;
    private int                  m_charWidths[] = new int[256];

    private int                  m_topItem      = 0;

    private int                  m_numLines;
    private TextData             m_currentText;

    private Image                m_imgFile;

    /**
     * 
     */
    public ShowTextState() throws Exception {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public ShowTextState(Flow owner, String name) throws Exception {
        super(owner, name);
        setCurrentFontSize(1);
        addCommand(s_cmdExit);
        addCommand(s_cmdBack);
        setCommandListener(this);

        try {
            m_imgFile = Image.createImage("/img/file.png");
        } catch (Exception ex) {
            throw new Exception("Error loading text images");
        }
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (s_cmdBack.equals(cmd)) {
            _fireEvent(Event.EV_BACK);
        } else {
            _fireEvent(Event.EV_EXIT);
        }
    }

    /**
     * @see com.jzb.flow.CanvasState#deactivate()
     */
    public void deactivate() {
        super.deactivate();
        GlobalContext.setData(Constants.GC_TEXT_DATA, null);
    }

    /**
     * @see com.jzb.flow.CanvasState#innerActivate(com.jzb.flow.Event)
     */
    protected void innerActivate(Event ev) {

        m_topItem = 0;
        m_currentText = (TextData) GlobalContext.getData(Constants.GC_TEXT_DATA);

        if (m_currentText != null) {
            m_currentText.repaginate(m_font, getWidth() - 4, m_charWidths);
        }
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

        // Draw Text Lines
        for (int n = m_topItem; (n < m_currentText.getNumLines() && (n - m_topItem) < m_numLines); n++) {

            int yIndex = m_charHeight + (n - m_topItem) * m_charHeight;
            
            TextData.LinePointer lp = m_currentText.getLineText(n);
            
            if (lp.m_isTitle) {
                gr.setColor(0x3333CC);
                gr.setFont(m_titleFont);
            }
            else {
                gr.setColor(0x000000);
                gr.setFont(m_font);
            }
            
            gr.drawChars(lp.getBuffer(), lp.getOffset(), lp.getSize(), 0, yIndex, Graphics.TOP | Graphics.LEFT);
            
        }

        paintRuler(gr);

    }

    private void moveDown(int lines) {
        if (m_topItem < m_currentText.getNumLines() - m_numLines) {
            m_topItem += lines;
            if (m_topItem > m_currentText.getNumLines() - m_numLines)
                m_topItem = m_currentText.getNumLines() - m_numLines;
            repaint();
        }
    }

    private void moveUp(int lines) {
        if (m_topItem > 0) {
            m_topItem -= lines;
            if (m_topItem < 0)
                m_topItem = 0;
            repaint();
        }
    }

    private void paintCaption(Graphics gr) {

        char alias[] = new char[m_currentText.getAlias().length()];
        m_currentText.getAlias().getChars(0, alias.length, alias, 0);

        gr.setColor(0x183185);
        gr.fillRect(0, 0, getWidth(), m_charHeight);
        gr.setColor(0xFFFFFF);
        gr.setFont(m_boldFont);
        int x = (getWidth() - m_boldFont.charsWidth(alias, 0, alias.length)) / 2;
        gr.drawChars(alias, 0, alias.length, x, 0, Graphics.TOP | Graphics.LEFT);
        gr.drawImage(m_imgFile, x - 20, (m_charHeight - 16) / 2, Graphics.TOP | Graphics.LEFT);
    }

    private void paintRuler(Graphics gr) {

        gr.setStrokeStyle(Graphics.SOLID);
        gr.setColor(0x999999);
        gr.fillRect(getWidth() - 3, 0, 3, getHeight());

        int p;
        if (m_topItem == 0) {
            p = 0;
        } else if (m_topItem == m_currentText.getNumLines() - m_numLines) {
            p = getHeight() - 3;
        } else {
            p = (getHeight() * m_topItem) / (m_currentText.getNumLines() - m_numLines);
        }

        gr.setColor(0xFFFF00);
        gr.fillRect(getWidth() - 3, p, 3, 3);
    }

    private void processKey(int keyCode, boolean repeated) {
        int ga = getGameAction(keyCode);
        switch (ga) {
            case Canvas.UP:
                moveUp(1);
                break;

            case Canvas.DOWN:
                moveDown(1);
                break;

            case Canvas.LEFT:
                moveUp(m_numLines);
                break;

            case Canvas.RIGHT:
                moveDown(m_numLines);
                break;

            case 0:
                switch (keyCode) {
                    case Canvas.KEY_STAR:
                        if (m_fontSize > 0) {
                            m_fontSize--;
                            setCurrentFontSize(m_fontSize);
                            m_currentText.repaginate(m_font, getWidth() - 3, m_charWidths);
                            m_topItem = 0;
                            repaint();
                        }
                        break;
                    case Canvas.KEY_POUND:
                        if (m_fontSize < 2) {
                            m_fontSize++;
                            setCurrentFontSize(m_fontSize);
                            m_currentText.repaginate(m_font, getWidth() - 3, m_charWidths);
                            m_topItem = 0;
                            repaint();
                        }
                        break;
                }
        }
    }

    private void setCurrentFontSize(int size) {

        m_fontSize = size;
        m_font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, FONT_SIZES[size]);
        m_boldFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN | Font.STYLE_BOLD, FONT_SIZES[size]);
        m_titleFont= Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN | Font.STYLE_BOLD | Font.STYLE_UNDERLINED, FONT_SIZES[size]);
        
        for (int n = 0; n < 256; n++) {
            m_charWidths[n] = m_font.charWidth((char) n);
        }

        m_charHeight = m_font.getHeight();

        m_numLines = (getHeight() / m_charHeight) - 1;
    }
}
