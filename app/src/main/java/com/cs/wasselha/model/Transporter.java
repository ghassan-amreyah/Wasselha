package com.cs.wasselha.model;


import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;


public class Transporter {

    //@Expose(serialize = false)
    private  int id; // for the database
    private String first_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String last_name;
    private String email;
    private String phone_number;
    private String password;
    private boolean is_verified;

    private String status;
    private String national_id;
    private String driving_license;
    private int review;

    public List<Transporter> getTransporters() {
        return transporters;
    }

    public void setTransporters(List<Transporter> transporters) {
        this.transporters = transporters;
    }

    //@Expose(serialize = false)
    transient List<Transporter> transporters = new ArrayList<>();


   /* public Transporter(){
        Faker faker = new Faker();
       // List<Transporter> transporters = new ArrayList<>();
        int i=0;
        while (i<30) {
            //int id = faker.number().numberBetween(1,1000000);
            String email = faker.internet().emailAddress();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String phoneNumber = "4"+i;//faker.phoneNumber().phoneNumber();
            String password = faker.internet().password();
            String nationalId = faker.idNumber().ssnValid();
            String drivingLicense =  new String("C:\\Users\\hp\\AndroidStudioProjects\\Wasselha2\\download.jpeg");
            String status = "Available";
            int review = 2;
            Transporter transporter  = new Transporter(
                    firstName,lastName,email,phoneNumber,password,nationalId,drivingLicense
            ,status,review);
            transporters.add(transporter);

            i++;

        }

       // Log.d("transporters",transporters.toString());
    }*/
    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }



    public Transporter(String first_name, String last_name, String email, String phone_number,
                       String password, String national_id,
                       String driving_license,String status, int review) {
      /*  this.id = id;*/
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = password;
        this.national_id = national_id;
        this.driving_license = driving_license;
        this.status = status;
        this.review = review;

    }

    public Transporter(String first_name, String last_name, String email, String phone_number,
                       String password, boolean is_verified, String status, String national_id, String driving_license) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = password;
        this.is_verified = is_verified;
        this.status = status;
        this.national_id = national_id;
        this.driving_license = driving_license;
    }

    public Transporter(String first_name, String last_name, String email, String phone_number,
                       String password, String status, String national_id, String driving_license) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = password;
        this.status = status;
        this.national_id = national_id;
        this.driving_license = driving_license;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public String getDriving_license() {
        return driving_license;
    }

    public void setDriving_license(String driving_license) {
        this.driving_license = driving_license;
    }

    @Override
    public String toString() {
        return "Transporter{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", password='" + password + '\'' +
                ", isIs_verified=" + is_verified +
                ", status='" + status + '\'' +
                ", national_id='" + national_id + '\'' +
                ", driving_license=" + driving_license +
                ", review=" + review +
                '}';
    }
}
