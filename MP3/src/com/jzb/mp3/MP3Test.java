/**
 * 
 */
package com.jzb.mp3;

import java.io.File;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileFormat;

/**
 * @author n63636
 * 
 */
public class MP3Test {

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
            MP3Test me = new MP3Test();
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

        BasicPlayerListener listener = new BasicPlayerListener() {

            @Override
            public void stateUpdated(BasicPlayerEvent event) {
                System.out.println("stateUpdated: " + event);

            }

            @Override
            public void setController(BasicController controller) {
                System.out.println("setController: " + controller);
            }

            @Override
            public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
                System.out.println("progress: " + bytesread + ", " + microseconds + ", " + properties);
            }

            @Override
            public void opened(Object stream, Map properties) {
                System.out.println("opened: " + stream + ", " + properties);
            }
        };

        BasicPlayer bp = new BasicPlayer();
        bp.addBasicPlayerListener(listener);

        File fmp3 = new File("C:\\JZarzuela\\MP3\\BSO\\04 - Picking Up Brides.mp3");
        long duration = _getDuration(fmp3);
        double ratio = (double) fmp3.length() / (double) duration;
        
        bp.open(fmp3);
        bp.play();
        bp.seek((long)(150000000*ratio));
        
        Thread.sleep(200000);
    }

    private long _getDuration(File fmp3) throws Exception {

        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(fmp3);
        // for (Map.Entry<String, Object> entry : audioFileFormat.properties().entrySet()) {
        // System.out.println(entry.getKey() + " -> " + entry.getValue());
        // }

        Object v = audioFileFormat.properties().get("duration");
        if (v != null) {
            if (v instanceof Number) {
                return ((Number) v).longValue();
            } else if (v instanceof String) {
                return Long.parseLong((String) v);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
