package com.cs.wasselha.model;

import java.util.Date;

public class Claim {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int delivery_service_details;

    private int writer_id;
    private int written_to_id;
    private String writer_type;
    private String written_to_type;
    private String message;

    private int review;
    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public int getWritten_to_id() {
        return written_to_id;
    }

    public Claim(int id,int delivery_service_details, int writer_id, int written_to_id, String writer_type, String written_to_type, String message,
                 int review, String date) {
        this.id = id;
        this.delivery_service_details = delivery_service_details;
        this.writer_id = writer_id;
        this.written_to_id = written_to_id;
        this.writer_type = writer_type;
        this.written_to_type = written_to_type;
        this.message = message;
        this.date = date;
        this.review = review;
    }

    public void setWritten_to_id(int written_to_id) {
        this.written_to_id = written_to_id;
    }

    public String getWritten_to_type() {
        return written_to_type;
    }

    public void setWritten_to_type(String written_to_type) {
        this.written_to_type = written_to_type;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

   // private String date;


    public Claim() {
    }



    public int getDelivery_service_details() {
        return delivery_service_details;
    }

    public void setDelivery_service_details(int delivery_service_details) {
        this.delivery_service_details = delivery_service_details;
    }

    public int getWriter_id() {
        return writer_id;
    }

    public void setWriter_id(int writer_id) {
        this.writer_id = writer_id;
    }

    public String getWriter_type() {
        return writer_type;
    }

    public void setWriter_type(String writer_type) {
        this.writer_type = writer_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Claim{" +


                ", From=" + writer_id +

                ", who is a='" + writer_type + '\'' +

                ", message='" + message + '\'' +
                ", review=" + review +
                ", date=" + date +
                '}';
    }
}
