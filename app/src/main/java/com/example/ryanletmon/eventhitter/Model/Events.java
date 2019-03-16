package com.example.ryanletmon.eventhitter.Model;

public class Events {
    private String title, description, image, category, date, eid, price, location, time, address, dateOfEvent, timeOfEvent;
    public double latitude;
    public double longitude;

    // constructor with no parameters
    public Events(){

    }

    // constructor with parameters
    public Events(String title, String description, String image, String category, String date, String eid, String price, String location, String time, String address, String dateOfEvent, String timeOfEvent, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.category = category;
        this.date = date;
        this.eid = eid;
        this.price = price;
        this.location = location;
        this.time = time;
        this.address = address;
        this.dateOfEvent = dateOfEvent;
        this.timeOfEvent = timeOfEvent;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(String dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public String getTimeOfEvent() {
        return timeOfEvent;
    }

    public void setTimeOfEvent(String timeOfEvent) {
        this.timeOfEvent = timeOfEvent;
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
