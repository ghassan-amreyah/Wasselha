package com.cs.wasselha.model;

import android.util.Log;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class Location {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //@Expose(serialize = false)
    private  int id;
    private String title;
    private String description;
    private double latitude;
    private double longitude;

    public  List<Location> getLocations() {
        return locations;
    }

    //@Expose(serialize = false)
    transient List<Location> locations = new ArrayList<>();

    public Location(){}
    /*public Location() {
        Faker faker = new Faker();
        int i = 0;
        while (i < 30) {

            double latitude = Double.parseDouble(faker.address().latitude());
            double longitude = Double.parseDouble(faker.address().longitude());

            String title = faker.address().city();
            String description = faker.address().streetName();

            Location location = new Location(title,description,latitude,longitude);

            locations.add(location);
            i++;
        }
        Log.d("locations",locations.toString());
    }*/

    /* public Location() throws FileNotFoundException {

         List<Location> locations = new ArrayList<>(110); // 110: is the number of cities in the file
         File file = new File("../../../../palestine_cities.txt"); // todo: the file can not be read!! solve it!

         if (file.exists()){
             Scanner scanner = new Scanner(file);


            // int numOfCities = Integer.parseInt(scanner.nextLine().trim());


             //int i = 0;
             while (scanner.hasNext()) {

                 String line = scanner.nextLine().trim();
                 String[] arr = line.split(" ");

                 String title = arr[0];
                 double latitude = Double.parseDouble(arr[1]);
                 double longitude = Double.parseDouble(arr[2]);

                 Location location = new Location(title, "", latitude, longitude);

                 locations.add(location);
               //  i++;

             }
             Log.d("locations",locations.toString());
             System.out.println(locations);
         }else {
             Log.d("locations","no file");
         }

     }*/
    public Location(String title, String description, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}