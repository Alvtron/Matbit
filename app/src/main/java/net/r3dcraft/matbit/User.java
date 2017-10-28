package net.r3dcraft.matbit;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 */

public class User {
    private String id;
    private UserData data;
    private boolean synced = false;

    public User() {
    }

    public User(final DataSnapshot DATA_SNAPSHOT) {
        downloadData(DATA_SNAPSHOT);
    }

    public boolean downloadData(final DataSnapshot DATA_SNAPSHOT) {;
        this.id = DATA_SNAPSHOT.getKey();
        this.data = DATA_SNAPSHOT.getValue(UserData.class);
        return synced = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public void uploadAll() {
        MatbitDatabase.USERS.child(id).setValue(data);
    }
}