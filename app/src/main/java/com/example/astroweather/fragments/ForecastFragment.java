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

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForecastFragment extends Fragment {
    private TextView[] days;
    private TextView[] temperatures;
    private ImageView[] icons;
    private SettingsManager settingsManager;

    public ForecastFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat", "DefaultLocale"})
    public void getForecast() {
        String basicInfo = settingsManager.getForecastData();
        if(!basicInfo.equals("error")) {
            JSONObject parsedJSON;
            try {
                parsedJSON = new JSONObject(basicInfo);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

                for(int i = 0; i < 5; i++) {
                    JSONObject day = parsedJSON.getJSONArray("daily").getJSONObject(i + 1);
                    String transformedDate = simpleDateFormat.format(new Date(Long.parseLong(day.getString("dt")) * 1000));

                    double temperature = day.getJSONObject("temp").getDouble("day");
                    char units = 'C';
                    if(settingsManager.getCurrentUnits().equals("Fahrenheit")) {
                        temperature = (temperature * (9.0 / 5)) + 32;
                        units = 'F';
                    }

                    days[i].setText(transformedDate);
                    temperatures[i].setText(String.format("%.2f ", temperature) + (char) 0x00B0 + units);

                    String iconUrl = "https://openweathermap.org/img/w/" + day.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png";
                    Picasso.get().load(iconUrl).into(icons[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        days = new TextView[]{view.findViewById(R.id.day1), view.findViewById(R.id.day2), view.findViewById(R.id.day3), view.findViewById(R.id.day4), view.findViewById(R.id.day5)};
        temperatures = new TextView[]{view.findViewById(R.id.temperature1), view.findViewById(R.id.temperature2), view.findViewById(R.id.temperature3), view.findViewById(R.id.temperature4), view.findViewById(R.id.temperature5)};
        icons = new ImageView[]{view.findViewById(R.id.day1_icon), view.findViewById(R.id.day2_icon), view.findViewById(R.id.day3_icon), view.findViewById(R.id.day4_icon), view.findViewById(R.id.day5_icon)};
        settingsManager = new SettingsManager(getContext());
        getForecast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }
}