package com.example.randyperrone.myweatherapp.Model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.randyperrone.myweatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.randyperrone.myweatherapp.Model.Consts.*;

public class DownloadWeatherData {
    private static final String TAG = "DownloadWeatherData";
    WeatherData weatherData;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double pressure;
    private Double humidity;
    private Double precipitation;
    private String condition;
    private Context context;
    private final String APIKEY;

    public DownloadWeatherData(Context context) {
        this.context = context;
        APIKEY = context.getString(R.string.open_weather_key);
    }

    //zipcode method
    public void getWeatherData(String zipCode, final VolleyCallBack callBack){

        if(zipCode == null){
            return;
        }
        final String URL_WEATHER = BASE_URL + WEATHER_URL + ZIP_URL + zipCode + API_KEY_URL + APIKEY;
        final String URL_FORECAST = BASE_URL + FORECAST_URL + ZIP_URL + zipCode + API_KEY_URL + APIKEY;
        downloadData(URL_WEATHER, URL_FORECAST, callBack);
    }
    //city method
    public void getWeatherDataForCity(String city, final VolleyCallBack callBack){
        if(city == null){
            return;
        }
        final String URL_WEATHER = BASE_URL + WEATHER_URL + CITY_URL + city + API_KEY_URL + APIKEY;
        final String URL_FORECAST = BASE_URL + FORECAST_URL + CITY_URL + city + API_KEY_URL + APIKEY;
        downloadData(URL_WEATHER, URL_FORECAST, callBack);
    }
    //location method
    public void getWeatherData(String longitude, String latitude, final VolleyCallBack callBack){
        if(longitude == null || latitude == null){
            return;
        }
        final String URL_WEATHER = BASE_URL + WEATHER_URL + LAT_URL + latitude + LON_URL  + longitude + API_KEY_URL + APIKEY;
        final String URL_FORECAST = BASE_URL + FORECAST_URL + LAT_URL + latitude + LON_URL  + longitude + API_KEY_URL + APIKEY;
        downloadData(URL_WEATHER, URL_FORECAST, callBack);
    }
    //Download weather and forecast data. Forecast needed for precipitation
    private void downloadData(String URL_WEATHER, String URL_FORECAST, final VolleyCallBack callBack){
        //Download Weather Data (excludes forecast for precipitation)
        final JsonObjectRequest requestWeather = new JsonObjectRequest(Request.Method.GET, URL_WEATHER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response != null){
                    try {
                        JSONObject object = response.getJSONObject(COORDINATES_JSON);
                        longitude = object.getDouble(LONGITUDE_JSON);
                        latitude = object.getDouble(LATITUDE_JSON);
                        object = response.getJSONObject(MAIN_JSON);
                        temperature = object.getDouble(TEMPERATURE_JSON);
                        pressure = object.getDouble(PRESSURE_JSON);
                        humidity = object.getDouble(HUMIDITY_JSON);
                        JSONArray array = response.getJSONArray(WEATHER_JSON);
                        //There is only one entry in the JSON array. Don't know why they set it up like that.
                        object = array.getJSONObject(0);
                        condition = object.getString(MAIN_JSON) + ": " + object.getString(DESCRIPTION_JSON);
                    } catch (JSONException e) {
                        Log.e(TAG, "Volley parse JSON failed: Weather", e);
                    }
                }
                else{
                    Log.w(TAG, "onResponse: is null");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                errorDetection(error);
            }
        });
        //Download forecast data (for precipitation)
        final JsonObjectRequest requestForecast = new JsonObjectRequest(Request.Method.GET, URL_FORECAST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response != null){
                    try {
                        JSONArray array = response.getJSONArray(LIST_JSON);
                        JSONObject object;
                        //Check for precipitation for the next 9 hours (hench i < 2).  JSON is broken up into 3 hour increments
                        //and predicts rainfall for every three hours.  If no rain for the next nine hours, return 0.0.
                        //Rain key is null if no rain for that 3 hour period. Crappy JSON setup if you ask me.
                        int i;
                        for(i = 0; i < 2; i++){
                            object = array.getJSONObject(i);
                            JSONObject rainObject;
                            //This mess is to check for the RAIN key. Sometimes it's not even there.
                            //If it is there, sometimes the 3 houre forecast isn't there, so need to check for that.
                            if(object.has(RAIN_JSON)){
                                rainObject = object.getJSONObject(RAIN_JSON);
                                if(rainObject.has(THREEHOURFORECAST_JSON)){
                                    precipitation = rainObject.getDouble(THREEHOURFORECAST_JSON);
                                    break;
                                }
                            }
                            //Make rain 0 mm if not found
                            if(precipitation == null){
                                precipitation = 0.0;
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Volley parse JSON failed: Forecast", e);
                    }
                    Log.i(TAG, "Parse Weather and Forecast successful");
                    weatherData = new WeatherData(latitude, longitude, temperature, pressure, humidity, precipitation, condition);
                    callBack.onSuccess(weatherData);
                }
                else{
                    Log.w(TAG, "onResponse: is null");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                errorDetection(error);
            }
        });
        MySingleton.getInstance(context).addToRequestque(requestWeather);
        MySingleton.getInstance(context).addToRequestque(requestForecast);
    }

    private void errorDetection(VolleyError error){
        if(error instanceof TimeoutError || error instanceof NoConnectionError){
            errorDetected("Connection Error \n or \nthe connection timed out");
        } else if(error instanceof AuthFailureError){
            errorDetected("Authentication Failure Error");
        } else if(error instanceof ServerError){
            errorDetected("There was an error with the server");
        } else if(error instanceof NetworkError){
            errorDetected("There was a Network Error");
        } else if(error instanceof ParseError){
            errorDetected("Data Parsing Error");
        }
    }

    private void errorDetected(String error){
        Log.e(TAG, "errorDetected: " + error);
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public interface VolleyCallBack{
        void onSuccess(WeatherData weatherData);
    }
}
