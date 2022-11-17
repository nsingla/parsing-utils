package io.nsingla.formatters;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateTimeFormattingUtils {
    public static final DateTimeFormatter dtf_yyyyMMdd_slash = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter dtf_Mdyy_slash = DateTimeFormatter.ofPattern("M/d/yy");
    public static final DateTimeFormatter dtf_Mdyyyy_slash = DateTimeFormatter.ofPattern("M/d/yyyy");
    public static final DateTimeFormatter dtf_MMddyyyy_slash = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final DateTimeFormatter dtf_EMMMddyyyy = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
    public static final DateTimeFormatter dtf_Eddyyyy = DateTimeFormatter.ofPattern("MMMM, dd yyyy");
    public static final DateTimeFormatter dtf_yyyyMMddTHHmm = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public static final DateTimeFormatter dtf_isoDateTime = DateTimeFormatter.ISO_DATE_TIME;
    public static final DateTimeFormatter dtf_yyyyMMddTHHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateTimeFormatter dtf_yyyyMMddTHHmmssSSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter dtf_yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter dateTimeFormatters = new DateTimeFormatterBuilder()
        .appendOptional(dtf_yyyyMMddHHmmss)
        .appendOptional(dtf_yyyyMMddTHHmmss)
        .appendOptional(dtf_yyyyMMddTHHmmssSSS)
        .appendOptional(dtf_yyyyMMddTHHmm)
        .toFormatter();

    public static final DateTimeFormatter dateFormatters = new DateTimeFormatterBuilder()
        .appendOptional(dtf_yyyyMMddHHmmss)
        .appendOptional(dtf_yyyyMMdd_slash)
        .appendOptional(dtf_Mdyyyy_slash)
        .appendOptional(dtf_MMddyyyy_slash)
        .appendOptional(dtf_Mdyy_slash)
        .appendOptional(dtf_EMMMddyyyy)
        .appendOptional(dtf_Eddyyyy)
        .toFormatter();

}
