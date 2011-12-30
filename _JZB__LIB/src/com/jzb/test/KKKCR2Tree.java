/**
 * 
 */
package com.jzb.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;

import com.jzb.futil.FileUtils;
import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;

/**
 * @author n63636
 * 
 */
public class KKKCR2Tree {

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
            KKKCR2Tree me = new KKKCR2Tree();
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

    public void doIt4(String[] args) throws Exception {

        File orFile = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Filtradas_NO\\ph\\02-P_Hockey_00055_IMG#CR2_2.jpg");
        File trFile = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Tree\\02-P_Hockey_00000-IMG_0509#CR2.jpg");

        BufferedImage orImage = ImageIO.read(orFile).getSubimage(0, 0, 200, 200);
        orImage = ImageCompare.imageToBufferedImage(GrayFilter.createDisabledImage(orImage));

        BufferedImage trImage = ImageIO.read(trFile).getSubimage(0, 0, 200, 200);
        trImage = ImageCompare.imageToBufferedImage(GrayFilter.createDisabledImage(trImage));
        ImageCompare ic = new ImageCompare(orImage, trImage);
        ic.setParameters(8, 6, 10, 10);
        ic.setDebugMode(2);
        ic.compare();

        System.out.println(ic.match());

    }

    public void doIt(String[] args) throws Exception {

        File origFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Organizadas\\02-P_Hockey\\kk");
        File destFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Organizadas\\02-P_Hockey\\kk2");
        File treeFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Tree");
        doCompare(origFolder, destFolder, treeFolder);
    }

    private HashMap<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();

    public void doCompare(File origFolder, File destFolder, File treeFolder) throws Exception {

        System.out.println("*** Buscando en: "+origFolder);
        for (File orFile : origFolder.listFiles()) {

            if (orFile.isDirectory()) {
                doCompare(orFile, new File(destFolder, orFile.getName()), treeFolder);
                continue;
            }

            System.out.println("Buscando equivalente de: " + orFile.getName());
            BufferedImage orImage = null;
            try {
                orImage = ImageIO.read(orFile).getSubimage(0, 0, 200, 200);
            } catch (Throwable th) {
                System.out.println("Error reading image: " + th.getMessage());
                continue;
            }

            orImage = ImageCompare.imageToBufferedImage(GrayFilter.createDisabledImage(orImage));

            for (File trFile : treeFolder.listFiles()) {

                if (trFile.isDirectory())
                    continue;

                System.out.print("    Comparando con: " + trFile.getName());
                BufferedImage trImage = imageCache.get(trFile.getPath());
                if (trImage == null) {
                    try {
                        trImage = ImageIO.read(trFile).getSubimage(0, 0, 200, 200);
                    } catch (Throwable th) {
                        System.out.println("Error reading image: " + th.getMessage());
                        continue;
                    }
                    trImage = ImageCompare.imageToBufferedImage(GrayFilter.createDisabledImage(trImage));
                    imageCache.put(trFile.getPath(), trImage);
                }

                ImageCompare ic = new ImageCompare(orImage, trImage);
                ic.setParameters(8, 8, 5, 10);
                // ic.setDebugMode(2);
                ic.compare();

                if (ic.match()) {
                    System.out.println("...Foto equivalente encontrada!!");
                } else {
                    System.out.println("...No");
                }

                if (ic.match()) {
                    File newFile = new File(destFolder, trFile.getName());
                    newFile.getParentFile().mkdirs();
                    if (!trFile.renameTo(newFile)) {
                        System.out.println("Error moving '" + trFile + "' to: " + newFile);
                    }
                    // if (!orFile.delete()) {
                    // System.out.println("Error deleting: " + orFile);
                    // }

                    break;
                }

            }
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
    public void doIt2(String[] args) throws Exception {

        final HashMap<String, File> cr2Files = new HashMap<String, File>();

        final File baseFolder1 = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Organizadas");
        final File baseFolder2 = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Filtradas_NO");
        final File treeFolder = new File("C:\\JZarzuela\\_Fotos_\\__SIN COLOCAR__\\2010_12_09-15_Boston\\Tree");

        // ----------------------------------------------------------------------------------------------
        IFileProcessor fprocessor1 = new IFileProcessor() {

            /**
             * @see com.jzb.futil.IFileProcessor#setFolderIterator(com.jzb.futil.FolderIterator)
             */
            @Override
            public void setFolderIterator(FolderIterator fi) {
            }

            /**
             * @see com.jzb.futil.IFileProcessor#processFile(java.io.File, java.io.File)
             */
            @Override
            public void processFile(File f, File baseFolder) throws Exception {
                String fn = f.getName().toLowerCase().substring(f.getName().length() - 16);
                cr2Files.put(fn, f.getParentFile());
            }
        };

        FolderIterator fi = new FolderIterator(fprocessor1, baseFolder1);
        fi.iterate();

        fi = new FolderIterator(fprocessor1, baseFolder2);
        fi.iterate();

        for (File f : treeFolder.listFiles()) {
            String fn = f.getName().toLowerCase().substring(f.getName().length() - 16);
            File parentFolder = cr2Files.get(fn);
            if (parentFolder != null) {
                System.out.println("Moving file: " + f.getName());
                File newFile = new File(parentFolder, "kk\\" + f.getName());
                newFile.getParentFile().mkdirs();
                if (!f.renameTo(newFile)) {
                    System.out.println("ERROR: File (" + f.getName() + ") couldn't be renamed to: " + newFile);
                }
            }
        }
    }
}
