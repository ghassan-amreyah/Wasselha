package com.cs.wasselha.model;



public class Vehicle {

    private int id; // for the database
    private int transporter;
    private String vehicle_image;
    private String vehicle_type;
    private String vehicle_number;
    private String vehicle_license;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public Vehicle(){

        Faker faker = new Faker();
        List<Vehicle> vehicles = new ArrayList<>();

        List<String> vehicleTypes = new ArrayList<>();
        vehicleTypes.add("car");
        vehicleTypes.add("van");
        vehicleTypes.add("truck");

        List<Transporter> transportersList = new Transporter().transporters; // todo: i have to get them from the real database

        int i=0;

        while (i<30) {

            int id = faker.number().numberBetween(1,1000000);
            String vehicleTypeCurr = vehicleTypes.get(new Random().nextInt(3));
            Transporter transporterOwner= transportersList.get(new Random().nextInt(transportersList.size()));
            Vehicle vehicle  = new Vehicle(id,transporterOwner,null,vehicleTypeCurr,null);

            vehicles.add(vehicle);

            i++;

        }

        Log.d("vehicles",vehicles.toString());
    }*/
    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }



    public Vehicle(int id, int transporter, String vehicle_image, String vehicle_type, String vehicle_license) {
        this.id = id;
        this.transporter = transporter;
        this.vehicle_image = vehicle_image;
        this.vehicle_type = vehicle_type;
        this.vehicle_license = vehicle_license;
    }

    public int getTransporter() {
        return transporter;
    }

    public void setTransporter(int transporter) {
        this.transporter = transporter;
    }

    public String getVehicle_image() {
        return vehicle_image;
    }

    public void setVehicle_image(String vehicle_image) {
        this.vehicle_image = vehicle_image;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_license() {
        return vehicle_license;
    }

    public void setVehicle_license(String vehicle_license) {
        this.vehicle_license = vehicle_license;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", transporter=" + transporter +
                ", vehicle_image=" + vehicle_image +
                ", vehicle_type='" + vehicle_type + '\'' +
                ", vehicle_number='" + vehicle_number + '\'' +
                ", vehicle_license=" + vehicle_license +
                '}';
    }
}
