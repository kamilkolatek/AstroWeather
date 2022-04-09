package com.example.astroweather.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.astroweather.R;
import com.example.astroweather.SettingsManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class BasicInfoFragment extends Fragment {
    private TextView weatherDescription, cityName, temperatureValue, pressureValue;
    private ImageView imageView;
    private SettingsManager settingsManager;

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void getBasicInfo() {
        String basicInfo = settingsManager.getWeatherData();
        if(!basicInfo.equals("error")) {
            JSONObject parsedJSON;
            try {
                parsedJSON = new JSONObject(basicInfo);
                String iconUrl = "https://openweathermap.org/img/w/" + parsedJSON.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png";
                Picasso.get().load(iconUrl).into(imageView);

                cityName.setText(parsedJSON.getString("name"));
                weatherDescription.setText(parsedJSON.getJSONArray("weather").getJSONObject(0).getString("description"));

                double temperature = parsedJSON.getJSONObject("main").getDouble("temp");
                char units = 'C';
                if(settingsManager.getCurrentUnits().equals("Fahrenheit")) {
                    temperature = (temperature * (9.0 / 5)) + 32;
                    units = 'F';
                }

                temperatureValue.setText(String.format("%.2f ", temperature) + (char) 0x00B0 + units);

                double pressure = parsedJSON.getJSONObject("main").getDouble("pressure");
                pressureValue.setText(pressure + " hPa");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weatherDescription = view.findViewById(R.id.weather_description);
        cityName = view.findViewById(R.id.city_name);
        temperatureValue = view.findViewById(R.id.temperature2);
        pressureValue = view.findViewById(R.id.temperature3);
        imageView = view.findViewById(R.id.weather_icon);
        settingsManager = new SettingsManager(getContext());
        getBasicInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basic_info, container, false);
    }
}