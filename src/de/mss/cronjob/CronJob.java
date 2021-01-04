package de.mss.cronjob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class CronJob implements Serializable {

   private static final long serialVersionUID = -3248378826911619923L;


   private List<Integer>       execMinute                = new ArrayList<>();
   private List<Integer>       execHour                  = new ArrayList<>();
   private List<Integer>       execMonth                 = new ArrayList<>();
   private List<Integer>       execDayOfMonth            = new ArrayList<>();
   private List<Integer>       execDayOfWeek             = new ArrayList<>();
   private String              execTask                  = null;
   protected java.util.Date    nextExecTime              = null;

   protected GregorianCalendar currentTimeForCalculation = null;

   public CronJob(String m, String h, String mon, String dom, String dow, String t) {
      setExecMin(m);
      setExecHour(h);
      setExecMonth(mon);
      setExecDayOfMonth(dom);
      setExecDayOfWeek(dow);
      setExecTask(t);
   }


   public void setExecMin(String m) {
      this.execMinute = getExecList(m, 0, 59);
   }


   public void setExecHour(String h) {
      this.execHour = getExecList(h, 0, 23);
   }


   public void setExecMonth(String m) {
      this.execMonth = getExecList(m, 1, 12);
   }


   public void setExecDayOfMonth(String d) {
      this.execDayOfMonth = getExecList(d, 1, 31);
   }


   public void setExecDayOfWeek(String d) {
      this.execDayOfWeek = getExecList(d, 0, 6);
      for (int i = 0; i < this.execDayOfWeek.size(); i++ ) {
         this.execDayOfWeek.set(i, Integer.valueOf(this.execDayOfWeek.get(i).intValue() + 1));
      }
   }


   public void setExecTask(String t) {
      this.execTask = t;
   }


   public String getExecTask() {
      return this.execTask;
   }


   public boolean shouldExecute() {
      if (this.nextExecTime == null) {
         calculateNextExecTime();
      }

      final boolean exec = this.nextExecTime != null && this.nextExecTime.compareTo(getCurrentTimeForCalculation().getTime()) <= 0;
      if (exec) {
         calculateNextExecTime();
      }

      return exec;
   }


   private void calculateNextExecTime() {
      final GregorianCalendar nextTime = getCurrentTimeForCalculation();
      nextTime.set(Calendar.SECOND, 0);
      if (this.nextExecTime != null) {
         nextTime.add(Calendar.MINUTE, 1);
      }

      this.nextExecTime = nextTime.getTime();
      boolean daySwitch = false;

      do {
         calculateExecMinute(nextTime);
         daySwitch = calculateExecHour(nextTime)
               ||
               calculateExecDayOfMonth(nextTime)
               ||
               calculateExecMonth(nextTime)
               ||
               calculateExecDayOfWeek(nextTime);

         if (daySwitch) {
            nextTime.set(Calendar.MINUTE, this.execMinute.get(0).intValue());
         }
      }
      while (daySwitch);

      this.nextExecTime = nextTime.getTime();
   }


   private GregorianCalendar getCurrentTimeForCalculation() {
      if (this.currentTimeForCalculation != null) {
         return this.currentTimeForCalculation;
      }

      return new GregorianCalendar();
   }


   private void calculateExecMinute(GregorianCalendar nextTime) {
      for (final Integer m : this.execMinute) {
         if (m.intValue() >= nextTime.get(Calendar.MINUTE)) {
            nextTime.set(Calendar.MINUTE, m.intValue());
            return;
         }
      }

      nextTime.add(Calendar.HOUR_OF_DAY, 1);
      nextTime.set(Calendar.MINUTE, this.execMinute.get(0).intValue());
      this.nextExecTime = nextTime.getTime();
   }


   private boolean calculateExecHour(GregorianCalendar nextTime) {
      for (final Integer m : this.execHour) {
         if (m.intValue() >= nextTime.get(Calendar.HOUR_OF_DAY)) {
            nextTime.set(Calendar.HOUR_OF_DAY, m.intValue());
            return false;
         }
      }

      nextTime.add(Calendar.DAY_OF_MONTH, 1);
      nextTime.set(Calendar.HOUR_OF_DAY, this.execHour.get(0).intValue());
      this.nextExecTime = nextTime.getTime();
      return true;
   }


   private boolean calculateExecDayOfMonth(GregorianCalendar nextTime) {
      for (final Integer m : this.execDayOfMonth) {
         if (m.intValue() >= nextTime.get(Calendar.DAY_OF_MONTH) && isInCurrentMonth(m, nextTime)) {
            nextTime.set(Calendar.DAY_OF_MONTH, m.intValue());
            return false;
         }
      }

      nextTime.add(Calendar.MONTH, 1);
      nextTime.set(Calendar.DAY_OF_MONTH, this.execDayOfMonth.get(0).intValue());
      this.nextExecTime = nextTime.getTime();
      return true;
   }


   private boolean calculateExecMonth(GregorianCalendar nextTime) {
      final int currMonth = nextTime.get(Calendar.MONTH) + 1;
      for (final Integer m : this.execMonth) {
         if (m.intValue() >= currMonth) {
            nextTime.set(Calendar.MONTH, m.intValue() - 1);
            return false;
         }
      }

      nextTime.add(Calendar.YEAR, 1);
      nextTime.set(Calendar.MONTH, this.execMonth.get(0).intValue());
      this.nextExecTime = nextTime.getTime();
      return true;
   }


   private boolean calculateExecDayOfWeek(GregorianCalendar nextTime) {
      for (final Integer m : this.execDayOfWeek) {
         if (m.intValue() >= nextTime.get(Calendar.DAY_OF_WEEK)) {
            nextTime.set(Calendar.DAY_OF_WEEK, m.intValue());
            return false;
         }
      }

      do {
         nextTime.add(Calendar.DAY_OF_WEEK, 1);
      }
      while (nextTime.get(Calendar.DAY_OF_WEEK) != this.execDayOfWeek.get(0).intValue());

      this.nextExecTime = nextTime.getTime();

      return true;
   }


   private boolean isInCurrentMonth(Integer m, GregorianCalendar nextTime) {
      final int year = nextTime.get(Calendar.YEAR);
      final int month = nextTime.get(Calendar.MONTH);

      int febDays = 28;
      if (year % 4 == 0) {
         febDays = 29;
      }

      switch (month) {
         case 1:
            return m.intValue() <= febDays;

         case 3:
         case 5:
         case 8:
         case 10:
            return m.intValue() <= 30;

         default:
            return m.intValue() <= 31;
      }
   }


   private List<Integer> getExecList(String value, int first, int last) {
      final List<Integer> ret = new ArrayList<>();

      if ("*".equals(value)) {
         for (int i = first; i <= last; i++ ) {
            ret.add(Integer.valueOf(i));
         }
      } else if (value.matches("\\d+(,\\d+)*")) {
         final String[] p = value.split(",");
         for (final String s : p) {
            ret.add(Integer.valueOf(s));
         }
      } else if (value.matches("\\d+-\\d+\\/\\*")) {
         final String[] p = value.split("/")[0].split("-");
         int start = Integer.parseInt(p[0]);
         final int end = Integer.parseInt(p[1]);
         while (start <= end) {
            ret.add(Integer.valueOf(start++ ));
         }
      } else if (value.matches("\\d+-\\d+\\/\\d+")) {
         final String[] p1 = value.split("/");
         final String[] p = p1[0].split("-");
         final int start = Integer.parseInt(p[0]);
         final int end = Integer.parseInt(p[1]);
         final int step = Integer.parseInt(p1[1]);
         for (int i = start; i <= end; i += step) {
            ret.add(Integer.valueOf(i));
         }
      } else if (value.matches("\\*/\\d+")) {
         final String[] p1 = value.split("/");
         final int start = first;
         final int end = last;
         final int step = Integer.parseInt(p1[1]);
         for (int i = start; i <= end; i += step) {
            ret.add(Integer.valueOf(i));
         }
      } else {
         for (int i = first; i <= last; i++ ) {
            ret.add(Integer.valueOf(i));
         }
      }

      Collections.sort(ret);

      return ret;
   }
}
