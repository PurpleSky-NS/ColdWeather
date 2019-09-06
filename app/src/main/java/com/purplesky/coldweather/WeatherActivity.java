package com.purplesky.coldweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.purplesky.coldweather.gson.Forecast;
import com.purplesky.coldweather.gson.Weather;
import com.purplesky.coldweather.util.WeatherUtility;

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
        RefreshWeather(getIntent().getStringExtra("weatherId"));
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("weatherId",getIntent().getStringExtra("weatherId")).apply();
    }

    public void RefreshWeather(String weatherId){
        WeatherUtility.GetWeather(this,weatherId,weather -> ShowWeather(weather));
    }

    private void ShowWeather(Weather weather){
        if(weather!=null&&weather.status.equals("ok")) {
            runOnUiThread(()-> {
                titleCity.setText(weather.basic.cityName);
                titleTime.setText(weather.basic.update.updateName.split(" ")[1]);
                nowTemp.setText(weather.now.temperature);
                nowInfo.setText(weather.now.more.info);
                for (Forecast forecast : weather.forecastList)
                    AddForecastItem(forecast);
                aqiAqi.setText(weather.aqi.city.aqi);
                aqiPm25.setText(weather.aqi.city.pm25);
                suggestionComfort.setText("舒适度：" + weather.suggestion.comfort);
                suggestionCarwash.setText("洗车建议：" + weather.suggestion.carWash);
                suggestionCarwash.setText("运动建议：" + weather.suggestion.sport);
            });
        }
        else{
            Toast.makeText(this,"获取天气失败",Toast.LENGTH_LONG).show();
        }
    }

    private void AddForecastItem(Forecast forecast){
        View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastListLayout,false);
        TextView date=view.findViewById(R.id.forecast_item_date);
        TextView info=view.findViewById(R.id.forecast_item_info);
        TextView temp=view.findViewById(R.id.forecast_item_temp);
        date.setText(forecast.date);
        info.setText(forecast.more.info);
        temp.setError(forecast.temperature.min+"℃ ~ "+forecast.temperature.max+"℃");
        forecastListLayout.addView(view);
    }
}
