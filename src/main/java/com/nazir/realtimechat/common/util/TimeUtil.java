package com.nazir.realtimechat.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.nazir.realtimechat.common.constants.AppConstants;

public class TimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(AppConstants.TIMESTAMP_FORMAT);
    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(FORMATTER);
    }
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
