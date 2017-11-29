package net.r3dcraft.matbit;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 31.10.2017.
 *
 * The string utility is just static final class which holds useful string tools.
 */

public final class StringUtility {
    private static final String TAG = "StringUtility";

    /**
     * Search algorithm for searching. Very simple made and with flaws, but at least I made it myself.
     * @param key search string
     * @param source source string
     * @return search string matches source string
     */
    public static boolean search(String key, String source) {
        key.toLowerCase(); source.toLowerCase();

        if (key.trim().length() < 4 || key == null || key.isEmpty())
            return true;

        for (int i = 0; i < key.length() - 3; i++){
           if (source.contains(key.substring(i, i + 4)))
               return true;
        }
        return false;
    }

    /**
     * Shorten numbers with letters to save space.
     * @param number number to be shortened
     * @return shortened number as string.
     */
    public static String shortNumber(int number) {
        if (number >= 1000000)
            return String.valueOf(Math.round(((double)number / 1000000.00) * 100) / 100) + "m";
        else if (number >= 1000)
            return String.valueOf(Math.round(((double)number / 1000.00) * 100) / 100) + "k";
        else
            return String.valueOf(number);
    }
}
