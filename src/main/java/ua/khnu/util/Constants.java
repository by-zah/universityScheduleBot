package ua.khnu.util;

import java.time.format.DateTimeFormatter;

public final class Constants {
    public static final int TEN_MINUTES_IN_MILLIS = 600000;
    public static final String TIME_ZONE_ID = "Europe/Kiev";
    public static final int ONE_SECOND_IN_MILLIS = 1000;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Constants() {
    }
}
