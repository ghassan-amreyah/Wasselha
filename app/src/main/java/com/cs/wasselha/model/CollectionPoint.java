package com.cs.wasselha.model;

import android.util.Log;

import com.github.javafaker.Faker;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollectionPoint {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

   // private LocalTime open_time ; // open_time = LocalTime.of(15,30);
   // private LocalTime close_time; // LocalTime lTime = LocalTime.now(); field = lTime.get(ChronoField.AMPM_OF_DAY);


    int collection_point_provider; // id

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
    String status; // open, closed
    String open_time;
    String close_time;
    int location; // location id

    public CollectionPoint() {
    }

    public CollectionPoint(int collection_point_provider, String status, String open_time, String close_time, int location) {
        this.collection_point_provider = collection_point_provider;
        this.status = status;
        this.open_time = open_time;
        this.close_time = close_time;
        this.location = location;
    }

    public int getCollection_point_provider() {
        return collection_point_provider;
    }

    public void setCollection_point_provider(int collection_point_provider) {
        this.collection_point_provider = collection_point_provider;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }}