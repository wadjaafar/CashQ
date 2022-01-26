package com.tutipay.app.model;

/**
 * Created by Mohamed Jaafar on 14/02/19.
 */

public class Course {

    private int id;
    private String description;

    public Course(){

    }

    public Course(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Course){
            Course c = (Course) obj;
            return c.getDescription().equals(description) && c.getId() == id;
        }

        return false;
    }

}
