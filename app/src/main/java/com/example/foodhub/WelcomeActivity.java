package com.example.foodhub;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeHeading;
    String originalHeading = "Welcome"; // [W,E,L,C,O,M,E]
    int index = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (index < originalHeading.length()) {
                String partialText = originalHeading.substring(0, index + 1);
                welcomeHeading.setText(partialText);
                index++;
                handler.postDelayed(this, 100);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    int currentOrientation = getResources().getConfiguration().orientation;
                    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {

                        intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        intent.putExtra("isLandscape", true);
                    } else {

                        intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    }

                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        welcomeHeading = findViewById(R.id.welcomeHeading);
        handler.post(runnable);
    }

}