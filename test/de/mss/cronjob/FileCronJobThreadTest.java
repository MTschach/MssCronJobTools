package de.mss.cronjob;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.junit.Test;

import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class FileCronJobThreadTest extends TestCase {

   private static final String      CRON_FILE_NAME = "cronjobs.txt";

   private FileCronJobThreadForTest cronJobThread  = null;

   @Override
   public void setUp() throws Exception {
      super.setUp();

      initCronFile();

      if (this.cronJobThread == null) {
         this.cronJobThread = new FileCronJobThreadForTest(null, 1000, CRON_FILE_NAME);
      }

   }


   private void initCronFile() throws Exception {
      final File cronFile = new File(CRON_FILE_NAME);

      if (cronFile.exists() && System.currentTimeMillis() - cronFile.lastModified() < 20000) {
         return;
      }

      if (cronFile.isDirectory()) {
         throw new Exception("Verzeichnis kann nicht als Cronfile dienen");
      }

      try (FileOutputStream fos = new FileOutputStream(cronFile)) {
         fos.write("# Test cronfile\r\n".getBytes());
         fos.write("\r\n".getBytes());
         fos.write("# Crontask 1\n".getBytes());
         fos.write("* *  * * * Crontask 1\n".getBytes());
         fos.write("\r\n".getBytes());
         fos.write("# Crontask 2\n".getBytes());
         fos.write("*\t\t*\t*   *\t* Crontask 2     # ist zum Test\n".getBytes());
         fos.write("\r\n".getBytes());
         fos.write(" \n".getBytes());
         fos.write("* * * * *\n".getBytes());
         fos.write("* * * *\n".getBytes());
         fos.write("* * *\n".getBytes());
         fos.write("* *\n".getBytes());
         fos.write("*\n".getBytes());
         fos.flush();
      }
   }


   @Override
   public void tearDown() throws Exception {
      super.tearDown();
   }


   @Test
   public void test() throws MssException {
      this.cronJobThread.readJobList();
      final List<CronJob> jobList = this.cronJobThread.getJobList();

      assertNotNull("joblist is not null", jobList);
      assertEquals("number of jobs", Integer.valueOf(2), Integer.valueOf(jobList.size()));

      for (int i = 1; i <= jobList.size(); i++ ) {
         final CronJob c = jobList.get(i - 1);
         assertNotNull("Cronjob is not null", c);
         assertEquals("exec task", "Crontask " + i, c.getExecTask());
      }
   }


   @Test
   public void testRunning() throws MssException, InterruptedException {
      this.cronJobThread.readJobList();

      final CronJobTaskForTest cronTask = new CronJobTaskForTest("Crontask 2");

      this.cronJobThread.addTask(cronTask);

      assertNull("logId before run", cronTask.getLogId());
      assertNull("cfg before run", cronTask.getCfgFile());

      this.cronJobThread.start();

      Thread.sleep(1000);

      this.cronJobThread.stopRunning();

      assertNotNull("logId after run", cronTask.getLogId());
      assertNull("cfg after run", cronTask.getCfgFile());
   }
}
