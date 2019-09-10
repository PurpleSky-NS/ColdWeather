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

import java.nio.Buffer;
import java.text.MessageFormat;

/*获取天气信息的工具类，从本地/网络查找*/
public class WeatherUtility {

    public interface OnGetWeather{
         void onGetWeather(Weather weather);
    }

    private static final String TAG = "WeatherUtility";
    private static final String WEATHER_API_URL="http://guolin.tech/api/weather?cityid={0}&key=123456";

    public static void GetWeather(final Context context,final String weatherId,final OnGetWeather onGetWeather){
        BufferDataUtility.GetData(context,"weather",MessageFormat.format(WEATHER_API_URL, weatherId),true,new BufferDataUtility.OnGetDataListener(){
			public String onGetData(String json,boolean fromNetWork){
				String weatherJson=null;
				Weather weather=null;
				if(fromNetWork){
					weatherJson=ParseWeatherJson(json);
					weather=ParseWeather(weatherJson);
				}
				else
					weather=ParseWeather(json);

				if(weather!=null&&weather.status!=null&&weather.status.equals("ok"))
					onGetWeather.onGetWeather(weather);
           
				else
					RequestWeather(context,weatherId,onGetWeather);

				if(fromNetWork)
					return weatherJson;
				else
					return null;
			}
        });
    }

    public static void RequestWeather(Context context,String weatherId,final OnGetWeather onGetWeather) {
        String url = MessageFormat.format(WEATHER_API_URL, weatherId);
        BufferDataUtility.RequestData(context, "weather", url, true,new BufferDataUtility.OnGetDataListener(){
			public String onGetData(String json,boolean fromNetWork){
				String weatherJson = ParseWeatherJson(json);
				Weather weather = ParseWeather(weatherJson);
				onGetWeather.onGetWeather(weather);
				if (weather != null && weather.status != null && weather.status.equals("ok")){
					onGetWeather.onGetWeather(weather);
					return weatherJson;
				} 
				else
					return null;
			}
        });
    }

    private static String ParseWeatherJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            return jsonArray.getJSONObject(0).toString();
        } catch (Exception e) {
            LogUtility.e(TAG, e.toString());
            return null;
        }
    }

    private static Weather ParseWeather(String weatherJson){
        return new Gson().fromJson(weatherJson,Weather.class);
    }
}
