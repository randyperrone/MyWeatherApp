package com.example.randyperrone.myweatherapp;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import com.example.randyperrone.myweatherapp.Model.Consts;
import com.example.randyperrone.myweatherapp.Model.DownloadWeatherData;
import com.example.randyperrone.myweatherapp.Model.MySingleton;
import com.example.randyperrone.myweatherapp.Model.WeatherData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.text.DecimalFormat;

import static com.example.randyperrone.myweatherapp.Model.Consts.API_KEY_URL;
import static com.example.randyperrone.myweatherapp.Model.Consts.BASE_URL;
import static com.example.randyperrone.myweatherapp.Model.Consts.CITY_KEY;
import static com.example.randyperrone.myweatherapp.Model.Consts.LATITUDE_KEY;
import static com.example.randyperrone.myweatherapp.Model.Consts.LONGITUDE_KEY;
import static com.example.randyperrone.myweatherapp.Model.Consts.WEATHER_URL;
import static com.example.randyperrone.myweatherapp.Model.Consts.ZIPCODE_KEY;
import static com.example.randyperrone.myweatherapp.Model.Consts.ZIP_URL;

public class DisplayDataActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "DisplayDataActivity";
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private String zipCodeString, latitude, longitude, city;
    private WeatherData currentWeatherData;
    private TextView temperatureTV, pressureTV, humidityTV, conditionTV, precipitationTV;
    GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.display_map);
        mapFragment.getMapAsync(this);

        temperatureTV = (TextView)findViewById(R.id.display_temperature);
        pressureTV = (TextView)findViewById(R.id.display_pressure);
        humidityTV = (TextView)findViewById(R.id.display_humidity);
        conditionTV = (TextView)findViewById(R.id.display_condition);
        precipitationTV = (TextView)findViewById(R.id.display_precipitation);
        Bundle bundle = getIntent().getExtras();
        zipCodeString = bundle.getString(ZIPCODE_KEY);
        longitude = bundle.getString(LONGITUDE_KEY);
        latitude = bundle.getString(LATITUDE_KEY);
        city = bundle.getString(CITY_KEY);
        DownloadWeatherData downloadWeatherData = new DownloadWeatherData(this);

        //user selected zipcode, use the zipcode
        if(zipCodeString != null){
            downloadWeatherData.getWeatherData(zipCodeString, new DownloadWeatherData.VolleyCallBack() {
                @Override
                public void onSuccess(WeatherData weatherData) {
                    Log.i(TAG, "Zipcode successfully used");
                    setTextViewFields(weatherData);
                    setMarker();
                }
            });
        }//user selected city, use city
        else if(city != null){
            downloadWeatherData.getWeatherDataForCity(city, new DownloadWeatherData.VolleyCallBack() {
                @Override
                public void onSuccess(WeatherData weatherData) {
                    Log.i(TAG, "onSuccess: City data successful");
                    setTextViewFields(weatherData);
                    setMarker();
                }
            });
        }//user selected location, use longitude and latitude
        else if(longitude != null && latitude != null){
            downloadWeatherData.getWeatherData(longitude, latitude, new DownloadWeatherData.VolleyCallBack() {
                @Override
                public void onSuccess(WeatherData weatherData) {
                    Log.i(TAG, "Location successfully used");
                    setTextViewFields(weatherData);
                    setMarker();
                }
            });
        }//error: display toast and go back.
        else{
            Log.w(TAG, "else: Zipcode and location failed");
            Toast.makeText(this, "An error occurred. Please try again", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void setTextViewFields(WeatherData weatherData){
        if(weatherData != null){
            Log.i(TAG, "setTextViewFields: weatherData loaded");
            currentWeatherData = weatherData;
        }else if(weatherData == null){
            Log.w(TAG, "setTextViewFields: weatherData is null");
            currentWeatherData = new WeatherData(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "");
        }
        //Set textViews.
        if(currentWeatherData.getTemperature() != null){
            Double F = convertKelvinToFahrenheit(currentWeatherData.getTemperature());
            temperatureTV.setText(df2.format(F) + " F");
        }
        if(currentWeatherData.getHumidity() != null){
            humidityTV.setText(currentWeatherData.getHumidity().toString() + "%");
        }
        if(currentWeatherData.getPressure() != null){
            pressureTV.setText(currentWeatherData.getPressure().toString() + " hPa");
        }
        if(currentWeatherData.getChanceOfPrecipitation() != null){
            precipitationTV.setText(currentWeatherData.getChanceOfPrecipitation().toString() + " mm");
        }
        if(currentWeatherData.getCondition() != null){
            conditionTV.setText(currentWeatherData.getCondition());
        }
    }

    private double convertKelvinToFahrenheit(Double Kelvin){
        if(Kelvin == null){
            return 0.0;
        }
        return (9/5 * (Kelvin - 273) + 32);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setMarker(){
        LatLng location = new LatLng(currentWeatherData.getLatitude(), currentWeatherData.getLongitude());
        myMap.addMarker(new MarkerOptions().position(location).title("Weather Location"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(10).build();
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
