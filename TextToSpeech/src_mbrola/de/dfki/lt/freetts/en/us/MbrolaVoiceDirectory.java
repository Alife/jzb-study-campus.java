/*    */ package de.dfki.lt.freetts.en.us;
/*    */ 
/*    */ import com.sun.speech.freetts.Age;
/*    */ import com.sun.speech.freetts.Gender;
/*    */ import com.sun.speech.freetts.ValidationException;
/*    */ import com.sun.speech.freetts.Voice;
/*    */ import com.sun.speech.freetts.VoiceDirectory;
/*    */ import com.sun.speech.freetts.en.us.CMULexicon;
/*    */ import com.sun.speech.freetts.util.Utilities;
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Locale;
/*    */ 
/*    */ public class MbrolaVoiceDirectory extends VoiceDirectory
/*    */ {
/*    */   public Voice[] getVoices()
/*    */   {
/* 23 */     String base = Utilities.getProperty("mbrola.base", null);
/*    */ 
/* 25 */     if ((base == null) || (base.trim().length() == 0)) {
/* 26 */       System.out.println("System property \"mbrola.base\" is undefined.  Will not use MBROLA voices.");
/*    */ 
/* 29 */       return new Voice[0];
/*    */     }
/*    */ 
/* 32 */     CMULexicon lexicon = new CMULexicon("cmulex");
/*    */ 
/* 34 */     Voice mbrola1 = new MbrolaVoice("us1", "us1", 150.0F, 180.0F, 22.0F, "mbrola_us1", Gender.FEMALE, Age.YOUNGER_ADULT, "MBROLA Voice us1", Locale.US, "general", "mbrola", lexicon);
/* 34 */     Voice mbrola1_1 = new MbrolaVoice("us1", "us1", 150.0F, 185.0F, 22.0F, "mbrola_us1.1", Gender.MALE, Age.OLDER_ADULT, "MBROLA Voice us1", Locale.ITALY, "general", "mbrola", lexicon);
/*    */ 
/* 40 */     Voice mbrola2 = new MbrolaVoice("us2", "us2", 150.0F, 115.0F, 12.0F, "mbrola_us2", Gender.MALE, Age.YOUNGER_ADULT, "MBROLA Voice us2", Locale.US, "general", "mbrola", lexicon);
/*    */ 
/* 46 */     Voice mbrola3 = new MbrolaVoice("us3", "us3", 150.0F, 125.0F, 12.0F, "mbrola_us3", Gender.MALE, Age.YOUNGER_ADULT, "MBROLA Voice us3", Locale.US, "general", "mbrola", lexicon);
/*    */ 
/* 52 */     Voice[] voices = { mbrola1, mbrola1_1,mbrola2, mbrola3 };
/*    */ 
/* 54 */     ArrayList validVoices = new ArrayList();
/* 55 */     int count = 0;
/*    */ 
/* 57 */     for (int i = 0; i < voices.length; i++) {
/* 58 */       MbrolaVoiceValidator validator = new MbrolaVoiceValidator((MbrolaVoice)voices[i]);
/*    */       try
/*    */       {
/* 61 */         validator.validate();
/* 62 */         validVoices.add(voices[i]);
/* 63 */         count++;
/*    */       }
/*    */       catch (ValidationException ve) {
/*    */       }
/*    */     }
/* 68 */     if (count == 0) {
/* 69 */       System.err.println("\nCould not validate any MBROLA voices at\n\n  " + base + "\n");
/*    */ 
/* 73 */       if (base.indexOf('~') != -1) {
/* 74 */         System.err.println("DO NOT USE ~ as part of the path name\nto specify the mbrola.base property.");
/*    */       }
/*    */ 
/* 78 */       System.err.println("Make sure you FULLY specify the path to\nthe MBROLA directory using the mbrola.base\nsystem property.\n");
/*    */ 
/* 82 */       return new Voice[0];
/*    */     }
/* 84 */     return (Voice[])validVoices.toArray(new Voice[count]);
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 93 */     System.out.println(new MbrolaVoiceDirectory().toString());
/*    */   }
/*    */ }

/* Location:           C:\WKSPs\Consolidado\TextToSpeech\mbrola\lib\mbrola.jar
 * Qualified Name:     de.dfki.lt.freetts.en.us.MbrolaVoiceDirectory
 * JD-Core Version:    0.6.0
 */