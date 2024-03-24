package com.cs.wasselha.Transporter;

public class Services {
    int id;
    int transporterId,sourceCityId, destinationCityId;
    String date;
    String time;
    double price;

    public Services(int id, int transporterId, int sourceCityId, int destinationCityId, String date, String time, double price) {
        this.id = id;
        this.transporterId = transporterId;
        this.sourceCityId = sourceCityId;
        this.destinationCityId = destinationCityId;
        this.date = date;
        this.time = time;
        this.price = price;
    }

    public int getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(int transporterId) {
        this.transporterId = transporterId;
    }

    public int getSourceCityId() {
        return sourceCityId;
    }

    public void setSourceCityId(int sourceCityId) {
        this.sourceCityId = sourceCityId;
    }

    public int getDestinationCityId() {
        return destinationCityId;
    }

    public void setDestinationCityId(int destinationCityId) {
        this.destinationCityId = destinationCityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
