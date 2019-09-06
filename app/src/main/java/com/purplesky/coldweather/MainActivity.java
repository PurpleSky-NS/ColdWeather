package com.purplesky.coldweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String weatherId= PreferenceManager.getDefaultSharedPreferences(this).getString("weatherId",null);
        if(weatherId!=null)
        {
            Intent intent=new Intent(this,WeatherActivity.class);
            intent.putExtra("weatherId",weatherId);
            startActivity(intent);
            finish();
        }
    }
}
