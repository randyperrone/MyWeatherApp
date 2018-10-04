package com.example.randyperrone.myweatherapp.Model;

public final class Consts {
    //URL
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String FORECAST_URL = "forecast?";
    public static final String WEATHER_URL = "weather?";
    public static final String ZIP_URL = "zip=";
    public static final String API_KEY_URL = "&APPID=";
    public static final String LAT_URL = "lat=";
    public static final String LON_URL = "&lon=";
    public static final String CITY_URL = "q=";

    //JSON
    public static final String WEATHER_JSON = "weather";
    public static final String MAIN_JSON = "main";
    public static final String DESCRIPTION_JSON = "description";
    public static final String TEMPERATURE_JSON = "temp";
    public static final String PRESSURE_JSON = "pressure";
    public static final String HUMIDITY_JSON = "humidity";
    public static final String LATITUDE_JSON = "lat";
    public static final String LONGITUDE_JSON = "lon";
    public static final String COORDINATES_JSON = "coord";
    public static final String LIST_JSON = "list";
    public static final String RAIN_JSON = "rain";
    public static final String THREEHOURFORECAST_JSON = "3h";

    //Bundle keys
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String ZIPCODE_KEY = "zipcode";
    public static final String CITY_KEY = "city";

    //Permission
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
}
