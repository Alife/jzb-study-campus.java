/**
 * 
 */
package com.jzb.sw.maps;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jzb.tpoi.srvc.GMapService;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Des3Encrypter;

/**
 * @author n63636
 * 
 */
public class MapsAppWnd {

    private JFrame frame;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    MapsAppWnd window = new MapsAppWnd();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application
     */
    public MapsAppWnd() throws Exception {
        createContents();
        DefaultHttpProxy.setDefaultProxy();

        ModelService.inst._setBaseFolder("C:\\Users\\n63636\\Desktop\\xIPAs\\gmaps");
        GMapService.inst.login(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

    }

    /**
     * Initialize the contents of the frame
     */
    private void createContents() {
        frame = new JFrame();
        frame.setBounds(100, 100, 500, 375);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel panel = new MapElementsPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }

}
