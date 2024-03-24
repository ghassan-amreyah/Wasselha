package com.cs.wasselha.Transporter;

public class Requests {
    int id;
    String customerName, customerReview, sourceCity, destinationCity, price, time;

    public Requests(int id,String customerName, String customerReview, String sourceCity, String destinationCity, String price, String time)
    {
        this.id=id;
        this.customerName = customerName;
        this.customerReview = customerReview;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.price = price;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerReview() {
        return customerReview;
    }

    public void setCustomerReview(String customerReview) {
        this.customerReview = customerReview;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Requests{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerReview='" + customerReview + '\'' +
                ", sourceCity='" + sourceCity + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", price='" + price + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
