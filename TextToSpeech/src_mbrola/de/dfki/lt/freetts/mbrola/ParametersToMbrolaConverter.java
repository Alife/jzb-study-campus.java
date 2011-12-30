/*    */ package de.dfki.lt.freetts.mbrola;
/*    */ 
/*    */ import com.sun.speech.freetts.FeatureSet;
/*    */ import com.sun.speech.freetts.Item;
/*    */ import com.sun.speech.freetts.ProcessException;
/*    */ import com.sun.speech.freetts.Relation;
/*    */ import com.sun.speech.freetts.Utterance;
/*    */ import com.sun.speech.freetts.UtteranceProcessor;
/*    */ 
/*    */ public class ParametersToMbrolaConverter
/*    */   implements UtteranceProcessor
/*    */ {
/*    */   public void processUtterance(Utterance utterance)
/*    */     throws ProcessException
/*    */   {
/* 40 */     Relation segmentRelation = utterance.getRelation("Segment");
/* 41 */     Relation targetRelation = utterance.getRelation("Target");
/*    */ 
/* 43 */     Item segment = segmentRelation.getHead();
/* 44 */     Item target = null;
/* 45 */     if (targetRelation != null) target = targetRelation.getHead();
/* 46 */     float prevEnd = 0.0F;
/* 47 */     while (segment != null) {
/* 48 */       String name = segment.getFeatures().getString("name");
/*    */ 
/* 51 */       float end = segment.getFeatures().getFloat("end");
/*    */ 
/* 53 */       int dur = (int)((end - prevEnd) * 1000.0F);
/* 54 */       StringBuffer targetStringBuffer = new StringBuffer();
/*    */ 
/* 56 */       while ((target != null) && (target.getFeatures().getFloat("pos") <= end)) {
/* 57 */         float pos = target.getFeatures().getFloat("pos");
/*    */ 
/* 59 */         int percentage = (int)((pos - prevEnd) * 1000.0F) * 100 / dur;
/*    */ 
/* 61 */         int f0 = (int)target.getFeatures().getFloat("f0");
/* 62 */         targetStringBuffer.append(" ");
/* 63 */         targetStringBuffer.append(percentage);
/* 64 */         targetStringBuffer.append(" ");
/* 65 */         targetStringBuffer.append(f0);
/* 66 */         target = target.getNext();
/*    */       }
/*    */ 
/* 69 */       segment.getFeatures().setInt("mbr_dur", dur);
/* 70 */       segment.getFeatures().setString("mbr_targets", targetStringBuffer.toString().trim());
/*    */ 
/* 72 */       prevEnd = end;
/* 73 */       segment = segment.getNext();
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 78 */     return "ParametersToMbrolaConverter";
/*    */   }
/*    */ }

/* Location:           C:\WKSPs\Consolidado\TextToSpeech\mbrola\lib\mbrola.jar
 * Qualified Name:     de.dfki.lt.freetts.mbrola.ParametersToMbrolaConverter
 * JD-Core Version:    0.6.0
 */