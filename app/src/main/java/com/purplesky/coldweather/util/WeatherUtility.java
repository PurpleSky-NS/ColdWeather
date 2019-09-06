package com.purplesky.coldweather.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.purplesky.coldweather.db.City;
import com.purplesky.coldweather.db.County;
import com.purplesky.coldweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;

public class WeatherUtility {

    public interface OnGetWeather{
         void onGetWeather(Weather weather);
    }

    private static final String TAG = "WeatherUtility";
    private static final String WEATHER_API_URL="http://guolin.tech/api/weather?cityid={0}&key=123456";

    public static void GetWeather(Context context,String weatherId,OnGetWeather onGetWeather){
        String weatherJson=PreferenceManager.getDefaultSharedPreferences(context).getString("weather",null);
        if(weatherJson==null)
            RequestWeather(context,weatherId,onGetWeather);
        else
            onGetWeather.onGetWeather(ParseWeather(weatherJson));
    }

    public static void RequestWeather(Context context,String weatherId,OnGetWeather onGetWeather) {
        String url = MessageFormat.format(WEATHER_API_URL, weatherId);
        HttpUtility.SendRequest(url, (json) -> {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                String weatherJson = jsonArray.getJSONObject(0).toString();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString("weather", weatherJson);
                editor.apply();
                onGetWeather.onGetWeather(ParseWeather(weatherJson));
            } catch (Exception e) {
                LogUtility.e(TAG, e.toString());
            }
        });
    }

    private static Weather ParseWeather(String weatherJson){
        return new Gson().fromJson(weatherJson,Weather.class);
    }
}
