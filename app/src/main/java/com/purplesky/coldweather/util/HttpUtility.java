package com.purplesky.coldweather.util;

import android.annotation.TargetApi;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*同步/异步请求网络数据*/
public class HttpUtility {

    private static final String TAG = "HttpUtility";

    public interface OnResponseListener{
        void onResponse(String response);
    }

    public static void SendRequest(String address, OnResponseListener onResponseListener){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onResponseListener.onResponse(null);
                LogUtility.e(TAG,e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                onResponseListener.onResponse(response.body().string());
            }
        });
    }

    public static String SendRequest(String address)throws IOException {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        return client.newCall(request).execute().body().string();
    }
}
