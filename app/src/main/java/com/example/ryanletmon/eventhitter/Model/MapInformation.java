package com.example.ryanletmon.eventhitter.Model;

public class MapInformation {
    public String title, category, date, description, eid, image, price, rating, time;
    public double latitude;
    public double longitude;

    // constructor with no parameters
    public MapInformation(){

    }

    // constructor with parameters
    public MapInformation(String title, String category, String date, String description, String eid, String image, String price, String rating, String time, double latitude, double longitude) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.description = description;
        this.eid = eid;
        this.image = image;
        this.price = price;
        this.rating = rating;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
