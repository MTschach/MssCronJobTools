package de.mss.cronjob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import de.mss.configtools.ConfigFile;
import de.mss.utils.exception.MssException;

public class FileCronJobThread extends CronJobThread {

   private static final long serialVersionUID = -663955234913863988L;

   private String            cronFileName     = null;
   private long              lastModification = -1;


   public FileCronJobThread(ConfigFile c, long interval, String cfn) {
      super(c, interval);
      this.cronFileName = cfn;
   }


   @Override
   protected void refreshJobList() throws MssException {
      final File cronFile = new File(this.cronFileName);
      if (!cronFile.exists() || cronFile.isDirectory()) {
         return;
      }

      if (this.lastModification >= cronFile.lastModified()) {
         return;
      }

      this.lastModification = cronFile.lastModified();

      synchronized (this.jobList) {
         this.jobList.clear();

         try (BufferedReader br = new BufferedReader(new FileReader(cronFile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
               final CronJob cj = parseLine(line);
               if (cj != null) {
                  this.jobList.add(cj);
               }
            }
         }
         catch (final FileNotFoundException e) {
            throw new MssException(de.mss.cronjob.exception.ErrorCodes.ERROR_FILE_NOT_FOUND, e, "file " + this.cronFileName + " not found");
         }
         catch (final IOException e) {
            throw new MssException(de.mss.cronjob.exception.ErrorCodes.ERROR_FILE_NOT_FOUND, e, "file " + this.cronFileName + " not found");
         }
      }
   }


   private CronJob parseLine(String line) {
      if (line.length() == 0 || line.startsWith("#")) {
         return null;
      }

      String execMin = null;
      String execHour = null;
      String execMonth = null;
      String execDayOfMonth = null;
      String execDayOfWeek = null;
      String execTask = null;
      final StringTokenizer token = new StringTokenizer(line, " \t");

      if (token.hasMoreTokens()) {
         execMin = token.nextToken();
      }
      if (token.hasMoreTokens()) {
         execHour = token.nextToken();
      }
      if (token.hasMoreTokens()) {
         execMonth = token.nextToken();
      }
      if (token.hasMoreTokens()) {
         execDayOfMonth = token.nextToken();
      }
      if (token.hasMoreTokens()) {
         execDayOfWeek = token.nextToken();
      }
      if (token.hasMoreTokens()) {
         execTask = token.nextToken();
      }
      while (token.hasMoreTokens()) {
         final String s = token.nextToken();
         if (s.startsWith("#")) {
            break;
         }

         execTask += " " + s;
      }

      if (execMin != null && execHour != null && execMonth != null && execDayOfMonth != null && execDayOfWeek != null && execTask != null) {
         return new CronJob(execMin, execHour, execMonth, execDayOfMonth, execDayOfWeek, execTask);
      }

      return null;
   }


   @Override
   protected void startTask(String loggingId, String taskName) throws MssException {
      de.mss.utils.logging.MssLoggingFactory.getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "starting task " + taskName);
   }


   @Override
   protected void finishTask(String loggingId, String taskName) throws MssException {
      de.mss.utils.logging.MssLoggingFactory.getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "finishing task " + taskName);
   }

}
