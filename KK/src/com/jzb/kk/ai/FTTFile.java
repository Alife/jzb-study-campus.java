/**
 * 
 */
package com.jzb.kk.ai;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

/**
 * @author n63636
 * 
 */
public class FTTFile {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            FTTFile me = new FTTFile();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        String folders[] = { "C:\\Users\\n63636\\Adobe Flash Builder 4.5\\TTSync\\src-views\\icons\\poi\\gm" };

        for (String folder : folders) {
            iterFolder(new File(folder));
        }
    }

    private void iterFolder(File folder) throws Exception {

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                iterFolder(f);
            } else {
                processFile(f);
            }
        }
    }


    private void processFile(File file) throws Exception {
        
        File fout = new File("C:\\Users\\n63636\\Adobe Flash Builder 4.5\\TTSync\\src-views\\icons\\poi\\bmp\\"+file.getName());

        BufferedImage image = ImageIO.read(file);
        
        BufferedImage resizedImage = new BufferedImage(32, 32, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = resizedImage.createGraphics();
        
        //g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setBackground(Color.WHITE);
        g.fillRect(0,0,32,32);
        g.drawImage(image, 0, 0, 32, 32, null);
        g.dispose();
        
        if(!ImageIO.write(resizedImage, "bmp", fout)) {
            System.out.println("Error: "+file);
        }
    }


}
