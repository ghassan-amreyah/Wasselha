package com.cs.wasselha.model;

import java.lang.String;

public class DeliveryServiceDetails {

    private  int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int service;
    private int customer;
    private int source_collection_point;
    private int destination_collection_point;
    private boolean is_paid;
    private String collection_time;
    private String source_place;
    private String destination_place;
    private double price;
    private boolean accepted;
    private boolean responsed;

    public String getSource_place() {
        return source_place;
    }

    public void setSource_place(String source_place) {
        this.source_place = source_place;
    }

    public String getDestination_place() {
        return destination_place;
    }

    public void setDestination_place(String destination_place) {
        this.destination_place = destination_place;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isResponsed() {
        return responsed;
    }

    public void setResponsed(boolean responsed) {
        this.responsed = responsed;
    }

    private String handover_time;
   // private String status;

    public DeliveryServiceDetails() {


    }

    public DeliveryServiceDetails(int service, int customer, int source_collection_point,
                                  int destination_collection_point, boolean is_paid,
                                  String collection_time, String handover_time) {
        this.service = service;
        this.customer = customer;
        this.source_collection_point = source_collection_point;
        this.destination_collection_point = destination_collection_point;
        this.is_paid = is_paid;
        this.collection_time = collection_time;
        this.handover_time = handover_time;
        //this.status = status;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public int getSource_collection_point() {
        return source_collection_point;
    }

    public void setSource_collection_point(int source_collection_point) {
        this.source_collection_point = source_collection_point;
    }

    public int getDestination_collection_point() {
        return destination_collection_point;
    }

    public void setDestination_collection_point(int destination_collection_point) {
        this.destination_collection_point = destination_collection_point;
    }

    public boolean isIs_paid() {
        return is_paid;
    }

    public void setIs_paid(boolean is_paid) {
        this.is_paid = is_paid;
    }

    public String getCollection_time() {
        return collection_time;
    }

    public void setCollection_time(String collection_time) {
        this.collection_time = collection_time;
    }

    public String getHandover_time() {
        return handover_time;
    }

    public void setHandover_time(String handover_time) {
        this.handover_time = handover_time;
    }

   // public String getStatus() {
       // return status;
    //}

   // public void setStatus(String status) {
     //   this.status = status;
   // }
}
