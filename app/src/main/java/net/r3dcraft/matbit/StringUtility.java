package net.r3dcraft.matbit;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 31.10.2017.
 */

public final class StringUtility {
    private static final String TAG = "StringUtility";

    public static Integer stringToInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            Log.e(TAG, "stringToInt: Could not convert '" + text);
            return null;
        }
    }

    public static Float stringToFloat(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            Log.e(TAG, "stringToFloat: Could not convert '" + text);
            return null;
        }
    }

    public static Double stringToDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            Log.e(TAG, "stringToDouble: Could not convert '" + text);
            return null;
        }
    }

    public static boolean stringIsNotEmpty(String string) {
        return (string != null && !string.equals(""));
    }

    public static boolean search(String key, String source) {
        key.toLowerCase(); source.toLowerCase();

        if (key.trim().length() < 4 || key == null || key.trim().equals(""))
            return true;

        for (int i = 0; i < key.length() - 3; i++){
           if (source.contains(key.substring(i, i + 4)))
               return true;
        }
        return false;
    }

    public static String shortNumber(int number) {
        if (number >= 1000000)
            return String.valueOf(Math.round(((double)number / 1000000.00) * 100) / 100) + "m";
        else if (number >= 1000)
            return String.valueOf(Math.round(((double)number / 1000.00) * 100) / 100) + "k";
        else
            return String.valueOf(number);
    }
}
