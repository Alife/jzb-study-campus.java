package com.JWinAPI;


import java.awt.*;
import javax.swing.JFrame;
/**
 * General purpose application window
 */

public class AppWindow extends JFrame {
    public AppWindow(String title) {
        this(title, null); // Pass null dimension to other constructor
    }

    /**
     * Create a window -- JFrame.  The window will be centered.
     * @param title
     * @param size
     */
    public AppWindow(String title, Dimension size) {
        super(title); // Call the base constructor

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // If window size is null, set to 2/3 screen size
        if (size == null)
            size = new Dimension(2 * screenSize.width / 3,
                    2 * screenSize.height / 3);

        // Set window size and centered on screen
        if (screenSize.width < size.width || screenSize.height < size.height) { // Requested
                                                                                // size
                                                                                // exceeds
                                                                                // screen
            setSize(screenSize); // so set to screen size
            setLocation(0, 0); // and positioned top left
        } else {
            setSize(size); // Set to given size
            setLocation((screenSize.width - size.width) / 2, // and centered
                    (screenSize.height - size.height) / 2);
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
