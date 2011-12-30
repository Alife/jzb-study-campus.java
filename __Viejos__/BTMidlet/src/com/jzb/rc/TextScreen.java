/**
 * 
 */
package com.jzb.rc;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

// A closeable screen for displaying text.

class TextScreen extends Form implements CommandListener {

    private final MIDletApplication midlet;
    private final Displayable       next;

    TextScreen(MIDletApplication midlet, Displayable next, String title, String text, String closeLabel) {
        super(title);
        this.midlet = midlet;
        this.next = next;
        append(text);
        addCommand(new Command(closeLabel, Command.BACK, 1));
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        // The application code only adds a 'close' command.
        midlet.textScreenClosed(next);
    }
}