package com.cs.wasselha.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Service {


   // @Expose(serialize = false)
    private   int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int transporter; // transporter Id
    private int source_place;
    private int destination_place;
    private Date service_date;
    private double price;

    public int getTransporter_location() {
        return transporter_location;
    }

    public void setTransporter_location(int transporter_location) {
        this.transporter_location = transporter_location;
    }

    private int transporter_location; // current transporter location

    public List<Service> getServices() {
        return services;
    }

    //@Expose(serialize = false)
    transient List<Service> services = new ArrayList<>();

    /*public Service() throws IOException {

        Faker faker = new Faker();



        ArrayList<Transporter> transportersList = new TransporterDA().getTransporters(); // todo: i have to get them from the real database
        List<Location> locationList = new LocationDA().getLocations();

        int i=0;

        while (i<30) {

            //int id = faker.number().numberBetween(1,1000000);

            double price = faker.number().numberBetween(10,500);

            Transporter serviceProvider= transportersList.get(new Random().nextInt(transportersList.size()));
            int src = locationList.get(new Random().nextInt(locationList.size()));
            Location dest = locationList.get(new Random().nextInt(locationList.size()));
            Service service  = new Service(id,serviceProvider,src,dest,
                    faker.date().between(new Date(),new Date(2023,7,18)),price);


            services.add(service);

            i++;

        }

        Log.d("services",services.toString());
    }*/

    public Service(int id, int transporter, int source_place, int destination_place, Date service_date, double price,int transporter_location) {
        this.id = id;
        this.transporter = transporter;
        this.source_place = source_place;
        this.destination_place = destination_place;
        this.service_date = service_date;
        this.price = price;
        this.transporter_location = transporter_location;
    }


    public int getTransporter() {
        return transporter;
    }

    public void setTransporter(int transporter) {
        this.transporter = transporter;
    }

    public int getSource_place() {
        return source_place;
    }

    public void setSource_place(int source_place) {
        this.source_place = source_place;
    }

    public int getDestination_place() {
        return destination_place;
    }

    public void setDestination_place(int destination_place) {
        this.destination_place = destination_place;
    }

    public Date getService_date() {
        return service_date;
    }

    public void setService_date(Date service_date) {
        this.service_date = service_date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
