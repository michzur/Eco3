package com.example.ecotrip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN=2500;
    //variables
    Animation topAnim, bottomAnim,bottonSpeedAnim;
    ImageView logo;
    TextView slogan,slogan1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.botton_animation);
        bottonSpeedAnim = AnimationUtils.loadAnimation(this,R.anim.botton_speed_animation);
        bottonSpeedAnim.setStartOffset(1000);

        logo= findViewById(R.id.imageView6);
        slogan= findViewById(R.id.textView2);
        slogan1= findViewById(R.id.textView5);

        logo.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
        slogan1.setAnimation(bottonSpeedAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginUser.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}