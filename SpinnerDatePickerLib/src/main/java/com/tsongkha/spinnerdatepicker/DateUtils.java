package com.tsongkha.spinnerdatepicker;/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
/**
 * Utility methods for processing dates.
 */
public class DateUtils {
    public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    /**
     * When parsing a date without a year, the system assumes 1970, which wasn't a leap-year.
     * Let's add a one-off hack for that day of the year
     */
    public static final String NO_YEAR_DATE_FEB29TH = "--02-29";
    // Variations of ISO 8601 date format.  Do not change the order - it does affect the
    // result in ambiguous cases.
    private static final SimpleDateFormat[] DATE_FORMATS = {
            CommonDateUtils.FULL_DATE_FORMAT,
            CommonDateUtils.DATE_AND_TIME_FORMAT,
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US),
            new SimpleDateFormat("yyyyMMdd", Locale.US),
            new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS'Z'", Locale.US),
            new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US),
            new SimpleDateFormat("yyyyMMdd'T'HHmm'Z'", Locale.US),
    };
    static {
        for (SimpleDateFormat format : DATE_FORMATS) {
            format.setLenient(true);
            format.setTimeZone(UTC_TIMEZONE);
        }
        CommonDateUtils.NO_YEAR_DATE_FORMAT.setTimeZone(UTC_TIMEZONE);
    }
    /**
     * Parses the supplied string to see if it looks like a date.
     *
     * @param string The string representation of the provided date
     * @param mustContainYear If true, the string is parsed as a date containing a year. If false,
     * the string is parsed into a valid date even if the year field is missing.
     * @return A Calendar object corresponding to the date if the string is successfully parsed.
     * If not, null is returned.
     */
    public static Calendar parseDate(String string, boolean mustContainYear) {
        ParsePosition parsePosition = new ParsePosition(0);
        Date date;
        if (!mustContainYear) {
            final boolean noYearParsed;
            // Unfortunately, we can't parse Feb 29th correctly, so let's handle this day seperately
            if (NO_YEAR_DATE_FEB29TH.equals(string)) {
                return getUtcDate(0, Calendar.FEBRUARY, 29);
            } else {
                synchronized (CommonDateUtils.NO_YEAR_DATE_FORMAT) {
                    date = CommonDateUtils.NO_YEAR_DATE_FORMAT.parse(string, parsePosition);
                }
                noYearParsed = parsePosition.getIndex() == string.length();
            }
            if (noYearParsed) {
                return getUtcDate(date, true);
            }
        }
        for (int i = 0; i < DATE_FORMATS.length; i++) {
            SimpleDateFormat f = DATE_FORMATS[i];
            synchronized (f) {
                parsePosition.setIndex(0);
                date = f.parse(string, parsePosition);
                if (parsePosition.getIndex() == string.length()) {
                    return getUtcDate(date, false);
                }
            }
        }
        return null;
    }
    private static final Calendar getUtcDate(Date date, boolean noYear) {
        final Calendar calendar = Calendar.getInstance(UTC_TIMEZONE, Locale.US);
        calendar.setTime(date);
        if (noYear) {
            calendar.set(Calendar.YEAR, 0);
        }
        return calendar;
    }
    private static final Calendar getUtcDate(int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance(UTC_TIMEZONE, Locale.US);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calendar;
    }
    public static boolean isYearSet(Calendar cal) {
        // use the Calendar.YEAR field to track whether or not the year is set instead of
        // Calendar.isSet() because doing Calendar.get() causes Calendar.isSet() to become
        // true irregardless of what the previous value was
        return cal.get(Calendar.YEAR) > 1;
    }
    /**
     * Same as {@link #formatDate(Context context, String string, boolean longForm)}, with
     * longForm set to {@code true} by default.
     *
     * @param context Valid context
     * @param string String representation of a date to parse
     * @return Returns the same date in a cleaned up format. If the supplied string does not look
     * like a date, return it unchanged.
     */
    public static String formatDate(Context context, String string) {
        return formatDate(context, string, true);
    }
    /**
     * Parses the supplied string to see if it looks like a date.
     *
     * @param context Valid context
     * @param string String representation of a date to parse
     * @param longForm If true, return the date formatted into its long string representation.
     * If false, return the date formatted using its short form representation (i.e. 12/11/2012)
     * @return Returns the same date in a cleaned up format. If the supplied string does not look
     * like a date, return it unchanged.
     */
    public static String formatDate(Context context, String string, boolean longForm) {
        if (string == null) {
            return null;
        }
        string = string.trim();
        if (string.length() == 0) {
            return string;
        }
        final Calendar cal = parseDate(string, false);
        // we weren't able to parse the string successfully so just return it unchanged
        if (cal == null) {
            return string;
        }
        final boolean isYearSet = isYearSet(cal);
        final java.text.DateFormat outFormat;
        if (!isYearSet) {
            outFormat = getLocalizedDateFormatWithoutYear(context);
        } else {
            outFormat =
                    longForm ? DateFormat.getLongDateFormat(context) :
                            DateFormat.getDateFormat(context);
        }
        synchronized (outFormat) {
            outFormat.setTimeZone(UTC_TIMEZONE);
            return outFormat.format(cal.getTime());
        }
    }
    public static boolean isMonthBeforeDay(Context context) {
        char[] dateFormatOrder = DateFormat.getDateFormatOrder(context);
        for (int i = 0; i < dateFormatOrder.length; i++) {
            if (dateFormatOrder[i] == 'd') {
                return false;
            }
            if (dateFormatOrder[i] == 'M') {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns a SimpleDateFormat object without the year fields by using a regular expression
     * to eliminate the year in the string pattern. In the rare occurence that the resulting
     * pattern cannot be reconverted into a SimpleDateFormat, it uses the provided context to
     * determine whether the month field should be displayed before the day field, and returns
     * either "MMMM dd" or "dd MMMM" converted into a SimpleDateFormat.
     */
    public static java.text.DateFormat getLocalizedDateFormatWithoutYear(Context context) {
        final String pattern = ((SimpleDateFormat) SimpleDateFormat.getDateInstance(
                java.text.DateFormat.LONG)).toPattern();
        // Determine the correct regex pattern for year.
        // Special case handling for Spanish locale by checking for "de"
        final String yearPattern = pattern.contains(
                "de") ? "[^Mm]*[Yy]+[^Mm]*" : "[^DdMm]*[Yy]+[^DdMm]*";
        try {
            // Eliminate the substring in pattern that matches the format for that of year
            return new SimpleDateFormat(pattern.replaceAll(yearPattern, ""));
        } catch (IllegalArgumentException e) {
            return new SimpleDateFormat(
                    DateUtils.isMonthBeforeDay(context) ? "MMMM dd" : "dd MMMM");
        }
    }
    /**
     * Given a calendar (possibly containing only a day of the year), returns the earliest possible
     * anniversary of the date that is equal to or after the current point in time if the date
     * does not contain a year, or the date converted to the local time zone (if the date contains
     * a year.
     *
     * @param target The date we wish to convert(in the UTC time zone).
     * @return If date does not contain a year (year < 1900), returns the next earliest anniversary
     * that is after the current point in time (in the local time zone). Otherwise, returns the
     * adjusted Date in the local time zone.
     */
    public static Date getNextAnnualDate(Calendar target) {
        final Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        // Round the current time to the exact start of today so that when we compare
        // today against the target date, both dates are set to exactly 0000H.
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        final boolean isYearSet = isYearSet(target);
        final int targetYear = target.get(Calendar.YEAR);
        final int targetMonth = target.get(Calendar.MONTH);
        final int targetDay = target.get(Calendar.DAY_OF_MONTH);
        final boolean isFeb29 = (targetMonth == Calendar.FEBRUARY && targetDay == 29);
        final GregorianCalendar anniversary = new GregorianCalendar();
        // Convert from the UTC date to the local date. Set the year to today's year if the
        // there is no provided year (targetYear < 1900)
        anniversary.set(!isYearSet ? today.get(Calendar.YEAR) : targetYear,
                targetMonth, targetDay);
        // If the anniversary's date is before the start of today and there is no year set,
        // increment the year by 1 so that the returned date is always equal to or greater than
        // today. If the day is a leap year, keep going until we get the next leap year anniversary
        // Otherwise if there is already a year set, simply return the exact date.
        if (!isYearSet) {
            int anniversaryYear = today.get(Calendar.YEAR);
            if (anniversary.before(today) ||
                    (isFeb29 && !anniversary.isLeapYear(anniversaryYear))) {
                // If the target date is not Feb 29, then set the anniversary to the next year.
                // Otherwise, keep going until we find the next leap year (this is not guaranteed
                // to be in 4 years time).
                do {
                    anniversaryYear +=1;
                } while (isFeb29 && !anniversary.isLeapYear(anniversaryYear));
                anniversary.set(anniversaryYear, targetMonth, targetDay);
            }
        }
        return anniversary.getTime();
    }
    /**
     * Determine the difference, in days between two dates.  Uses similar logic as the
     * {android.text.format.DateUtils.getRelativeTimeSpanString} method.
     *
     * @param time Instance of time object to use for calculations.
     * @param date1 First date to check.
     * @param date2 Second date to check.
     * @return The absolute difference in days between the two dates.
     */
    public static int getDayDifference(Time time, long date1, long date2) {
        time.set(date1);
        int startDay = Time.getJulianDay(date1, time.gmtoff);
        time.set(date2);
        int currentDay = Time.getJulianDay(date2, time.gmtoff);
        return Math.abs(currentDay - startDay);
    }
}