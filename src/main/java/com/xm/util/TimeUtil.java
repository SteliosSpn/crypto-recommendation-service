package com.xm.util;

import com.xm.exception.InvalidDateFormatException;
import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;

@UtilityClass
public class TimeUtil {

    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean isValidUnixTimestamp(String timestampString) {
        try {
            long timestamp = Long.parseLong(timestampString);
            if (timestamp < 0 || timestamp > Instant.now().toEpochMilli()) {
                return false;
            }
            Instant.ofEpochMilli(timestamp);
            return true;
        } catch (NumberFormatException | DateTimeParseException e) {
            return false;
        }
    }

    public static Date getDate(String requestedDate) {
        try{
            return YYYY_MM_DD.parse(requestedDate);
        } catch (ParseException e) {
            throw new InvalidDateFormatException("Invalid date format. Allowed formats are: yyyy-MM-dd");
        }
    }

    public static long getEndOfDateTimestamp(Date date) {
        return date.getTime() + 1000 * 60 * 60 * 24 - 1;
    }
}
