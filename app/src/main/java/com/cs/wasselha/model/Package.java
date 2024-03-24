package com.cs.wasselha.model;

public class Package {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int delivery_service_details;
    private String type;
    private double weight;



    public Package(int delivery_service_details, String type, double weight) {
        this.delivery_service_details = delivery_service_details;
        this.type = type;
        this.weight = weight;
    }

    public int getDelivery_service_details() {
        return delivery_service_details;
    }

    public void setDelivery_service_details(int delivery_service_details) {
        this.delivery_service_details = delivery_service_details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
