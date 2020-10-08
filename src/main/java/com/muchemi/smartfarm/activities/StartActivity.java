package com.muchemi.smartfarm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    private AppCompatButton btnRegister;
    private AppCompatButton btnLogin;
    private LinearLayoutCompat linear_layout;

    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLogin = findViewById(R.id.appCompatButtonLogin);
        btnRegister = findViewById(R.id.appCompatButtonRegister);
        linear_layout = findViewById(R.id.linear_layout);
        icon = findViewById(R.id.icon_image);

        linear_layout.animate().alpha(0f).setDuration(1);

        TranslateAnimation animation = new TranslateAnimation(0,0,0,-1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());
        icon.setAnimation(animation);


        btnLogin.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to login.");

                Intent LoginIntent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(LoginIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }));

        btnRegister.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to Register page.");

                Intent RegisterIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(RegisterIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
              }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            Log.d(TAG,"checkAuthenticationState: user is null, navigate back to the Login Screen.");
            Intent intent = new Intent(StartActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else{
            Log.d(TAG,"checkAuthenticationState: User is Authenticated.");
        }
    }

    private class MyAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            icon.clearAnimation();
            icon.setVisibility(View.INVISIBLE);
            linear_layout.animate().alpha(1f).setDuration(1000);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
