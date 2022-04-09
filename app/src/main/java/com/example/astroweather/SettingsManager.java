package com.example.astroweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SettingsManager {
    private final SharedPreferences settings;
    private final SharedPreferences.Editor editor;
    private final ApiHandler apiHandler;

    @SuppressLint("CommitPrefEdits")
    public SettingsManager(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();
        apiHandler = new ApiHandler();
    }

    public void updateWeatherData(String cityName, ArrayList<String> cities) throws JSONException, IOException {
        JSONObject weatherData = apiHandler.getWeatherData(cityName);
        JSONObject forecastData = apiHandler.getForecastData(weatherData.getJSONObject("coord").getDouble("lat"),
                weatherData.getJSONObject("coord").getDouble("lon"));

        SharedPreferences.Editor editor = settings.edit();

        String currentCity = weatherData.getString("name");

        if(!cities.contains(currentCity))
            cities.add(currentCity);
        editor.putStringSet("cities", new HashSet<>(cities));

        editor.putString(currentCity + "_weather", weatherData.toString());
        editor.putString(currentCity + "_forecast", forecastData.toString());
        editor.apply();
    }

    public void deleteWeatherData(String currentCity, ArrayList<String> cities) {
        cities.remove(currentCity);
        editor.putStringSet("cities", new HashSet<>(cities));
        editor.remove(currentCity + "_weather");
        editor.remove(currentCity + "_forecast");
        if(cities.isEmpty())
            editor.clear();
        editor.apply();
    }

    public void setCurrentSettings(int refreshTime, String currentCity, String units) {
        editor.putInt("refresh_time", refreshTime);
        editor.putString("current_city", currentCity);
        editor.putString("units", units);
        editor.apply();
    }

    public void setCurrentCoord(String cityName) throws JSONException {
        String basicInfo = settings.getString(cityName + "_weather", null);
        JSONObject jsonObject = new JSONObject(basicInfo);
        editor.putString("latitude", String.valueOf(jsonObject.getJSONObject("coord").getDouble("lat")));
        editor.putString("longitude", String.valueOf(jsonObject.getJSONObject("coord").getDouble("lon")));
        editor.apply();
    }

    public Set<String> getCitiesList() {
        return settings.getStringSet("cities", new HashSet<>());
    }

    public double getCurrentLatitude() {
        return Double.parseDouble(settings.getString("latitude", "51.759445"));
    }

    public double getCurrentLongitude() {
        return Double.parseDouble(settings.getString("longitude", "19.457216"));
    }

    public int getRefreshTime() {
        return settings.getInt("refresh_time", 5);
    }

    public String getCurrentUnits() {
        return settings.getString("units", "Celsius");
    }

    public String getCurrentCity() {
        return settings.getString("current_city", null);
    }

    public String getWeatherData() {
        return settings.getString(getCurrentCity() + "_weather", "error");
    }

    public String getForecastData() {
        return settings.getString(getCurrentCity() + "_forecast", "error");
    }
}
