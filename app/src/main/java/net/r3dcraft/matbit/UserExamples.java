package net.r3dcraft.matbit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 22.10.2017.
 */

public final class UserExamples {
    public static UserData NEW_USER_TEMPLATE () {
        return new UserData(
                "",
                "",
                "",
                DateTime.nowString(),
                DateTime.nowString(),
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
