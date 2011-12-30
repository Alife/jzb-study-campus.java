/*    */ package de.dfki.lt.freetts.en.us;
/*    */ 
/*    */ import com.sun.speech.freetts.ValidationException;
/*    */ import com.sun.speech.freetts.Validator;
/*    */ import com.sun.speech.freetts.util.Utilities;
/*    */ import java.io.File;
/*    */ 
/*    */ public class MbrolaVoiceValidator
/*    */   implements Validator
/*    */ {
/*    */   private MbrolaVoice mbrolaVoice;
/*    */ 
/*    */   public MbrolaVoiceValidator(MbrolaVoice mbrolaVoice)
/*    */   {
/* 37 */     this.mbrolaVoice = mbrolaVoice;
/*    */   }
/*    */ 
/*    */   public void validate()
/*    */     throws ValidationException
/*    */   {
/* 46 */     String mbrolaBase = Utilities.getProperty("mbrola.base", null);
/* 47 */     File mbrolaBinary = new File(this.mbrolaVoice.getMbrolaBinary());
/* 48 */     File mbrolaVoiceDB = new File(this.mbrolaVoice.getDatabase());
/*    */ 
/* 50 */     if ((mbrolaBase == null) || (mbrolaBase.length() == 0)) {
/* 51 */       throw new ValidationException("System property \"mbrola.base\" is undefined. You might need to set the MBROLA_DIR environment variable.");
/*    */     }
/*    */ 
/* 55 */     if (!mbrolaBinary.exists()) {
/* 56 */       throw new ValidationException("No MBROLA binary at: " + this.mbrolaVoice.getMbrolaBinary());
/*    */     }
/*    */ 
/* 59 */     if (!mbrolaVoiceDB.exists())
/* 60 */       throw new ValidationException("No voice database for " + this.mbrolaVoice.getName() + " at: " + this.mbrolaVoice.getDatabase());
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 72 */     return this.mbrolaVoice.toString() + "Validator";
/*    */   }
/*    */ }

/* Location:           C:\WKSPs\Consolidado\TextToSpeech\mbrola\lib\mbrola.jar
 * Qualified Name:     de.dfki.lt.freetts.en.us.MbrolaVoiceValidator
 * JD-Core Version:    0.6.0
 */