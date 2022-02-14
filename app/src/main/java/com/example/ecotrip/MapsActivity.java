package com.example.ecotrip;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, Serializable {
    private GoogleMap mMap;
    private Marker dynamic; // user's input marker
    private final LatLng WORKPLACELATLNG=new LatLng(49.75684032886519,18.626126441274153);

    private LinearLayout topLayout,groupLayout,settingsLayout;
    private User currentUser;

    private final DataBase dataBase=new DataBase();
    private final Listener listener=new Listener();
    private ArrayList<User>users=new ArrayList<>();
    private boolean fullSize=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setElement(R.id.userMapInput);
        setElement(R.id.Group);
        setElement(R.id.setGroups);
        setElement(R.id.settings);

        settingsLayout=setLayout(R.id.settingsLayout);
        groupLayout=setLayout(R.id.groupLayout);
        topLayout=setLayout(R.id.topLayout);
    }
    @Override
    public void onStart() {
        super.onStart();

        if(isRegistering())
        {
            LinearLayout bottomLayout = setLayout(R.id.bottomLayout);
            bottomLayout.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        BitmapDescriptor iconRose=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(this);

        if(!isRegistering())
        {
            catchUsers();
            putMarkersFromIntent();
        }

        mMap.addMarker(new MarkerOptions().position(WORKPLACELATLNG).title("Workplace").icon(iconRose));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(WORKPLACELATLNG,14));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(!isRegistering())
            return;

        if(dynamic!=null)
            dynamic.remove();

        // latitude and longitude from user's input
        LatLng latlng = new LatLng(latLng.latitude, latLng.longitude);
        dynamic=mMap.addMarker(new MarkerOptions().position(latlng).title("User place"));
    }


    private void catchUsers(){
        if(getIntent().getExtras()==null || getIntent().getExtras().get("users")==null)
            return;
        users=(ArrayList<User>)getIntent().getExtras().get("users");
    }

    private void setElement(int id){
        View view = findViewById(id);
        view.setOnClickListener(listener);
    }

    private LinearLayout setLayout(int id){
        LinearLayout linearLayout = findViewById(id);
        linearLayout.setOnClickListener(listener);
        return linearLayout;
    }

private void setClosestUsers(){
        dataBase.setClosestUsers(users);
}
private void showGroup(){
        dataBase.getGroups(groupHashMap -> showGroup(groupHashMap));
}

private void showGroup(HashMap<String,Group> groupHashMap){
//        Group group=currentUser.getIsDriver()? groupHashMap.get(currentUser.getEmail()) :
//                groupHashMap.get(currentUser.getDriver().getEmail());
    Group group;
    if((group=groupHashMap.get(currentUser.getEmail()))==null)
    for(Map.Entry<String, Group> entry : groupHashMap.entrySet())
    {
         group = entry.getValue();
            for(User passenger:group.getPassengers())
                if(passenger.getEmail().equals(currentUser.getEmail()))
                    break;
    }
        User driver=group.getDriver();
    StringBuilder text= new StringBuilder();
    text.append("   Kierowca:");
    text.append("   ").append(driver.getName()).append("   ").append(driver.getSurname()).append("   ").append(driver.getEmail()).append("\n");

    text.append("\n   Pasażerowie\n ");
    for(User passenger:group.getPassengers())
        text.append("   ").append(passenger.getName()).append("   ").append(passenger.getSurname()).append("  ").append(passenger.getEmail()).append("\n");

            TextView groupTextView=findViewById(R.id.groupTextview);
            groupTextView.setText(text);

            groupLayout.setVisibility(View.VISIBLE);
}

    private void putMarkersFromIntent(){
        BitmapDescriptor iconAzure = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        if(users ==null || users.isEmpty())
            return;

        for(User user: users)
        {
            LatLng latLng=user.getLatLng();
            if(user.getEmail().equals(getIntent().getExtras().get("currentUserEmail")))
            {
                currentUser=user;
                mMap.addMarker(new MarkerOptions().position(latLng).title(user.getEmail()).icon(iconAzure));
                continue;
            }

            mMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()));
        }
    }

    private boolean isRegistering(){
        return !(getIntent()==null || getIntent().getExtras()==null || getIntent().getExtras().get("Registration") == null);
    }

    private class Listener implements View.OnClickListener{

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.Group:
                    showGroup();

                    break;
                case R.id.settings:
                   changeVisibility(settingsLayout);
                    break;

                case R.id.setGroups:
                    setClosestUsers();

                    break;

                case R.id.groupLayout:
                    changeVisibility(groupLayout);
                    break;

                case R.id.topLayout:
                    changeTopStance();
                    break;

                case R.id.userMapInput:
                {   // When pressed,pass latLng as double[] to Menu and close the window
                    if(!isRegistering() || dynamic==null)
                    {
                        finish();
                        return;
                    }
                    Intent intent = new Intent();
                    double lat = dynamic.getPosition().latitude;
                    double lng = dynamic.getPosition().longitude;

                    intent.putExtra("latitude", lat); // passing Data back to Menu
                    intent.putExtra("longitude", lng); // passing Data back to Menu
                    setResult(RESULT_OK, intent);

                    finish();
                }
                    break;
            }
        }

        private void changeVisibility(View view){
            int visibility=view.getVisibility()
                    ==View.VISIBLE ? View.INVISIBLE : View.VISIBLE;

            view.setVisibility(visibility);
        }

        private void changeTopStance(){
            settingsLayout.setVisibility(View.INVISIBLE);

            float alpha=fullSize ? 0.9f : 0;
            topLayout.setAlpha(alpha);

            fullSize=!fullSize;
        }
    }
}















