package com.example.ecotrip;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    String TAG=this.getClass().getSimpleName();
    DataBase dataBase=new DataBase();
    private final int MAXCARSEATS=20;
    private LatLng latLng;

    private TextView registerLocalization;
    private CheckBox isDriverCheckBox;
    private FirebaseAuth mAuth;
    private EditText registerName,registerSurname,registerPassword,registerEmail,registerSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // Setting Up Activity Elements
        mAuth = FirebaseAuth.getInstance();
        registerName=findViewById(R.id.registerName);
        registerPassword=findViewById(R.id.registerPassword);
        registerEmail=findViewById(R.id.registerEmail);
        registerSurname=findViewById(R.id.registerSurname);
        isDriverCheckBox=findViewById(R.id.registerIsDriver);
        registerLocalization=findViewById(R.id.registerMap);
        registerSeats=findViewById(R.id.registerSeats);
        Button registerButton=findViewById(R.id.registerButton);


        //Setting Click Listener see @onClick
        isDriverCheckBox.setOnClickListener(this);
        registerLocalization.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }
     // Helpers
    private String getStringFromText(EditText editText){
        return editText.getText().toString().trim();
    }
    private boolean isTextValid(String text){ // name and surname checker
        return !text.isEmpty() && text.matches("[a-zA-Z]+");
    }
    private boolean isLocationValid(LatLng latLng){ //latlng checker
        final double MINLATITUDE=49.29899, MAXLATITUDE=54.79086,
                     MINLONGITUDE=14.24712, MAXLONGITUDE=23.89251; // min/max cords for Poland

        return (latLng.latitude >= MINLATITUDE) && (latLng.latitude <= MAXLATITUDE)
                &&  (latLng.longitude >= MINLONGITUDE) && (latLng.longitude<= MAXLONGITUDE);
    }

    private void setError(TextView view,String errorText){
        view.setError(errorText);
        view.requestFocus();
    }

    private void registerUser(){
    String name=getStringFromText(registerName);
    String surname=getStringFromText(registerSurname);
    String password=getStringFromText(registerPassword);
    String email=getStringFromText(registerEmail);
    User user;
        if(!isTextValid(name))
        {
            setError(registerName,"Podaj poprawne imię");
            return;
        }
        if(!isTextValid(surname))
        {
            setError(registerSurname,"Podaj poprawne nazwisko");
            return;
        }
        if(password.isEmpty())
        {
            setError(registerPassword,"Hasło jest wymagane");
            return;
        }
        if(password.length()<6)
        {
            setError(registerPassword,"Hasło powinno mieć minimum 6 znaków");
            return;
        }
        if(email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            setError(registerEmail,"Podaj poprawny email");
            return;
        }
        if(latLng==null)
        {
            setError(registerLocalization,"Wprowadź lokalizację");
            Log.d(TAG," Brak lokalizacji.");
            return;
        }
        if(!isLocationValid(latLng))
        {
            setError(registerLocalization,"Wprowadź lokalizację w Polsce");
            Log.d(TAG," Błędna lokalizacja. "+latLng);
            return;
        }

double latitude=latLng.latitude;double longitude=latLng.longitude;

        // IsDriverCheckBox has to be the last segment of that if segment
        if(isDriverCheckBox.isChecked())
        {
            if(registerSeats.getText().toString().isEmpty()){
                setError(registerSeats,"Wprowadź dane!");
                return;
            }
            int seatsNumber=Integer.parseInt(getStringFromText(registerSeats));
            if(seatsNumber<0 || seatsNumber > MAXCARSEATS)
            {
                setError(registerSeats, "Wprowadź poprawną ilość miejsc!");
                return;
            }

            user=new User(name,surname,email,latitude,longitude,isDriverCheckBox.isChecked(),new Car(seatsNumber));
        } else user=new User(name,surname,email,latitude,longitude,isDriverCheckBox.isChecked());

        createAccount(email,password);
        dataBase.sendToDB(user);

        finish();
    }


    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterUser.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Listener
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.registerButton:
                registerUser();
                break;
            case R.id.registerMap:
                openMap();
                break;
            case R.id.registerIsDriver:
                registerSeats.setVisibility
                        (isDriverCheckBox.isChecked() ? View.VISIBLE:View.INVISIBLE);
        }
    }
    public void openMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        //TODO here you can pass user's latlng if he opens map again after chosing marker once
        // you have to catch it in MapsActivity @putMarkersFromIntent();

        intent.putExtra("Registration",true);
        startActivityForResult(intent,1); // starts an Activity and receive data back @onActivityResult
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //receive data from mapActivity and saves it in latlng
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode==RESULT_OK)
        {
            double longitude=(double)data.getExtras().get("longitude");
            double latitude=(double)data.getExtras().get("latitude");
            latLng=new LatLng(latitude,longitude);
            Log.d(TAG," LatLng "+latLng);
        }
    }
}