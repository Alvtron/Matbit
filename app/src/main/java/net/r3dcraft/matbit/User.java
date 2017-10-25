package net.r3dcraft.matbit;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by unibl on 21.10.2017.
 */

public class User {
    private String id;
    private UserData data;

    public User() {
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