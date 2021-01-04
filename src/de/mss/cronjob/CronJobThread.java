package de.mss.cronjob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mss.configtools.ConfigFile;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;

public abstract class CronJobThread extends Thread implements Serializable {

   private static final long serialVersionUID = -4470694468031995579L;


   protected final List<CronJob>          jobList  = Collections.synchronizedList(new ArrayList<>());

   private final Map<String, CronJobTask> taskList = new HashMap<>();


   private ConfigFile cfg = null;


   private boolean isRunning = false;


   private long runInterval = 10000;


   public CronJobThread(ConfigFile c, long interval) {
      this.cfg = c;
      this.runInterval = interval;
   }


   protected abstract void refreshJobList() throws MssException;


   protected abstract void startTask(String loggingId, String taskName) throws MssException;


   protected abstract void finishTask(String loggingId, String taskName) throws MssException;


   public void addTask(CronJobTask t) {
      this.taskList.put(t.getName(), t);
   }


   public void removeTask(CronJobTask t) {
      this.taskList.remove(t.getName());
   }


   @Override
   public void run() {
      this.isRunning = true;

      while (this.isRunning) {
         try {
            singleRun(Tools.getId(new Throwable()));
         }
         catch (final MssException e) {
            Tools.doNullLog(e);
         }
         catch (final Exception e) {
            Tools.doNullLog(e);
         }

         try {
            sleep(this.runInterval);
         }
         catch (final InterruptedException e) {
            Tools.doNullLog(e);
         }
      }

      this.isRunning = false;
   }


   private void singleRun(String loggingId) throws MssException {
      refreshJobList();

      final List<CronJob> workingJobList = getWorkingList();
      for (final CronJob cj : workingJobList) {
         if (cj.shouldExecute()) {
            final CronJobTask t = this.taskList.get(cj.getExecTask());
            if (t != null) {
               startTask(loggingId, t.getName());

               t.runTask(loggingId, this.cfg);

               finishTask(loggingId, t.getName());
            }
         }
      }
   }


   private List<CronJob> getWorkingList() {
      final List<CronJob> ret = new ArrayList<>();

      synchronized (this.jobList) {
         for (final CronJob c : this.jobList) {
            ret.add(c);
         }
      }

      return ret;
   }


   public void stopRunning() {
      this.isRunning = false;
   }
}
