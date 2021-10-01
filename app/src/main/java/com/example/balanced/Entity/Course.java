package com.example.balanced.Entity;

public class Course {
    private String name;
    private double aditionalPrice;
    private String photoURI;
    private String id;
    private String profesionalId;
    private String description;

    public double getAditionalPrice() {
        return aditionalPrice;
    }

    public void setAditionalPrice(double aditionalPrice) {
        this.aditionalPrice = aditionalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfesionalId() {
        return profesionalId;
    }

    public void setProfesionalId(String profesionalId) {
        this.profesionalId = profesionalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
