package net.r3dcraft.matbit;

import java.util.HashMap;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 22.10.2017.
 */

public final class UserExamples {
    public static UserData NEW_USER_TEMPLATE () {
        return new UserData(
                "",
                "",
                "",
                DateUtility.nowString(),
                DateUtility.nowString(),
                "",
                0,
                0,
                0,
                new HashMap<String, String>(),
                new HashMap<String, String>(),
                new HashMap<String, String>(),
                new HashMap<String, String>()
        );
    }
}
