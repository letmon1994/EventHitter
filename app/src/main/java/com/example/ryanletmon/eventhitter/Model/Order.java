package com.example.ryanletmon.eventhitter.Model;

public class Order {
    private String eid;
    private String eventName;
    private String quantity;
    private String price;
    private String discount;

    // constructor with no parameters
    public Order() {
    }

    // constructor with parameters
    public Order(String eid, String eventName, String quantity, String price, String discount) {
        this.eid = eid;
        this.eventName = eventName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
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
}