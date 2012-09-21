import java.io.File;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * 
 */

/**
 * @author jzarzuela
 * 
 */
public class KKKK {

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
            KKKK me = new KKKK();
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

        File folder = new File("/Users/jzarzuela/Documents/_TMP_/100CANON-PRAGA/Organizadas copy 2/2-Plzen");
        File folderR = new File("/Users/jzarzuela/Documents/_TMP_/100CANON-PRAGA/Organizadas copy 2/R2-Plzen");

        String name1 = "Praga-#00030-Varios_[IMG_7694].jpg";
        String name2 = "Praga-#00050-Stare Mesto-Plaza de la Republica_[IMG_7581-RETO].jpg";

        /*
         * printOrientation(new File(folder,name1)); printOrientation(new File(folder,name2)); System.exit(1);
         */
        for (File f : folder.listFiles()) {
            if (f.getName().startsWith(".")) {
                continue;
            }
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            ExifIFD0Directory dir = metadata.getDirectory(ExifIFD0Directory.class);
            int orientation = dir.getInt(274);
            System.out.println(orientation);

            if (orientation != 1) {
                File newFile = new File(folderR, f.getName());
                if (!f.renameTo(newFile)) {
                    System.out.println("Error moving: " + newFile);
                }
            }
        }

    }

    private void printOrientation(File f) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(f);
        ExifIFD0Directory dir = metadata.getDirectory(ExifIFD0Directory.class);
        int orientation = dir.getInt(274);
        System.out.println(orientation);
    }

    private void printTags(File f) throws Exception {

        Metadata metadata = ImageMetadataReader.readMetadata(f);
        for (Directory dir : metadata.getDirectories()) {
            System.out.println("\n\n" + dir.getName() + " " + dir.getClass());
            for (Tag tag : dir.getTags()) {
                System.out.println(tag.getTagType() + " " + tag);
            }
        }
    }
}
