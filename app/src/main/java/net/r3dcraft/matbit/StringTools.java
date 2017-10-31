package net.r3dcraft.matbit;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 31.10.2017.
 */

public final class StringTools {
    private static final String TAG = "StringTools";

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
}