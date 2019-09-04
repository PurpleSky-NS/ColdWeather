package com.purplesky.coldweather.util;

import com.purplesky.coldweather.db.City;
import com.purplesky.coldweather.db.County;
import com.purplesky.coldweather.db.Province;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

public class CityUtility {

    private static final String TAG = "CityUtility";
    private static final String CITY_API_URL="http://guolin.tech/api/china";

    public static List<Province> QueryProvices(){
        List<Province> provinces=LitePal.findAll(Province.class);
        if(provinces.isEmpty())
        {
            try {
                String json = HttpUtility.SendRequest(CITY_API_URL);
                if(json==null||json.isEmpty())
                {
                    LogUtility.e(TAG,"获取省份信息错误 json=empty");
                    return null;
                }
                JSONArray array=new JSONArray(json);
                for(int i=0;i<array.length();++i)
                {
                    Province province=new Province();
                    JSONObject object=array.getJSONObject(i);
                    province.setCode(object.getInt("id"));
                    province.setName(object.getString("name"));
                    province.save();
                    provinces.add(province);
                }
            }catch (Exception e){
                LogUtility.e(TAG,e.getMessage());
            }
        }
        return provinces;
    }

    public static List<City> QueryCities(Province province){
        List<City> cities=LitePal.findAll(City.class);
        if(cities.isEmpty())
        {
            try {
                String json = HttpUtility.SendRequest(CITY_API_URL+"/"+province.getCode());
                if(json==null||json.isEmpty())
                {
                    LogUtility.e(TAG,"获取城市信息错误 Province : id = "+province.getCode()+" name = "+province.getName());
                    return null;
                }
                JSONArray array=new JSONArray(json);
                for(int i=0;i<array.length();++i)
                {
                    City city=new City();
                    JSONObject object=array.getJSONObject(i);
                    city.setCityCode(object.getInt("id"));
                    city.setName(object.getString("name"));
                    city.setProvinceCode(province.getCode());
                    city.save();
                    cities.add(city);
                }
            }catch (Exception e){
                LogUtility.e(TAG,e.getMessage());
            }
        }
        return cities;
    }

    public static List<County> QueryCounties(City city){
        List<County> counties=LitePal.findAll(County.class);
        if(counties.isEmpty())
        {
            try {
                String json = HttpUtility.SendRequest(CITY_API_URL+"/"+city.getProvinceCode()+"/"+city.getCityCode());
                if(json==null||json.isEmpty())
                {
                    LogUtility.e(TAG,"获取乡县信息错误 [Province : id = "+city.getProvinceCode()+"] City id = "+city.getCityCode()+"name = "+city.getName());
                    return null;
                }
                JSONArray array=new JSONArray(json);
                for(int i=0;i<array.length();++i)
                {
                    County county=new County();
                    JSONObject object=array.getJSONObject(i);
                    county.setWeatherId(object.getInt("weather_id"));
                    county.setCountyCode(object.getInt("id"));
                    county.setName(object.getString("name"));
                    county.setCityCode(city.getCityCode());
                    county.save();
                    counties.add(county);
                }
            }catch (Exception e){
                LogUtility.e(TAG,e.getMessage());
            }
        }
        return counties;
    }


}
