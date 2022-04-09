package com.example.astroweather.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.astroweather.R;
import com.example.astroweather.SettingsManager;

import java.util.Calendar;

public class SunFragment extends Fragment {
    private TextView sunrise_time_view, sunrise_azimuth_view, sunset_time_view, sunset_azimuth_view, dusk_view, dawn_view;
    private SettingsManager settingsManager;

    public SunFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void getSunInfo() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        AstroDateTime dateTime = new AstroDateTime(year, month, day, hour, minute, second, 2, true);

        double latitude = settingsManager.getCurrentLatitude();
        double longitude = settingsManager.getCurrentLongitude();

        AstroCalculator.Location location = new AstroCalculator.Location(latitude, longitude);
        AstroCalculator calculator = new AstroCalculator(dateTime, location);
        AstroCalculator.SunInfo sunInfo = calculator.getSunInfo();

        AstroDateTime sunrise_time = sunInfo.getSunrise();
        sunrise_time_view.setText(String.format("%02d:%02d", sunrise_time.getHour(), sunrise_time.getMinute()));

        sunrise_azimuth_view.setText(String.format("%.4f", sunInfo.getAzimuthRise()) + (char) 0x00B0);

        AstroDateTime sunset_time = sunInfo.getSunset();
        sunset_time_view.setText(String.format("%02d:%02d", sunset_time.getHour(), sunset_time.getMinute()));

        sunset_azimuth_view.setText(String.format("%.4f", sunInfo.getAzimuthSet()) + (char) 0x00B0);

        AstroDateTime dusk = sunInfo.getTwilightEvening();
        dusk_view.setText(String.format("%02d:%02d", dusk.getHour(), dusk.getMinute()));

        AstroDateTime dawn = sunInfo.getTwilightMorning();
        dawn_view.setText(String.format("%02d:%02d", dawn.getHour(), dawn.getMinute()));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sunrise_time_view = view.findViewById(R.id.city_name);
        sunrise_azimuth_view = view.findViewById(R.id.temperature2);
        sunset_time_view = view.findViewById(R.id.temperature3);
        sunset_azimuth_view = view.findViewById(R.id.temperature4);
        dusk_view  = view.findViewById(R.id.temperature5);
        dawn_view = view.findViewById(R.id.clouds);
        settingsManager = new SettingsManager(getContext());
        getSunInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sun, container, false);
    }
}