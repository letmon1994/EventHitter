package com.example.ryanletmon.eventhitter.Model;

public class OrderList {
    private String name;
    private String address;
    private String date;
    private String payPal;
    private String phone;
    private String time;
    private String totalAmount;

    // constructor with no parameters
    public OrderList() {
    }

    // constructor with parameters
    public OrderList(String name, String address, String date, String payPal, String phone, String time, String totalAmount) {
        this.name = name;
        this.address = address;
        this.date = date;
        this.payPal = payPal;
        this.phone = phone;
        this.time = time;
        this.totalAmount = totalAmount;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayPal() {
        return payPal;
    }

    public void setPayPal(String payPal) {
        this.payPal = payPal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}