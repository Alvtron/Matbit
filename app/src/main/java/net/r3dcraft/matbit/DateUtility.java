package net.r3dcraft.matbit;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import java.util.Locale;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 19.10.2017.
 *
 * Dates are important in this app, and there are so many ways to store time. I made this utility to
 * generalize the way I store time. The time format used is yyyy-MM-dd HH:mm:ss.
 */

public final class DateUtility {
    // Date format
    private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.US);

    // A formatter that displays a period of time
    private static final PeriodFormatter PERIOD_FORMAT = new PeriodFormatterBuilder()
            // Format days
            .appendDays()
            .appendSuffix(
                    " " + MatbitApplication.resources().getString(R.string.string_day),
                    " " + MatbitApplication.resources().getString(R.string.string_days))

            .appendSeparator(" " + MatbitApplication.resources().getString(R.string.string_and) + " ")
            // Format hours
            .appendHours()
            .appendSuffix(
                    " " + MatbitApplication.resources().getString(R.string.string_hour),
                    " " + MatbitApplication.resources().getString(R.string.string_hours))

            .appendSeparator(" " + MatbitApplication.resources().getString(R.string.string_and) + " ")

            // Format minutes
            .appendMinutes()
            .appendSuffix(
                    " " + MatbitApplication.resources().getString(R.string.string_minute),
                    " " + MatbitApplication.resources().getString(R.string.string_minutes))

            .appendSeparator(" " + MatbitApplication.resources().getString(R.string.string_and) + " ")

            // Format seconds
            .appendSeconds()
            .appendSuffix(
                    " " + MatbitApplication.resources().getString(R.string.string_second)
                    , " " + MatbitApplication.resources().getString(R.string.string_seconds))

            .toFormatter();

    /**
     * @return Current date and time as DateTime.
     */
    public static DateTime nowDate() {
        return new DateTime();
    }

    /**
     * @return Current date and time as String, formatted.
     */
    public static String nowString() {
        try {
            return FORMAT.print(new DateTime());
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    /**
     * Convert DateTime object to String, formatted.
     *
     * @param date - A DateTime object
     * @return Converted date as String
     */
    public static String dateToString(DateTime date) {
        try {
            return FORMAT.print(date);
        }
        catch (Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    /**
     * Convert String to DateTime object, formatted.
     * @param dateString - A date string that is formatted
     * @return A DateTime converted from the provided dateString
     */
    public static DateTime stringToDate(String dateString) {
        try {
            return FORMAT.parseDateTime(dateString);
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    /**
     * This returns a formatted string for the period between the provided fromDate and the current
     * date.
     *
     * @param fromDate - Provided DateTime object
     * @return String with formatted period
     */
    public static String dateToPeriod(DateTime fromDate) {
        DateTime nowDate = new DateTime();
        int seconds = Seconds.secondsBetween(fromDate, nowDate).getSeconds();
        Period period = new Period(seconds);
        return PERIOD_FORMAT.print(period.normalizedStandard());
    }

    /**
     * This returns a formatted string for the period between the provided fromDate and the toDate.
     *
     * @param fromDate - Provided DateTime object
     * @param toDate - Provided DateTime object
     * @return
     */
    public static String dateToPeriod(DateTime fromDate, DateTime toDate) {
        int seconds = Seconds.secondsBetween(toDate, fromDate).getSeconds();
        Period period = new Period(seconds);
        return PERIOD_FORMAT.print(period.normalizedStandard());
    }

}
