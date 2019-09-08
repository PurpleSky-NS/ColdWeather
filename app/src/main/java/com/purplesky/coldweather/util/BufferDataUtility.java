package com.purplesky.coldweather.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.util.Log;

public class BufferDataUtility {

    private static final String TAG = "BufferDataUtility";

    public interface OnGetDataListener{
        /*返回值为需要存储的数据，为null表示不存储/覆盖之前的数据，下次获取key时会获取到这个*/
        String onGetData(String data,boolean fromNetWork);
    }

    /*从requestUrl或者SharedPre获取数据
    如果要inUiThread则Context必须是Activity
    如果是从网络获取的数据并且onUiThread才会调用runOnUiThread*/
    public static void GetData(Context context, String name, String requestUrl,boolean onUiThread, OnGetDataListener onGetDataListener) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String data = preferences.getString(name, null);
        if (data == null)
            RequestData(context, name, requestUrl, onUiThread, onGetDataListener);
        else {
            SaveData(editor, name, onGetDataListener.onGetData(data, false));
            LogUtility.d(TAG, "Get local data : " + name + "(" + data + ")");
        }
    }

    /*从requestUrl获取，非null则存入SharedPre*/
    public static void RequestData(Context context, String name, String requestUrl,boolean onUiThread, OnGetDataListener onGetDataListener) {
        HttpUtility.SendRequest(requestUrl, (data) -> {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            if (onUiThread)
                ((Activity) context).runOnUiThread(() -> SaveData(editor, name, onGetDataListener.onGetData(data, true)));
            else
                SaveData(editor, name, onGetDataListener.onGetData(data, true));
            LogUtility.d(TAG, "Get Network data : " + name + "(" + data + ")");
        });
    }

    private static void SaveData(SharedPreferences.Editor editor,String name,String saveData) {
        if (editor != null && saveData != null) {
            editor.putString(name, saveData);
            editor.apply();
            LogUtility.d(TAG, "Save to local : " + name + "(" + saveData + ")");
        }
    }

}
