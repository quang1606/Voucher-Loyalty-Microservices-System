package com.example.customerservice.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimestampUtils {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static LocalDateTime convertMillisToLocalDateTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }
    
    public static String convertMillisToString(long millis) {
        return convertMillisToLocalDateTime(millis).format(DEFAULT_FORMATTER);
    }
    
    public static String convertMillisToString(long millis, DateTimeFormatter formatter) {
        return convertMillisToLocalDateTime(millis).format(formatter);
    }
    
    public static long convertLocalDateTimeToMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}