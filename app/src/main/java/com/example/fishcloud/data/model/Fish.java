package com.example.fishcloud.data.model;

import java.io.File;

public class Fish {
    private String name;
    private int length;
    private int width;
    private float weight;
    private String imgURL;
    private String location;
    private String date;
    private float latitude;
    private float longitude;

    public Fish(String name, int length, int width, float weight, String location, String date, String url) {
        this.name = name;
        this.length = length;
        this.width = width;
        this.weight = weight;
        this.location = location;
        this.date = date;
        this.imgURL = url;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
