/**
 * 
 */
package com.jzb.fdf;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
@SuppressWarnings("synthetic-access")
public class FolderProcessor implements FileProcessor.Callback {

    private static final int                          MAX_NUM_FILEPROCESSORS = 4 * 3 * 4;

    private static ExecutorService                    s_executor             = Executors.newFixedThreadPool(2);
    private static Phaser                             s_folderProcessorCount = new Phaser(1);
    private static LinkedBlockingQueue<FileProcessor> s_processors           = new LinkedBlockingQueue();
    private Phaser                                    m_fileProcessorCount   = null;
    private static Path                               s_outputFolder;
    private Path                                      m_outputFile;
    private Path                                      m_folder;
    private TreeMap<String, String>                   m_fileData             = new TreeMap();

    // ----------------------------------------------------------------------------------------------------
    public static void init(final String outputFolder) {

        s_outputFolder = FileSystems.getDefault().getPath(outputFolder);

        for (int n = 0; n < MAX_NUM_FILEPROCESSORS; n++) {
            try {
                s_processors.put(new FileProcessor());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public static void spawnFolderProcessor(final Path folder) {

        s_folderProcessorCount.register();
        s_executor.execute(new Runnable() {

            public void run() {
                try {
                    FolderProcessor fp = new FolderProcessor(folder);
                    fp.process();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                s_folderProcessorCount.arriveAndDeregister();
            }
        });

    }

    // ----------------------------------------------------------------------------------------------------
    public static void awaitTermination() throws InterruptedException {

        s_folderProcessorCount.arriveAndAwaitAdvance();
        s_executor.shutdown();
        s_executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    // ----------------------------------------------------------------------------------------------------
    public FolderProcessor(Path folder) {
        m_folder = folder;
        m_outputFile = Paths.get(s_outputFolder.toString(), m_folder.toString().replace('/', '#').replace('\\', '#') + "_out.txt");
    }

    // ----------------------------------------------------------------------------------------------------
    public void process() throws IOException {

        // Antes de nada chequea que no se haya procesado previamente
        if (Files.exists(m_outputFile, LinkOption.NOFOLLOW_LINKS)) {
           // return;
        }

        // procesa la informacion
        Files.walkFileTree(m_folder, EnumSet.noneOf(FileVisitOption.class), 1, new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                m_fileProcessorCount = new Phaser(1);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                m_fileProcessorCount.arriveAndAwaitAdvance();
                _doneProcessor();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println("** Error: " + exc);
                return null;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                if (attrs.isDirectory()) {

                    FolderProcessor.spawnFolderProcessor(file);

                } else if (attrs.isRegularFile()) {

                    _spawnFileProcessor(file, attrs);
                }

                return FileVisitResult.CONTINUE;
            }

        });

    }

    // ----------------------------------------------------------------------------------------------------
    public void fileFinished(FileProcessor processor, Throwable th) {

        // Recoge la informacion del proceso finalizado
        synchronized (m_fileData) {
            m_fileData.put(processor.getFile().toString(), processor.getHash());
        }
        Tracer._debug(processor.getFile().toString());


        // Retorna el procesador al pool
        try {
            s_processors.put(processor);
        } catch (InterruptedException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        // Decrementa la cuenta de trabajos pendientes
        m_fileProcessorCount.arriveAndDeregister();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _spawnFileProcessor(final Path file, final BasicFileAttributes attrs) {

        try {
            // Solicita un
            FileProcessor processor = s_processors.take();
            m_fileProcessorCount.register();
            processor.process(file, attrs, this);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _doneProcessor() {

        // Guarda la informacion calculada
        try (Writer wtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(m_outputFile.toFile()), "UTF-8"))) {

            for (Map.Entry<String, String> entry : m_fileData.entrySet()) {
                wtr.write(entry.getValue());
                wtr.write(", ");
                wtr.write(entry.getKey());
                wtr.write("\n");
            }
            wtr.flush();

        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        
        Tracer._info("Finished folder: "+m_folder);

    }

}
