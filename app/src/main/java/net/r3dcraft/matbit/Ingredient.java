package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 *
 * The Ingredient class is a data structure class that represents a ingredient block in the Firebase
 * Database. Any changes made here will have an impact on the data structure in the database.
 *
 * Since Google Firebase uses clever ClassWrapping, this class can be use directly with both writing
 * and storing ingredient-data from the database.
 */

public class Ingredient {
    private String course;
    private String name;
    private double amount;
    private String measurement;

    /**
     * Ingredient default constructor
     */
    public Ingredient() {
        this.course = new String();
        this.name = new String();
        this.amount = -1.0;
        this.measurement = new String();
    }

    /**
     * Ingredient constructor.
     * @param course - Which course (ex. main, sauce, optional, etc...)
     * @param name - Name of ingredient
     * @param amount - The amount of [measurement units]
     * @param measurement - Measurement (ex. dl, ltr, g, kg, etc...)
     */
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
