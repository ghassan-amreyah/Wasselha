package com.cs.wasselha.model;


import com.github.javafaker.Faker;

import java.util.List;

public class Customer {
    private int id; // for the database
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String first_name;
    private String last_name;
    private String password;
    private String phone_number;
    private boolean is_verified;
    private int location; // location id
    private int review;
    //private String documentId; // this is for the firebase database

    //List<Customer> customers = new ArrayList<>();
    /*public Customer(List<Customer> customers) {
        Faker faker = new Faker();

        int i = 0;
        while (i < 30) {
           // int id = faker.number().numberBetween(1, 1000000);
            String email = faker.internet().emailAddress();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String phoneNumber = faker.phoneNumber().phoneNumber();
            boolean isVerified = true;
            String password = faker.internet().password();
            int review = 0;
            int location = 1;
            Customer customer = new Customer( email,
                    firstName, lastName, phoneNumber,
                    isVerified, password, review,location);
            customers.add(customer);

            i++;

        }


        //Log.d("customers",customers.toString());
    }
*/
    public Customer(String email, String firstName, String last_name, String phone_number,
                    boolean is_verified, String password, int review, int location) {
        //this.id = id;
        this.email = email;
        this.first_name = firstName;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.is_verified = is_verified;
        this.password = password;
        this.review = review;
        this.location = location;

    }

    public Customer(String email, String firstName, String last_name, String phone_number,
                    boolean is_verified, int location, int review) {
        this.email = email;
        this.first_name = firstName;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.is_verified = is_verified;
        this.location = location;
        this.review = review;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public void setLocation(int location) {
        this.location = location;
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

    public String getPhone_number() {
        return phone_number;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public int getLocation() {
        return location;
    }

    public int getReview() {
        return review;
    }

   /* @Override
    public String toString() {
        return first_name + " " + last_name + " (" + email + ")";
    }*/

    @Override
    public String toString() {
        return "Customer{" +
                "email='" + email + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", is_verified=" + is_verified +
                ", location=" + location +
                ", review=" + review +
                '}';
    }


}