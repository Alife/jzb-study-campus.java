/*     */ package de.dfki.lt.freetts.mbrola;
/*     */ 
/*     */ import com.sun.speech.freetts.FeatureSet;
/*     */ import com.sun.speech.freetts.Item;
/*     */ import com.sun.speech.freetts.ProcessException;
/*     */ import com.sun.speech.freetts.Relation;
/*     */ import com.sun.speech.freetts.Utterance;
/*     */ import com.sun.speech.freetts.UtteranceProcessor;
/*     */ import com.sun.speech.freetts.util.Utilities;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MbrolaCaller
/*     */   implements UtteranceProcessor
/*     */ {
/*     */   private String[] cmd;
/*  28 */   private long closeDelay = 0L;
/*     */ 
/*     */   public MbrolaCaller(String[] cmd)
/*     */   {
/*  37 */     this.cmd = cmd;
/*  38 */     this.closeDelay = Utilities.getLong("de.dfki.lt.freetts.mbrola.MbrolaCaller.closeDelay", 100L).longValue();
/*     */   }
/*     */ 
/*     */   public void processUtterance(Utterance utterance)
/*     */     throws ProcessException
/*     */   {
/*     */     Process process;
/*     */     try
/*     */     {
/*  55 */       process = Runtime.getRuntime().exec(this.cmd);
/*     */     } catch (Exception e) {
/*  57 */       throw new ProcessException("Cannot start mbrola program: " + this.cmd);
/*     */     }
/*  59 */     PrintWriter toMbrola = new PrintWriter(process.getOutputStream());
/*  60 */     BufferedInputStream fromMbrola = new BufferedInputStream(process.getInputStream());
/*     */ 
/*  64 */     Relation segmentRelation = utterance.getRelation("Segment");
/*  65 */     Item segment = segmentRelation.getHead();
/*     */ 
/*  67 */     while (segment != null) {
/*  68 */       String name = segment.getFeatures().getString("name");
/*     */ 
/*  70 */       int dur = segment.getFeatures().getInt("mbr_dur");
/*     */ 
/*  75 */       String targets = segment.getFeatures().getString("mbr_targets");
/*  76 */       String output = name + " " + dur + " " + targets;
/*     */ 
/*  78 */       toMbrola.println(output);
/*  79 */       segment = segment.getNext();
/*     */     }
/*     */ 
/*  82 */     toMbrola.flush();
/*     */ 
/*  98 */     if (this.closeDelay > 0L)
/*     */       try {
/* 100 */         Thread.sleep(this.closeDelay);
/*     */       }
/*     */       catch (InterruptedException ie) {
/*     */       }
/* 104 */     toMbrola.close();
/*     */ 
/* 107 */     byte[] buffer = new byte[1024];
/*     */ 
/* 112 */     List audioData = new ArrayList();
/* 113 */     int totalSize = 0;
/* 114 */     int nrRead = -1;
/*     */     try
/*     */     {
/* 117 */       while ((nrRead = fromMbrola.read(buffer)) != -1) {
/* 118 */         if (nrRead < buffer.length) {
/* 119 */           byte[] slice = new byte[nrRead];
/* 120 */           System.arraycopy(buffer, 0, slice, 0, nrRead);
/* 121 */           audioData.add(slice);
/*     */         } else {
/* 123 */           audioData.add(buffer);
/* 124 */           buffer = new byte[buffer.length];
/*     */         }
/* 126 */         totalSize += nrRead;
/*     */       }
/* 128 */       fromMbrola.close();
/*     */     } catch (IOException e) {
/* 130 */       throw new ProcessException("Cannot read from mbrola");
/*     */     }
/*     */ 
/* 133 */     if (totalSize == 0) {
/* 134 */       throw new Error("No audio data read");
/*     */     }
/*     */ 
/* 137 */     utterance.setObject("mbrolaAudio", audioData);
/* 138 */     utterance.setInt("mbrolaAudioLength", totalSize);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 142 */     return "MbrolaCaller";
/*     */   }
/*     */ }

/* Location:           C:\WKSPs\Consolidado\TextToSpeech\mbrola\lib\mbrola.jar
 * Qualified Name:     de.dfki.lt.freetts.mbrola.MbrolaCaller
 * JD-Core Version:    0.6.0
 */