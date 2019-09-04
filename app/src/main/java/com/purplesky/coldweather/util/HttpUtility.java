package com.purplesky.coldweather.util;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtility {

    public static void SendRequest(String address, Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static String SendRequest(String address)throws IOException {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        return client.newCall(request).execute().body().string();
    }
}
