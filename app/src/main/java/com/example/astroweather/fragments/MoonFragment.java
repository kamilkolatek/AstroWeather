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

import static java.lang.Math.abs;

public class MoonFragment extends Fragment {
    private TextView moonrise_time_view, moonset_time_view, nearest_new_moon, nearest_full_moon, moon_phase_view, synodic_month_day_view;
    private SettingsManager settingsManager;

    public MoonFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void getMoonInfo() {
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
        AstroCalculator.MoonInfo moonInfo = calculator.getMoonInfo();

        AstroDateTime moonrise_time = moonInfo.getMoonrise();
        moonrise_time_view.setText(String.format("%02d:%02d", moonrise_time.getHour(), moonrise_time.getMinute()));

        AstroDateTime moonset_time = moonInfo.getMoonset();
        moonset_time_view.setText(String.format("%02d:%02d", moonset_time.getHour(), moonset_time.getMinute()));

        AstroDateTime new_moon = moonInfo.getNextNewMoon();
        nearest_new_moon.setText(String.format("%02d.%02d.%04d", new_moon.getDay(), new_moon.getMonth(), new_moon.getYear()));

        AstroDateTime full_moon = moonInfo.getNextFullMoon();
        nearest_full_moon.setText(String.format("%02d.%02d.%04d", full_moon.getDay(), full_moon.getMonth(), full_moon.getYear()));

        moon_phase_view.setText(String.format("%.2f%%", moonInfo.getIllumination() * 100));

        synodic_month_day_view.setText(Integer.toString(abs((int)moonInfo.getAge())));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moonrise_time_view = view.findViewById(R.id.moonrise_time);
        moonset_time_view = view.findViewById(R.id.moonset_time);
        nearest_new_moon = view.findViewById(R.id.new_moon);
        nearest_full_moon = view.findViewById(R.id.full_moon);
        moon_phase_view = view.findViewById(R.id.moon_phase);
        synodic_month_day_view = view.findViewById(R.id.synodic_month_day);
        settingsManager = new SettingsManager(getContext());
        getMoonInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moon, container, false);
    }
}