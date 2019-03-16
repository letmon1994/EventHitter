package com.example.ryanletmon.eventhitter.Model;

public class AdminOrders {
    private String address, date, name, payPal, phone, time, totalAmount;

    // constructor with no parameters
    public AdminOrders() {
    }

    // constructor with parameters
    public AdminOrders(String address, String date, String name, String payPal, String phone, String time, String totalAmount) {
        this.address = address;
        this.date = date;
        this.name = name;
        this.payPal = payPal;
        this.phone = phone;
        this.time = time;
        this.totalAmount = totalAmount;
    }

    // getters and setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

