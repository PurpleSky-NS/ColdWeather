package com.purplesky.coldweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.purplesky.coldweather.View.ChooseAreaFragment;
import com.purplesky.coldweather.gson.Forecast;
import com.purplesky.coldweather.gson.Weather;
import com.purplesky.coldweather.service.UpdateService;
import com.purplesky.coldweather.util.BufferDataUtility;
import com.purplesky.coldweather.util.WeatherUtility;

public class WeatherActivity extends AppCompatActivity {

    private Button titleNav;
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

    public DrawerLayout weatherDrawerLayout;
    private SwipeRefreshLayout weatherSwipeRefresh;
    private ImageView weatherImage;

    private String weatherId;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver updateReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RefreshWeather(weatherId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        titleNav = findViewById(R.id.title_nav_btn);
        titleCity = findViewById(R.id.title_city);
        titleTime = findViewById(R.id.title_time);
        nowTemp = findViewById(R.id.now_temp);
        nowInfo = findViewById(R.id.now_info);
        forecastListLayout = findViewById(R.id.forecast_list_layout);
        aqiAqi = findViewById(R.id.aqi_aqi);
        aqiPm25 = findViewById(R.id.aqi_pm25);
        suggestionComfort = findViewById(R.id.suggestion_comfort);
        suggestionCarwash = findViewById(R.id.suggestion_carwash);
        suggestionSport = findViewById(R.id.suggestion_sport);
        weatherDrawerLayout = findViewById(R.id.weather_drawerlayout);
        weatherSwipeRefresh = findViewById(R.id.weather_swiperefresh);
        weatherImage = findViewById(R.id.weather_image);
        weatherId = getIntent().getStringExtra("weatherId");

        /*设置刷新控件*/
        weatherSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        weatherSwipeRefresh.setOnRefreshListener(() -> RefreshWeather(weatherId));

        titleNav.setOnClickListener(v->weatherDrawerLayout.openDrawer(GravityCompat.START));

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter=new IntentFilter("UPDATE_WEATHER");
        localBroadcastManager.registerReceiver(updateReceiver,filter);

        Intent intent=new Intent(this, UpdateService.class);
        startService(intent);

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("weatherId", getIntent().getStringExtra("weatherId")).apply();
        LoadWeather(weatherId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(updateReceiver);
    }

    /*可以被外部调用，从网络上刷新天气/图片数据*/
    public void RefreshWeather(String weatherId){
        weatherSwipeRefresh.setRefreshing(true);
        WeatherUtility.RequestWeather(this,weatherId,weather -> ShowWeather(weather));
        BufferDataUtility.RequestData(this,"weatherImage","http://guolin.tech/api/bing_pic",true,(data,t) -> {
            ShowImage(data);
            return data;
        });
    }

    /*从本地/网络刷新*/
    private void LoadWeather(String weatherId){
        weatherSwipeRefresh.setRefreshing(true);
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
            forecastListLayout.removeAllViews();
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
        if(weatherSwipeRefresh.isRefreshing())
            weatherSwipeRefresh.setRefreshing(false);
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
