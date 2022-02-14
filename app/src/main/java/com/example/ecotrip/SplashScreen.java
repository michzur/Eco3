package com.example.ecotrip;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        //variables
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.botton_animation);
        Animation bottomSpeedAnim = AnimationUtils.loadAnimation(this, R.anim.botton_speed_animation);
        bottomSpeedAnim.setStartOffset(1000);

        ImageView logo = findViewById(R.id.imageView6);
        TextView slogan = findViewById(R.id.textView2);
        TextView slogan1 = findViewById(R.id.textView5);

        logo.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
        slogan1.setAnimation(bottomSpeedAnim);

        int SPLASH_SCREEN = 2500;
        new Handler().postDelayed(() ->
        {   // start new Activity LoginUser
            Intent intent = new Intent(SplashScreen.this, LoginUser.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}