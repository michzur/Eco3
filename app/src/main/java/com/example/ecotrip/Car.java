package com.example.ecotrip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Car implements Serializable {

    int seats;
    String brand;
    double combustion;
    public Car(int seats){
        this.seats=seats;
    }
    public Car(){};
    public Car(HashMap<String,Object> map){

        this.seats=((Long)map.get("seats")).intValue();
        this.brand=(String) map.get("brand");
        this.combustion=(double) map.get("combustion");
    }


    public int getSeats() {
        return seats;
    }

    public String getBrand() {
        return brand;
    }

    public double getCombustion() {
        return combustion;
    }
}
