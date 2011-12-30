import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import com.jzb.util.Tracer;

/**
 * 
 */

/**
 * @author n63636
 * 
 */
public class TTS_Test {

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
            TTS_Test me = new TTS_Test();
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
        Synthesizer synthesizer =_createSynthetizer();
        _selectVoice(synthesizer, "kevin16");
        
        SpeakableListener spklst = new SpeakableListener() {

            @Override
            public void markerReached(SpeakableEvent arg0) {
                Tracer._debug("markerReached: "+arg0);
            }

            @Override
            public void speakableCancelled(SpeakableEvent arg0) {
                Tracer._debug("speakableCancelled: "+arg0);
            }

            @Override
            public void speakableEnded(SpeakableEvent arg0) {
                Tracer._debug("speakableEnded: "+arg0);
            }

            @Override
            public void speakablePaused(SpeakableEvent arg0) {
                Tracer._debug("speakablePaused: "+arg0);
            }

            @Override
            public void speakableResumed(SpeakableEvent arg0) {
                Tracer._debug("speakableResumed: "+arg0);
            }

            @Override
            public void speakableStarted(SpeakableEvent arg0) {
                Tracer._debug("speakableStarted: "+arg0);
            }

            @Override
            public void topOfQueue(SpeakableEvent arg0) {
                Tracer._debug("topOfQueue: "+arg0);
            }

            @Override
            public void wordStarted(SpeakableEvent arg0) {
                Tracer._debug("wordStarted: "+arg0);
            }
            
        };
        
        synthesizer.addSpeakableListener(spklst);
        
        synthesizer.speakPlainText("Hello, how are you?", null);
        synthesizer.speakPlainText("Goodbye", null);
        synthesizer.cancelAll();
        synthesizer.speakPlainText("My friend", null);
        
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();

    }

    private Synthesizer _createSynthetizer() throws Exception {

        SynthesizerModeDesc smDesc = new SynthesizerModeDesc(null, "general",Locale.US,null,null );

        String user_home = System.getProperty("user.home");
        System.setProperty("user.home", "C:\\WKSPs\\Consolidado\\TextToSpeech\\src");
        Synthesizer synthesizer = Central.createSynthesizer(smDesc);
        System.setProperty("user.home", user_home);

        synthesizer.allocate();
        synthesizer.resume();

        return synthesizer;
    }

    private void _selectVoice(Synthesizer synthesizer, String voiceName) throws Exception {

        SynthesizerModeDesc desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
        Voice[] voices = desc.getVoices();
        Voice voice = null;
        for (int i = 0; i < voices.length; i++) {
            if (voices[i].getName().equals(voiceName)) {
                voice = voices[i];
                break;
            }
        }

        if (voice != null) {
            synthesizer.getSynthesizerProperties().setVoice(voice);
        }
    }
}
