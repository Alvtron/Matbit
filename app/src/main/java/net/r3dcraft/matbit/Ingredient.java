package net.r3dcraft.matbit;

/**
 * Created by unibl on 21.10.2017.
 */

public class Ingredient {
    private String course;
    private String name;
    private double amount;
    private String measurement;

    public Ingredient() {
        this.course = new String();
        this.name = new String();
        this.amount = -1.0;
        this.measurement = new String();
    }

    public Ingredient(String course, String name, double amount, String measurement) {
        this.course = course;
        this.name = name;
        this.amount = amount;
        this.measurement = measurement;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }


}
