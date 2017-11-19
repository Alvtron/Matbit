package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 19.10.2017.
 */

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import java.util.Locale;

/**
 *
 */
public final class DateUtility {
    private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.US);
    private static final PeriodFormatter PERIOD_FORMAT = new PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix(" dag", " dager")
            .appendSeparator(" og ")
            .appendHours()
            .appendSuffix(" step_time", " timer")
            .appendSeparator(" og ")
            .appendMinutes()
            .appendSuffix(" minutt", " minutter")
            .appendSeparator(" og ")
            .appendSeconds()
            .appendSuffix(" sekund", " sekunder")
            .toFormatter();

    public static DateTime nowDate() {
        return new DateTime();
    }

    public static String nowString() {
        try {
            return FORMAT.print(new DateTime());
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static String dateToString(DateTime date) {
        try {
            return FORMAT.print(date);
        }
        catch (Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public static DateTime stringToDate(String dateString) {
        try {
            return FORMAT.parseDateTime(dateString);
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static String dateToTimeText(DateTime sourceDate) {
        DateTime nowDate = new DateTime();
        int seconds = Seconds.secondsBetween(sourceDate, nowDate).getSeconds();
        Period period = new Period(seconds);
        return PERIOD_FORMAT.print(period.normalizedStandard());
    }
}
