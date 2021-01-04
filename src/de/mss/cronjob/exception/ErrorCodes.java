package de.mss.cronjob.exception;

import de.mss.utils.exception.Error;

public class ErrorCodes {

   private static final int  ERROR_CODE_BASE      = 9000;
   public static final Error ERROR_FILE_NOT_FOUND = new Error(ERROR_CODE_BASE + 0, "Cronfile not found");

   private ErrorCodes() {}
}
