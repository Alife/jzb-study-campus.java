package com.jzb.sp;

import javax.microedition.lcdui.Command;

import com.jzb.flow.AlertState;
import com.jzb.flow.Event;
import com.jzb.flow.EventCommand;

public class ConnectCableState extends AlertState {

    public ConnectCableState(String name) {
        super(name, "Instructions");
        setString("Connect the cable in the next screen.\n\nIf it's already connected, disconect it and wait next screen.");
        addCommand(new EventCommand(Event.EV_ID_CANCEL, "Cancel",Command.CANCEL,1));
    }

}
