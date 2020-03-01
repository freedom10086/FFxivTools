package com.xdluoyang.ffxivtools.util;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ET一天是70分钟
 * ET一小时是175秒
 * ET一分钟是(2+11/12)s
 * 现实(2+11/12)s=艾欧泽亚1min
 */
public class TimeUtil {

    public static final double EORZEA_TIME_CONSTANT = 3600.0 / 175;

    public static final double EORZEA_TIME_MINUTE_MILLS = (2 + 11.0 / 12) * 1000;

    private static final int YEAR = 33177600;

    private static final int MONTH = 2764800;

    private static final int DAY = 86400;

    private static final int HOUR = 3600;

    private static final int MINUTE = 60;

    private static final int SECOND = 1;

    public static class EorezeaTime {

        private int year;

        private int month;

        private int day;

        private int hour;

        private int minute;

        private int second;

        public EorezeaTime(int year, int month, int day, int hour, int minute, int second) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public String getSimpleTimeString() {
            return String.format("%02d:%02d", hour, minute);
        }

        public String getDateTimeString() {
            return String.format("%d-%02d-%02d %2d:%02d:%02d", year, month, day, hour, minute, second);
        }

        public String getTimeString() {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }

    public static EorezeaTime toEorezeaTime(long currentMills) {
        long eorezeaMills = (long) Math.floor(currentMills * (60 / (2 + 11.0 / 12)));
        long eorezeaSecond = Math.floorDiv(eorezeaMills, 1000);

        int year = (int) (Math.floorDiv(eorezeaSecond, YEAR) + 1);
        int month = (int) (Math.floorDiv(eorezeaSecond, MONTH) % 12 + 1);
        int day = (int) (Math.floorDiv(eorezeaSecond, DAY) % 32 + 1);
        int hour = (int) (Math.floorDiv(eorezeaSecond, HOUR) % 24);
        int minute = (int) (Math.floorDiv(eorezeaSecond, MINUTE) % 60);
        int second = (int) (Math.floorDiv(eorezeaSecond, SECOND) % 60);

        return new EorezeaTime(year, month, day, hour, minute, second);
    }
}
