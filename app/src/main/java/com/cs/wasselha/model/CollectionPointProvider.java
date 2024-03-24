package com.cs.wasselha.model;

import android.util.Log;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class CollectionPointProvider {
    private  int id; // for the database
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String phone_number;
    private boolean is_verified;
    private String national_id;
    private int review;

    public List<CollectionPointProvider> getCollectionPointProviders() {
        return collectionPointProviders;
    }

    transient List<CollectionPointProvider> collectionPointProviders = new ArrayList<>();
   /* public CollectionPointProvider(){
        Faker faker = new Faker();

        int i=0;
        while (i<30) {
            //int id = faker.number().numberBetween(1,1000000);
            String email = faker.internet().emailAddress();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String phoneNumber = "1"+i;//faker.phoneNumber().phoneNumber();
            boolean isVerified = true;
            String password = faker.internet().password();
            String nationalId = faker.idNumber().ssnValid();

            CollectionPointProvider collectionPointProvider  = new CollectionPointProvider(
                   id, firstName,lastName,email,password,phoneNumber,nationalId,isVerified);
            collectionPointProviders.add(collectionPointProvider);

            i++;

        }

        Log.d("collectionPointProviders",collectionPointProviders.toString());
    }*/
    public CollectionPointProvider(){}
    public CollectionPointProvider(int id, String first_name, String last_name, String email, String password,
                                   String phone_number, String national_id, boolean is_verified) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
        this.is_verified = is_verified;
        this.national_id = national_id;
        //this.review = review;
    }
    public CollectionPointProvider( String first_name, String last_name, String email, String password,
                                   String phone_number, String national_id, boolean is_verified) {
       // this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
        this.is_verified = is_verified;
        this.national_id = national_id;
        //this.review = review;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public String getNational_id() {
        return national_id;
    }

    public int getReview() {
        return review;
    }

    @Override
    public String toString() {
        return first_name + " " + last_name + " (" + email + ") (" + national_id + ")";
    }
}
