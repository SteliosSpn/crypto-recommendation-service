package com.xm.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@UtilityClass
public class TimeUtil {

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
}
