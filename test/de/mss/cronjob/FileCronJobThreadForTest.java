package de.mss.cronjob;

import java.util.List;

import de.mss.configtools.ConfigFile;
import de.mss.utils.exception.MssException;

public class FileCronJobThreadForTest extends FileCronJobThread {

   private static final long serialVersionUID = 5247348616277835890L;

   public FileCronJobThreadForTest(ConfigFile c, long interval, String cfn) {
      super(c, interval, cfn);
   }


   public List<CronJob> getJobList() {
      return this.jobList;
   }


   public void readJobList() throws MssException {
      refreshJobList();
   }
}
