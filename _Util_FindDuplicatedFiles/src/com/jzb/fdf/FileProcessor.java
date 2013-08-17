/**
 * 
 */
package com.jzb.fdf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

/**
 * @author jzarzuela
 * 
 */
public class FileProcessor {

    private static final char[]     hex        = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

    private static final int        NUM_CHUNKS = 3;
    private static final int        CHUNK_SIZE = 1024;

    private int                     m_readCount;
    private long                    m_skipPos;
    private long                    m_currentPos;
    private ByteBuffer              m_chunckBuffer;
    private ByteBuffer              m_fullBuffer;
    private ByteBuffer              m_activeBuffer;
    private AsynchronousFileChannel m_afChannel;
    private Callback                m_callback;
    private MessageDigest           m_md5;

    private Path                    m_file;
    private String                  m_hash;

    // ----------------------------------------------------------------------------------------------------
    public interface Callback {

        public void fileFinished(FileProcessor processor, Throwable th);
    }

    // ----------------------------------------------------------------------------------------------------
    @SuppressWarnings("synthetic-access")
    private static class MyHandler implements CompletionHandler<Integer, FileProcessor> {

        public MyHandler() {
        }

        @Override
        public void completed(Integer result, FileProcessor attachment) {
            attachment._readCompleted(result);
        }

        @Override
        public void failed(Throwable exc, FileProcessor attachment) {
            attachment._readFailed(exc);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public FileProcessor() {
    }

    // ----------------------------------------------------------------------------------------------------
    public Path getFile() {
        return m_file;
    }

    // ----------------------------------------------------------------------------------------------------
    public String getHash() {
        return m_hash;
    }

    // ----------------------------------------------------------------------------------------------------
    public void process(Path file, BasicFileAttributes attrs, Callback callback) {

        // Inicializa el procesador y lanza la primera lectura
        try {
            _initProcessor(file, attrs, callback);
            m_activeBuffer.rewind();
            m_activeBuffer.limit(m_activeBuffer.capacity());
            m_afChannel.read(m_activeBuffer, m_currentPos, this, new MyHandler());
        } catch (Exception ex) {
            _doneProcessor(ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _readCompleted(Integer result) {

        // Procesa la informacon leida
        m_activeBuffer.rewind();
        m_activeBuffer.limit(result);
        m_md5.update(m_activeBuffer);

        // Continua leyendo si hace falta, o avisa de que se ha terminado
        m_currentPos += m_skipPos;
        m_readCount--;
        if (m_readCount > 0) {
            m_activeBuffer.rewind();
            m_activeBuffer.limit(m_activeBuffer.capacity());
            m_afChannel.read(m_activeBuffer, m_currentPos, this, new MyHandler());
        } else {
            _doneProcessor(null);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public void _readFailed(Throwable th) {

        _doneProcessor(th);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _doneProcessor(Throwable th) {

        if (th == null) {
            m_hash = _toHex(m_md5.digest());
        } else {
            th.printStackTrace();
            m_hash = "";
        }

        // Cierra el canal de lectura
        _disposeChannel();
        
        // Avisa de que se ha terminado el procesamiento
        m_callback.fileFinished(this, th);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _initProcessor(Path file, BasicFileAttributes attrs, Callback callback) throws Exception {

        // Recuerda la informacion de inicializacion
        m_file = file;
        m_callback = callback;

        // Reinicia el calculo de hashing
        if (m_md5 == null)
            m_md5 = MessageDigest.getInstance("MD5");
        m_md5.reset();
        byte length[] = new byte[8];
        long l = attrs.size();
        for (int n = 0; n < 8; n++) {
            length[n] = (byte) (l & 0x0FF);
            l = l >> 8;
        }
        m_md5.update(length);

        // Crea el canal asincrono para el fichero a procesar
        m_afChannel = AsynchronousFileChannel.open(file, StandardOpenOption.READ);

        // Crea los buffers (no lo estaban ya) y actualiza variables segun tamaño
        if (attrs.size() > NUM_CHUNKS * CHUNK_SIZE) {
            if (m_chunckBuffer == null)
                m_chunckBuffer = ByteBuffer.allocateDirect(CHUNK_SIZE);
            m_readCount = NUM_CHUNKS;
            m_skipPos = (attrs.size() - CHUNK_SIZE) / (NUM_CHUNKS - 1);
            m_activeBuffer = m_chunckBuffer;
        } else {
            if (m_fullBuffer == null)
                m_fullBuffer = ByteBuffer.allocateDirect(NUM_CHUNKS * CHUNK_SIZE);
            m_activeBuffer = m_fullBuffer;
            m_skipPos = 0;
            m_readCount = 1;
        }

        // Resetea el resto de variables
        m_currentPos = 0;
        m_hash = "";

    }

    // ----------------------------------------------------------------------------------------------------
    private void _disposeChannel() {

        try {
            m_afChannel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        m_afChannel = null;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _toHex(byte hash[]) {
        StringBuffer buf = new StringBuffer(hash.length * 2);

        for (int idx = 0; idx < hash.length; idx++)
            buf.append(hex[(hash[idx] >> 4) & 0x0f]).append(hex[hash[idx] & 0x0f]);

        return buf.toString();
    }

}
