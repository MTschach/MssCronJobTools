package de.mss.cronjob;

import java.util.GregorianCalendar;

public class CronJobForTest extends CronJob {

   private static final long serialVersionUID = 5546055380141577015L;


   public CronJobForTest(String m, String h, String mon, String dom, String dow, String t) {
      super(m, h, mon, dom, dow, t);
   }


   public void setCurrentTimeForCalculation(GregorianCalendar gc) {
      this.currentTimeForCalculation = gc;
   }


   public void clearCurrentTimeForCalculation() {
      this.currentTimeForCalculation = null;
   }


   public java.util.Date getNextExecTime() {
      return this.nextExecTime;
   }


   public void setNextExecTime(java.util.Date t) {
      this.nextExecTime = t;
   }
}
