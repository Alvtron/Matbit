package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 20.10.2017.
 *
 * The Step class is a data structure class that represents a step block in the Firebase
 * Database. Any changes made here will have an impact on the data structure in the database.
 *
 * Since Google Firebase uses clever ClassWrapping, this class can be use directly with both writing
 * and storing step-data from the database.
 */

public class Step {
    private String string;
    private int seconds;

    public Step() {
        string= new String();
        seconds = 0;
    }

    public Step(String string) {
        this.string = string;
        this.seconds = 0;
    }

    public Step(String string, int seconds) {
        this.string = string;
        this.seconds = seconds;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}