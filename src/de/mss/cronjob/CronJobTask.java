package de.mss.cronjob;

import java.io.Serializable;

import de.mss.configtools.ConfigFile;
import de.mss.utils.exception.MssException;

public abstract class CronJobTask implements Serializable {

   private static final long serialVersionUID = -9126002643514542790L;

   private String            name             = null;


   public CronJobTask(String n) {
      this.name = n;
   }


   public abstract void runTask(String loggingId, ConfigFile cfg) throws MssException;


   public String getName() {
      return this.name;
   }
}
