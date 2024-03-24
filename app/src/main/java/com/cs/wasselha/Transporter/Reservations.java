package com.cs.wasselha.Transporter;

public class Reservations {
    int id,customerId;
    String customerName, packageType, sourceCity, destinationCity, time;
    public Reservations(int id,String customerName, String packageType, String sourceCity, String destinationCity, String time,int customerId)
    {
        this.id=id;
        this.customerName = customerName;
        this.packageType = packageType;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.time = time;
        this.customerId = customerId;
    }

    public Reservations(int id,String customerName, String packageType, String sourceCity, String destinationCity, String time)
    {
        this.id=id;
        this.customerName = customerName;
        this.packageType = packageType;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Reservations{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", packageType='" + packageType + '\'' +
                ", sourceCity='" + sourceCity + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
