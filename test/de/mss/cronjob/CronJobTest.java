package de.mss.cronjob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;


public class CronJobTest {


   private CronJobForTest classUnderTest;


   private String formatTime(java.util.Date time) {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
   }


   private GregorianCalendar getTime(String timestamp) throws ParseException {
      final GregorianCalendar gc = new GregorianCalendar();

      gc.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp));

      return gc;
   }


   @Test
   public void test() {
      this.classUnderTest = new CronJobForTest("*/15", "8-18/*", "*", "*", "blah", "testTask");
      this.classUnderTest.clearCurrentTimeForCalculation();
      this.classUnderTest.shouldExecute();

      assertEquals("testTask", this.classUnderTest.getExecTask());
   }


   @Test
   public void testDefault() throws ParseException {
      this.classUnderTest = new CronJobForTest("*", "*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-10 12:12:23"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-10 12:12:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-10 12:13:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testDefaultFirstRun() throws ParseException {
      this.classUnderTest = new CronJobForTest("*", "*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-10 12:12:23"));

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-10 12:13:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testDefinedInterval() throws ParseException {
      this.classUnderTest = new CronJobForTest("10-15/*", "*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-10 12:10:23"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-10 12:10:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-10 12:11:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testInterval() throws ParseException {
      this.classUnderTest = new CronJobForTest("0-59/10", "*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-10 12:10:23"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-10 12:10:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-10 12:20:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testInterval1() throws ParseException {
      this.classUnderTest = new CronJobForTest("*/10", "*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-10 12:10:23"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-10 12:10:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-10 12:20:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testMultipleValues() throws ParseException {
      this.classUnderTest = new CronJobForTest("10,30,50", "*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-10 12:10:23"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-10 12:10:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-10 12:30:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testNextDay() throws ParseException {
      this.classUnderTest = new CronJobForTest("*/15", "8-18/*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-22 18:45:00"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-22 18:45:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-23 08:00:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testNextDayOfMonth() throws ParseException {
      this.classUnderTest = new CronJobForTest("*/15", "8-18/*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-22 18:45:00"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-22 18:45:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-23 08:00:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testNextDayOfNextMonth() throws ParseException {
      this.classUnderTest = new CronJobForTest("*/15", "8-18/*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-31 18:45:00"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-31 18:45:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-11-01 08:00:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testNextDayOfWeek() throws ParseException {
      this.classUnderTest = new CronJobForTest("*/15", "8-18/*", "*", "*", "1-5/*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-23 18:45:00"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-23 18:45:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-26 08:00:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testNextHour() throws ParseException {
      this.classUnderTest = new CronJobForTest("*/15", "*", "*", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-27 10:45:00"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-27 10:45:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-10-27 11:00:00", formatTime(this.classUnderTest.getNextExecTime()));
   }


   @Test
   public void testNextMonth() throws ParseException {
      this.classUnderTest = new CronJobForTest("*/15", "8-18/*", "2-12/2", "*", "*", "testTask");
      this.classUnderTest.setCurrentTimeForCalculation(getTime("2020-10-31 18:45:00"));
      this.classUnderTest.setNextExecTime(getTime("2020-10-31 18:45:00").getTime());

      assertEquals(Boolean.TRUE, Boolean.valueOf(this.classUnderTest.shouldExecute()));
      assertEquals("2020-12-01 08:00:00", formatTime(this.classUnderTest.getNextExecTime()));
   }
}
