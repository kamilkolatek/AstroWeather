package com.example.astroweather.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.astroweather.R;
import com.example.astroweather.SettingsManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ExtendedInfoFragment extends Fragment {
    private TextView humidityValue, windSpeedValue, windDegreeValue, windGustValue, visibilityValue, cloudsValue;
    private SettingsManager settingsManager;

    public ExtendedInfoFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    public void getExtendedInfo() {
        String basicInfo = settingsManager.getWeatherData();
        if(!basicInfo.equals("error")) {
            JSONObject parsedJSON;
            try {
                parsedJSON = new JSONObject(basicInfo);

                double humidity = parsedJSON.getJSONObject("main").getDouble("humidity");
                humidityValue.setText(humidity + " %");

                JSONObject wind = parsedJSON.getJSONObject("wind");

                if(wind.has("speed")) {
                    double windSpeed = wind.getDouble("speed");
                    windSpeedValue.setText(windSpeed + " m/s");
                }

                if(wind.has("deg")) {
                    double windDeg = wind.getDouble("deg");
                    windDegreeValue.setText(Double.toString(windDeg) + (char) 0x00B0);
                }

                if(wind.has("gust")) {
                    double windGust = wind.getDouble("gust");
                    windGustValue.setText(windGust + " m/s");
                }

                int visibility = parsedJSON.getInt("visibility");
                visibilityValue.setText(visibility + " m");

                int clouds = parsedJSON.getJSONObject("clouds").getInt("all");
                cloudsValue.setText(clouds + " %");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        humidityValue = view.findViewById(R.id.city_name);
        windSpeedValue = view.findViewById(R.id.temperature2);
        windDegreeValue = view.findViewById(R.id.temperature3);
        windGustValue = view.findViewById(R.id.temperature4);
        visibilityValue = view.findViewById(R.id.temperature5);
        cloudsValue = view.findViewById(R.id.clouds);
        settingsManager = new SettingsManager(getContext());
        getExtendedInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extended_info, container, false);
    }
}