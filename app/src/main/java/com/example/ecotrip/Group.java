package com.example.ecotrip;

import java.util.ArrayList;

public class Group {
private User driver;
private ArrayList<User> passengers=new ArrayList<>();
public Group(User driver,ArrayList<User> passengers){
    this.driver=driver;
    this.passengers=passengers;
}
public Group(){

}
    public User getDriver(){

        return driver;
    }

    public ArrayList<User> getPassengers() {
        return passengers;
    }
}
