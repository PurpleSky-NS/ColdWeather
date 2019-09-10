package com.purplesky.coldweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.purplesky.coldweather.util.BufferDataUtility;
import com.purplesky.coldweather.util.LogUtility;
import com.purplesky.coldweather.util.WeatherUtility;

public class UpdateService extends Service {

    private static final String TAG = "UpdateService";

    private LocalBroadcastManager localBroadcastManager;
    private Intent broadcastIntent;

    @Override
    public void onCreate() {
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        broadcastIntent=new Intent("UPDATE_WEATHER");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherId=preferences.getString("weatherId",null);
        if(weatherId!=null)
            WeatherUtility.RequestWeather(this,weatherId,(weather)->{});

        BufferDataUtility.RequestData(this,"weatherImage","http://guolin.tech/api/bing_pic",false,(data, t) ->  data);
        localBroadcastManager.sendBroadcast(broadcastIntent);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int wakeTime=4*60*60*1000;
        long triggerTime=wakeTime+ SystemClock.elapsedRealtime();
        Intent i=new Intent(this,UpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        LogUtility.d(TAG,"Active Service");
        return super.onStartCommand(intent, flags, startId);
    }
}
