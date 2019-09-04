package com.purplesky.coldweather.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {
    private int id;
    private int countyCode;
    private int weatherId;
    private int cityCode;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(int code) {
        this.countyCode = code;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
