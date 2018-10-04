package com.example.randyperrone.myweatherapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.example.randyperrone.myweatherapp.Model.Consts.CITY_KEY;
import static com.example.randyperrone.myweatherapp.Model.Consts.LATITUDE_KEY;
import static com.example.randyperrone.myweatherapp.Model.Consts.LONGITUDE_KEY;
import static com.example.randyperrone.myweatherapp.Model.Consts.MY_PERMISSION_ACCESS_FINE_LOCATION;
import static com.example.randyperrone.myweatherapp.Model.Consts.ZIPCODE_KEY;

public class EnterLocationActivity extends AppCompatActivity {
    private static final String TAG = "EnterLocationActivity";
    private static final int ZIPCODE_LENGTH = 5;
    private EditText zipcodeEditText, cityEditText;
    private Button goButton, userLocationButton;
    private String zipcodeString, cityString;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_location);
        zipcodeEditText = (EditText)findViewById(R.id.enter_location_zipcode_edittext);
        cityEditText = (EditText)findViewById(R.id.enter_location_city_edittext);
        goButton = (Button)findViewById(R.id.enter_location_go_button);
        userLocationButton = (Button)findViewById(R.id.enter_location_currentlocation_button);

        //use zipcode
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipcodeString = zipcodeEditText.getText().toString();
                cityString = cityEditText.getText().toString();
                if(zipcodeString != null && zipcodeString.length() == ZIPCODE_LENGTH){
                    Bundle bundle = new Bundle();
                    bundle.putString(ZIPCODE_KEY, zipcodeString);
                    Intent intent = new Intent(getApplicationContext(), DisplayDataActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(cityString != null){
                    Bundle bundle = new Bundle();
                    bundle.putString(CITY_KEY, cityString);
                    Intent intent = new Intent(getApplicationContext(), DisplayDataActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(EnterLocationActivity.this, "Zipcode needs to be correct length", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "ZipCode failed");
                }
            }
        });

        //use location
        userLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(EnterLocationActivity.this);
                //Get permission from user to use location.
                if (ContextCompat.checkSelfPermission(EnterLocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    Log.i(TAG, "Permission for location access");
                    ActivityCompat.requestPermissions(EnterLocationActivity.this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                            MY_PERMISSION_ACCESS_FINE_LOCATION );
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(EnterLocationActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    try{
                                        Log.i(TAG, "Received location of user");
                                        // Get last known location.
                                        //Parse the location data to get longitude and latitude.
                                        String loc = location.toString();
                                        String delims = "[ ]+";
                                        String[] tokens = loc.split(delims);
                                        String latitudeLongitude = tokens[1];
                                        String delims2 = "[,]+";
                                        String[] tokens2 = latitudeLongitude.split(delims2);
                                        String latitude = tokens2[0];
                                        String longitude = tokens2[1];
                                        Bundle bundle = new Bundle();
                                        bundle.putString(LATITUDE_KEY, latitude);
                                        bundle.putString(LONGITUDE_KEY, longitude);
                                        Intent intent = new Intent(getApplicationContext(), DisplayDataActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }catch (Exception e){
                                        Log.e(TAG, "exception: ", e);
                                    }

                                }
                            }
                        });
            }
        });

    }
}
