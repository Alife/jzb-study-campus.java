/**
 * 
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.tools.zip.ZipFile;

/**
 * @author jzarzuela
 * 
 */
public class CheckFileUTF8 {

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            CheckFileUTF8 me = new CheckFileUTF8();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
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
    public void doIt2(String[] args) throws Exception {

        // System.setOut(new PrintStream(System.out, true, "x-ibm737"));

        System.out.println("------------------------------------------------------------");

        Map<String, String> env = System.getenv();
        TreeMap<Object, Object> sortedEnv = new TreeMap<Object, Object>();
        sortedEnv.putAll(env);
        for (Entry<Object, Object> entry : sortedEnv.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }

        System.out.println();
        System.out.println();
        System.out.println("------------------------------------------------------------");

        sortedEnv.clear();
        sortedEnv.putAll(System.getProperties());
        for (Entry<Object, Object> entry : sortedEnv.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }

        System.out.println();
        System.out.println();
        System.out.println("------------------------------------------------------------");
        File folder = new File("/Users/jzarzuela/Documents/personal/backup-discoextr/_Backup_/Imagenes/");
        File lfiles[] = folder.listFiles();
        if (lfiles != null) {
            for (File f : lfiles) {
                if (!f.exists()) {
                    System.err.println("*** " + f + " - " + f.exists());
                } else {
                    System.out.println("*** " + f + " - " + f.exists());
                }
            }
        }

        // String s = "Galería multimedia de Microsoft - true";
        // byte[] buffer = s.getBytes("UTF-8");
        // s = new String(buffer, "ISO-8859-15");
        // System.out.println(s);

    }

    public void doIt(String[] args) throws Exception {

        Files.walkFileTree(Paths.get("/Users/jzarzuela/Documents/personal/backup-discoextr/_Backup_/Imagenes/"), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!Files.isDirectory(file) && file.toString().toLowerCase().contains("microsoft")) {
                    File f = file.toFile();
                    System.out.println(Files.exists(file) + " - " + f.exists() + " - " + file);
                    new RandomAccessFile(f, "r").close();
                    new ZipFile(f).close();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
