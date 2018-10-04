package com.example.randyperrone.myweatherapp.Model;

public class WeatherData {
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double pressure;
    private Double humidity;
    private Double chanceOfPrecipitation;
    private String condition;

    public WeatherData(Double latitude, Double longitude, Double temperature, Double pressure, Double humidity, Double chanceOfPrecipitation, String condition) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.condition = condition;
        this.chanceOfPrecipitation = chanceOfPrecipitation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getHumidity() {
        return humidity;
    }

    public String getCondition() {
        return condition;
    }

    public Double getChanceOfPrecipitation() {
        return chanceOfPrecipitation;
    }
}
