package com.cs.wasselha.model;

import java.time.LocalDateTime;

public class DeliveryStatus {

    private int id;
    private int delivery_service_details;
    private String action_time;
    private String collection_from;
    private String handover_to;

    public DeliveryStatus() {
    }

    public DeliveryStatus(int delivery_service_details, String actionTime, String collectionFrom, String handoverTo) {
        this.delivery_service_details = delivery_service_details;
        this.action_time = actionTime;
        this.collection_from = collectionFrom;
        this.handover_to = handoverTo;
    }

    public int getDeliveryServiceDetails() {
        return delivery_service_details;
    }

    public void setDeliveryServiceDetails(int delivery_service_details) {
        this.delivery_service_details = delivery_service_details;
    }

    public String getActionTime() {
        return action_time;
    }

    public void setActionTime(String actionTime) {
        this.action_time = actionTime;
    }

    public String getCollectionFrom() {
        return collection_from;
    }

    public void setCollectionFrom(String collectionFrom) {
        this.collection_from = collectionFrom;
    }

    public String getHandoverTo() {
        return handover_to;
    }

    public void setHandoverTo(String handoverTo) {
        this.handover_to = handoverTo;
    }
}
