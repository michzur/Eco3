package com.example.ecotrip;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase {
    private static final String TAG = DataBase.class.getName();
    private final FirebaseFirestore db;
    public ArrayList<User> users=new ArrayList<>();
    public DataBase() {
        db = FirebaseFirestore.getInstance();
    }


    public void sendToDB(User user){
        db.collection("users")
                .document(user.getEmail()).set(user);
    }
    public void sendToDB(Group group){
        db.collection("groups")
                .document(group.getDriver().getEmail()).set(group);
    }

    public void getGroups(FirestoreCallbackGroup firestoreCallback){
        HashMap<String,Group> groupHashMap=new HashMap<>();

        db.collection("groups")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            Group group=document.toObject(Group.class);
                            groupHashMap.put(group.getDriver().getEmail(),group);
                        }
                        firestoreCallback.onCallBack(groupHashMap);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }


    public void setClosestUsers(ArrayList<User> users){

        ArrayList<User> drivers=new ArrayList<>();
        ArrayList<User> passengers=new ArrayList<>();
        for(User user:users)
            if(user.getIsDriver())
                drivers.add(user);
                else passengers.add(user);

                for(User passenger:passengers)
                    for(User driver:drivers)
                    {
                        double distance=distance(driver.getLatLng(),passenger.getLatLng());

                        if(distance<passenger.getDistance())
                        {
                            passenger.setDistance(distance);
                            passenger.setDriver(driver);
                        }
                    }

        ArrayList<User> closestPassengers;
                for(User driver:drivers)
                {
                    closestPassengers=new ArrayList<>();
                    for (User passenger : passengers)
                    {
                        if (passenger.getDriver().getEmail().equals(driver.getEmail())
                        && closestPassengers.size()<driver.getCar().getSeats())
                            closestPassengers.add(passenger);
                    }
                    Group group=new Group(driver,closestPassengers);

                  sendToDB(group);
                }
}

    public static double distance(LatLng currentUserCords,LatLng userCords) {
        // returns distance between two locations in metres.
        double lat1=currentUserCords.latitude;
        double lon1=currentUserCords.longitude;
        double lat2= userCords.latitude;
        double lon2= userCords.longitude;
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }


    public void readUsers(FirestoreCallbackUser firestoreCallback){
        users=new ArrayList<>();
    db.collection("users")
            .get()
            .addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        User user=document.toObject(User.class);
                        users.add(user);
                    }
                    firestoreCallback.onCallBack(users);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
}
public interface FirestoreCallbackUser{
        void onCallBack(ArrayList<User> users);
}
    public interface FirestoreCallbackGroup{
        void onCallBack(HashMap<String,Group>groupHashMap);
    }
}




