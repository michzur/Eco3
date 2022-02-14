package com.example.ecotrip;

import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

public class User implements Serializable {

    private String name,surname,email;
    private boolean isDriver;
    private double distance=5000,latitude,longitude;
    private User driver;
    private Car car;
    //Constructors
    public User(){
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public User getDriver() {
        return driver;
    }

    public Double getDistance() {
        return distance;
    }



    public User(String name, String surname, String email, double latitude,double longitude, boolean isDriver) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.latitude=latitude;
        this.longitude=longitude;
        this.isDriver = isDriver;
    }
    public User(String name, String surname, String email,double latitude,double longitude, boolean isDriver,Car car) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.isDriver = isDriver;
        this.latitude=latitude;
        this.longitude=longitude;
        this.car=car;
    }

    // Getters
    public String getName() {
        return name;
    }
    public boolean getIsDriver() {
        return isDriver;
    }
    public String getSurname() {
        return surname;
    }
    public String getEmail() {
        return email;
    }
    public Car getCar() {
        return car;
    }
    // Setters
    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
}
