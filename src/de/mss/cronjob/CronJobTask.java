package de.mss.cronjob;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;

import de.mss.configtools.ConfigFile;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;

public abstract class CronJobTask implements Serializable {

   private static final long serialVersionUID     = -9126002643514542790L;

   private String            name                 = null;
   private int               maxParallelTasks     = 1;
   private int               currentParallelTasks = 0;


   public CronJobTask(String n) {
      this.name = n;
   }


   public CronJobTask(String n, int mpt) {
      this.name = n;
      this.maxParallelTasks = mpt;
   }


   public abstract void execTask(String loggingId, ConfigFile cfg) throws MssException;


   public String getName() {
      return this.name;
   }


   public void runTask(String loggingId, ConfigFile cfg) throws MssException {
      synchronized (this) {
         if (this.currentParallelTasks >= this.maxParallelTasks) {
            LogManager
                  .getLogger()
                  .debug(Tools.formatLoggingId(loggingId) + "maximum numbers of parallel tasks for " + this.name + " reached -> skipping");
            return;
         }

         this.currentParallelTasks++ ;
      }

      execTask(loggingId, cfg);

      synchronized (this) {
         if (this.currentParallelTasks > 0) {
            this.currentParallelTasks-- ;
         }
      }
   }


}
