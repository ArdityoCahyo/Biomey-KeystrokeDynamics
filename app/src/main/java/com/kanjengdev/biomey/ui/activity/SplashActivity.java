package com.kanjengdev.biomey.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.kanjengdev.biomey.MainActivity;
import com.kanjengdev.biomey.databinding.ActivitySplashBinding;
import com.kanjengdev.biomey.utils.SharedPreferences;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    SharedPreferences sharedPreferences = new SharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.strike.setPaintFlags(binding.strike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.loadUsername().isEmpty() && sharedPreferences.loadUID().isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 2000L);

    }
}