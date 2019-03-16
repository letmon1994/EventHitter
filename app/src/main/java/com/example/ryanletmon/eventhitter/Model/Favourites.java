package com.example.ryanletmon.eventhitter.Model;

public class Favourites {
    private String eid;
    private String eventName;
    private String quantity;
    private String price;
    private String discount;
    private String description;
    private String image;
    private String dateOfEvent;
    private String location;

    public Favourites() {
    }

    // constructor with parameters
    public Favourites(String eid, String eventName, String quantity, String price, String discount, String description, String image, String dateOfEvent, String location) {
        this.eid = eid;
        this.eventName = eventName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.image = image;
        this.dateOfEvent = dateOfEvent;
        this.location = location;
    }

    // getters and setters
    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(String dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

