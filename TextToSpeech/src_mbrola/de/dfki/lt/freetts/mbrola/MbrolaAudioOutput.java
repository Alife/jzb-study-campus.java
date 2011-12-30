/*     */ package de.dfki.lt.freetts.mbrola;
/*     */ 
/*     */ import com.sun.speech.freetts.ProcessException;
/*     */ import com.sun.speech.freetts.Utterance;
/*     */ import com.sun.speech.freetts.UtteranceProcessor;
/*     */ import com.sun.speech.freetts.Voice;
/*     */ import com.sun.speech.freetts.audio.AudioPlayer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ 
/*     */ public class MbrolaAudioOutput
/*     */   implements UtteranceProcessor
/*     */ {
/*  37 */   private static final AudioFormat MBROLA_AUDIO = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000.0F, 16, 1, 2, 16000.0F, ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
/*     */ 
/*     */   public void processUtterance(Utterance utterance)
/*     */     throws ProcessException
/*     */   {
/*  58 */     utterance.getVoice().log("=== " + utterance.getString("input_text"));
/*     */ 
/*  61 */     AudioPlayer audioPlayer = utterance.getVoice().getAudioPlayer();
/*     */ 
/*  63 */     audioPlayer.setAudioFormat(MBROLA_AUDIO);
/*  64 */     audioPlayer.setVolume(utterance.getVoice().getVolume());
/*     */ 
/*  71 */     List audioData = (List)utterance.getObject("mbrolaAudio");
/*  72 */     if (audioData == null) {
/*  73 */       throw new ProcessException("No \"mbrolaAudio\" object is associated with utterance");
/*     */     }
/*     */ 
/*     */     int totalSize;
/*     */     try
/*     */     {
/*  81 */       totalSize = utterance.getInt("mbrolaAudioLength");
/*     */     } catch (NullPointerException npe) {
/*  83 */       totalSize = 0;
/*     */     }
/*     */ 
/*  86 */     audioPlayer.begin(totalSize);
/*     */ 
/*  88 */     for (Iterator it = audioData.iterator(); it.hasNext(); ) {
/*  89 */       byte[] bytes = (byte[])it.next();
/*  90 */       if (!audioPlayer.write(bytes)) {
/*  91 */         throw new ProcessException("Cannot write audio data to audio player");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  96 */     if (!audioPlayer.end())
/*  97 */       throw new ProcessException("audio player reports problem");
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 109 */     return "MbrolaAudioOutput";
/*     */   }
/*     */ }

/* Location:           C:\WKSPs\Consolidado\TextToSpeech\mbrola\lib\mbrola.jar
 * Qualified Name:     de.dfki.lt.freetts.mbrola.MbrolaAudioOutput
 * JD-Core Version:    0.6.0
 */