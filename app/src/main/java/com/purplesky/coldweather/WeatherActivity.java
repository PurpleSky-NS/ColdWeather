package com.purplesky.coldweather;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.purplesky.coldweather.gson.Forecast;
import com.purplesky.coldweather.gson.Weather;
import com.purplesky.coldweather.util.BufferDataUtility;
import com.purplesky.coldweather.util.WeatherUtility;

import java.nio.BufferOverflowException;

public class WeatherActivity extends AppCompatActivity {

    private TextView titleCity;
    private TextView titleTime;

    private TextView nowTemp;
    private TextView nowInfo;

    private LinearLayout forecastListLayout;

    private TextView aqiAqi;
    private TextView aqiPm25;

    private TextView suggestionComfort;
    private TextView suggestionCarwash;
    private TextView suggestionSport;

    private ImageView weatherImage;

    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        titleCity=findViewById(R.id.title_city);
        titleTime=findViewById(R.id.title_time);
        nowTemp =findViewById(R.id.now_temp);
        nowInfo=findViewById(R.id.now_info);
        forecastListLayout=findViewById(R.id.forecast_list_layout);
        aqiAqi=findViewById(R.id.aqi_aqi);
        aqiPm25=findViewById(R.id.aqi_pm25);
        suggestionComfort=findViewById(R.id.suggestion_comfort);
        suggestionCarwash=findViewById(R.id.suggestion_carwash);
        suggestionSport=findViewById(R.id.suggestion_sport);
        weatherImage=findViewById(R.id.weather_image);
        weatherId=getIntent().getStringExtra("weatherId");

        if(Build.VERSION.SDK_INT>=21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("weatherId",getIntent().getStringExtra("weatherId")).apply();
        LoadWeather(weatherId);
    }

    public void RefreshWeather(String weatherId){
        WeatherUtility.RequestWeather(this,weatherId,weather -> ShowWeather(weather));
        BufferDataUtility.RequestData(this,"weatherImage","http://guolin.tech/api/bing_pic",true,(data,t) -> {
            ShowImage(data);
            return data;
        });
    }

    private void LoadWeather(String weatherId){
        WeatherUtility.GetWeather(this,weatherId,weather -> ShowWeather(weather));
        BufferDataUtility.GetData(this,"weatherImage","http://guolin.tech/api/bing_pic",true,(data,fromNetwork) -> {
            ShowImage(data);
            if(fromNetwork)
                return data;
            else
                return null;
        });
    }

    private void ShowWeather(Weather weather){
        if(weather!=null) {
            titleCity.setText(weather.basic.cityName);
            titleTime.setText(weather.basic.update.updateName.split(" ")[1]);
            nowTemp.setText(weather.now.temperature);
            nowInfo.setText(weather.now.more.info);
            for (Forecast forecast : weather.forecastList)
                AddForecastItem(forecast);
            aqiAqi.setText(weather.aqi.city.aqi);
            aqiPm25.setText(weather.aqi.city.pm25);
            suggestionComfort.setText("舒适度：" + weather.suggestion.comfort.txt);
            suggestionCarwash.setText("洗车建议：" + weather.suggestion.carWash.txt);
            suggestionSport.setText("运动建议：" + weather.suggestion.sport.txt);
        }
        else{
            Toast.makeText(this,"获取天气失败",Toast.LENGTH_LONG).show();
        }
    }

    private void ShowImage(String imageData){
        Glide.with(this).load(imageData).into(weatherImage);
    }

    private void AddForecastItem(Forecast forecast){
        View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastListLayout,false);
        TextView date=view.findViewById(R.id.forecast_item_date);
        TextView info=view.findViewById(R.id.forecast_item_info);
        TextView temp=view.findViewById(R.id.forecast_item_temp);
        date.setText(forecast.date);
        info.setText(forecast.more.info);
        temp.setText(forecast.temperature.min+"℃ ~ "+forecast.temperature.max+"℃");
        forecastListLayout.addView(view);
    }
}
