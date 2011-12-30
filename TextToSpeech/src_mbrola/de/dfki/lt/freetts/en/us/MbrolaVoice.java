/*     */ package de.dfki.lt.freetts.en.us;
/*     */ 
/*     */ import com.sun.speech.freetts.Age;
/*     */ import com.sun.speech.freetts.Gender;
/*     */ import com.sun.speech.freetts.UtteranceProcessor;
/*     */ import com.sun.speech.freetts.en.us.CMULexicon;
/*     */ import com.sun.speech.freetts.en.us.CMUVoice;
/*     */ import com.sun.speech.freetts.util.Utilities;
/*     */ import de.dfki.lt.freetts.mbrola.MbrolaAudioOutput;
/*     */ import de.dfki.lt.freetts.mbrola.MbrolaCaller;
/*     */ import de.dfki.lt.freetts.mbrola.ParametersToMbrolaConverter;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class MbrolaVoice extends CMUVoice
/*     */ {
/*     */   private String databaseDirectory;
/*     */   private String database;
/*     */   private static final String MRPA_TO_SAMPA_RENAME_LIST = "V ah i iy I ih U uh { ae @ ax r= er A aa O ao u uw E eh EI ey AI ay OI oy aU aw @U ow j y h hh N ng S sh T th Z zh D dh tS ch dZ jh _ pau";
/*     */ 
/*     */   public MbrolaVoice(String databaseDirectory, String database, float rate, float pitch, float range, String name, Gender gender, Age age, String description, Locale locale, String domain, String organization, CMULexicon lexicon)
/*     */   {
/*  73 */     super(name, gender, age, description, locale, domain, organization, lexicon);
/*     */ 
/*  75 */     setRate(rate);
/*  76 */     setPitch(pitch);
/*  77 */     setPitchRange(range);
/*  78 */     this.databaseDirectory = databaseDirectory;
/*  79 */     this.database = database;
/*     */   }
/*     */ 
/*     */   protected UtteranceProcessor getUnitSelector()
/*     */     throws IOException
/*     */   {
/*  97 */     return new ParametersToMbrolaConverter();
/*     */   }
/*     */ 
/*     */   protected String[] getMbrolaCommand()
/*     */   {
/* 114 */     String[] cmd = { getMbrolaBinary(), "-e", "-R", "\"" + getRenameList() + "\"", getDatabase(), "-", "-.raw" };
/*     */ 
/* 118 */     return cmd;
/*     */   }
/*     */ 
/*     */   public String getMbrolaBase()
/*     */   {
/* 127 */     return Utilities.getProperty("mbrola.base", ".");
/*     */   }
/*     */ 
/*     */   public String getMbrolaBinary()
/*     */   {
/* 136 */     return getMbrolaBase() + File.separator + "mbrola";
/*     */   }
/*     */ 
/*     */   public String getRenameList()
/*     */   {
/* 146 */     return "V ah i iy I ih U uh { ae @ ax r= er A aa O ao u uw E eh EI ey AI ay OI oy aU aw @U ow j y h hh N ng S sh T th Z zh D dh tS ch dZ jh _ pau";
/*     */   }
/*     */ 
/*     */   public String getDatabase()
/*     */   {
/* 156 */     return getMbrolaBase() + File.separator + this.databaseDirectory + File.separator + this.database;
/*     */   }
/*     */ 
/*     */   protected UtteranceProcessor getUnitConcatenator()
/*     */     throws IOException
/*     */   {
/* 190 */     return new MbrolaCaller(getMbrolaCommand());
/*     */   }
/*     */ 
/*     */   protected UtteranceProcessor getAudioOutput()
/*     */     throws IOException
/*     */   {
/* 201 */     return new MbrolaAudioOutput();
/*     */   }
/*     */ 
/*     */   protected URL getResource(String resource)
/*     */   {
/* 209 */     return CMUVoice.class.getResource(resource);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 219 */     return "MbrolaVoice";
/*     */   }
/*     */ }

/* Location:           C:\WKSPs\Consolidado\TextToSpeech\mbrola\lib\mbrola.jar
 * Qualified Name:     de.dfki.lt.freetts.en.us.MbrolaVoice
 * JD-Core Version:    0.6.0
 */