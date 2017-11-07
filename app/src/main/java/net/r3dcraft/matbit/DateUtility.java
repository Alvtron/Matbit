package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 19.10.2017.
 */

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtility {
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public static Date nowDate() {
        return new Date();
    }

    public static String nowString() {
        try {
            return FORMAT.format(new Date());
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static String dateToString(Date date) {
        try {
            return FORMAT.format(date);
        }
        catch (Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public static Date stringToDate(String dateString) {
        try {
            return FORMAT.parse(dateString);
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static String dateToTimeText(Date date) {
        DateTime sourceDate = new DateTime(date);
        DateTime nowDate = new DateTime(new Date());
        int seconds = Seconds.secondsBetween(sourceDate, nowDate).getSeconds();
        Period period = new Period(seconds);
        return PERIOD_FORMAT.print(period.normalizedStandard());
    }
}
