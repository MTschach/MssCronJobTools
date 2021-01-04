package de.mss.cronjob;

import de.mss.configtools.ConfigFile;
import de.mss.utils.exception.MssException;

public class CronJobTaskForTest extends CronJobTask {

   private static final long serialVersionUID = 5028095214312868857L;

   private ConfigFile        cfgFile          = null;
   private String            logId            = null;

   public CronJobTaskForTest(String n) {
      super(n);
   }


   @Override
   public void runTask(String loggingId, ConfigFile cfg) throws MssException {
      this.logId = loggingId;
      this.cfgFile = cfg;
   }


   public String getLogId() {
      return this.logId;
   }


   public ConfigFile getCfgFile() {
      return this.cfgFile;
   }
}
