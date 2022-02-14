package com.example.ecotrip;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class LoginUser extends AppCompatActivity implements View.OnClickListener {

    private EditText loginEmail,loginPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_user);

        TextView registerText = findViewById(R.id.registerText);
        Button loginButton = findViewById(R.id.loginButton);
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);

        registerText.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();
    }

   //Listener
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.registerText:
                startActivity(new Intent(this,RegisterUser.class));
                break;

            case R.id.loginButton:
                userLogin();
                break;
        }
    }

   private void setError(TextView textView,String error){
        textView.setError(error);
        textView.requestFocus();
   }
    private void userLogin() {
        // getting String from EditTexts
        String email=loginEmail.getText().toString().trim();
        String password=loginPassword.getText().toString().trim();

        if(password.isEmpty())
        {
            setError(loginPassword,"Wprowadź hasło");
            return;
        }
        if(password.length()<6)
        {
            setError(loginPassword,"Hasło powinno mieć minimum 6 znaków");
            return;
        }
        if(email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            setError(loginEmail,"Niepoprawny email");
            return;
        }
        // login user
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
                // passing a FireStoreCallback to the asynchronous Database method
                new DataBase().readUsers(users -> openMapsActivity(users,loginEmail.getText().toString()));

            else
                Toast.makeText(LoginUser.this,"Logowanie nie powiodło się. Sprawdź dane",Toast.LENGTH_LONG).show();
        });
    }

    private void openMapsActivity(ArrayList<User> users, String currentUserEmail){
        Intent intent = new Intent(this, MapsActivity.class);
        //  passing data between activities
        intent.putExtra("users",users);
        intent.putExtra("currentUserEmail",currentUserEmail);
        startActivity(intent);
    }
}