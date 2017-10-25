package net.r3dcraft.matbit;

/**
 * Created by unibl on 19.10.2017.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTime {
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
}
