/**
 * 
 */
package com.jzb.atm.mz;

import java.util.Locale;
import java.util.StringTokenizer;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

/**
 * @author n63636
 * 
 */
public class TTSFree {

    private Synthesizer synthesizer = null;

    /**
     * 
     */
    public TTSFree() {
        try {
            init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void init() throws Exception {

        // Create synthesizer
        SynthesizerModeDesc desc = new SynthesizerModeDesc(null, // engine name
                "general", // mode name
                Locale.US, // locale
                null, // running
                null); // voice

        String user_home = System.getProperty("user.home");
        System.setProperty("user.home", "C:\\WKSPs\\Consolidado\\TextToSpeech\\src");
        synthesizer = Central.createSynthesizer(desc);
        System.setProperty("user.home", user_home);

        if (synthesizer == null) {
            throw new Exception("No Voice Synthesizer found");
        }

        /*
         * Get the synthesizer ready to speak
         */
        synthesizer.allocate();
        synthesizer.resume();

        /*
         * Choose the voice.
         */
        desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
        Voice[] voices = desc.getVoices();
        Voice voice = null;
        for (int i = 0; i < voices.length; i++) {

            if (voices[i].getName().equals("kevin16")) {

                voice = voices[i];
                break;
            }
        }

        if (voice == null) {
            throw new Exception("Synthesizer does not have a voice named " + "kevin16" + ".");
        }

        synthesizer.getSynthesizerProperties().setVoice(voice);

    }

    public void say(String text, boolean queue) {
        /*
         * The the synthesizer to speak and wait for it to complete.
         */
        try {
            if (!queue) {
                synthesizer.cancelAll();
            }
            text = text.replace('.',',');
            text = text.replace(':',',');
            text = text.replace(",","@@");
            StringTokenizer st=new StringTokenizer(text,"@@");
            while(st.hasMoreTokens()) {
                String txt = st.nextToken();
                synthesizer.speakPlainText(txt, null);
            }
            // synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*
         * Clean up and leave.
         */
        // synthesizer.deallocate();
    }
}
