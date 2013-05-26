/**
 * 
 */
package com.jzb.fcmp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

/**
 * @author jzarzuela
 * 
 */
public class FileProcessor implements Callable<Long> {

    private static final int BUFFER_CHUCK_SIZE = 1024;
    private static final int BUFFER_FULL_SIZE  = 3 * BUFFER_CHUCK_SIZE;

    private File             m_fIn;

    public FileProcessor(File fIn) {
        m_fIn = fIn;
    }

    public Long call() throws Exception {

        MessageDigest md5;

        FileInputStream fis = new FileInputStream(m_fIn);
        FileChannel fc = fis.getChannel();

        int len = 0;
        byte dataBuffer[] = new byte[BUFFER_FULL_SIZE];
        if (m_fIn.length() <= BUFFER_FULL_SIZE) {

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_FULL_SIZE);
            len = fc.read(buffer);
            buffer.get(dataBuffer, 0, BUFFER_FULL_SIZE);
        } else {

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CHUCK_SIZE);

            len += fc.read(buffer, 0);
            buffer.get(dataBuffer, 0, BUFFER_CHUCK_SIZE);
            
            len += fc.read(buffer, (m_fIn.length() - BUFFER_CHUCK_SIZE) / 2);
            buffer.get(dataBuffer, BUFFER_CHUCK_SIZE, BUFFER_CHUCK_SIZE);
            
            len += fc.read(buffer, m_fIn.length() - BUFFER_CHUCK_SIZE);
            buffer.get(dataBuffer, 2 * BUFFER_CHUCK_SIZE, BUFFER_CHUCK_SIZE);

            if (len != 3072) {
                throw new Exception("Not enought bytes (" + len + ") read from file '" + m_fIn + "'");
            }

        }

        // TODO Auto-generated method stub
        return null;
    }
}
