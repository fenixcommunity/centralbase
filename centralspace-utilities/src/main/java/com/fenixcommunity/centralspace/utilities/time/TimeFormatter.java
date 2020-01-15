package com.fenixcommunity.centralspace.utilities.time;

import lombok.experimental.FieldDefaults;

import java.time.format.DateTimeFormatter;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TimeFormatter {
    //    DATE_TIME
    public static final DateTimeFormatter DT_FORMATTER_1 = DateTimeFormatter.ISO_DATE_TIME;
    public static final DateTimeFormatter DT_FORMATTER_2 = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss");

    //    ZONE_DATE_TIME
    public static final DateTimeFormatter ZONE_FORMATTER_1 = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    public static final DateTimeFormatter ZONE_FORMATTER_2 = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss z");

}