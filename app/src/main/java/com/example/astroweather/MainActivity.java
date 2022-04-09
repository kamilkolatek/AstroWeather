package com.example.astroweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.astroweather.fragments.BasicInfoFragment;
import com.example.astroweather.fragments.ExtendedInfoFragment;
import com.example.astroweather.fragments.ForecastFragment;
import com.example.astroweather.fragments.MoonFragment;
import com.example.astroweather.fragments.SunFragment;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Thread timeUpdater, dataUpdater;
    private SunFragment sunFragment;
    private MoonFragment moonFragment;
    private BasicInfoFragment basicInfoFragment;
    private ExtendedInfoFragment extendedInfoFragment;
    private ForecastFragment forecastFragment;
    private SettingsManager settingsManager;
    int refreshTime;

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private class TimeUpdater extends Thread {
        public void run() {
            try {
                while (!timeUpdater.isInterrupted()) {
                    runOnUiThread(() -> {
                        Calendar calendar = Calendar.getInstance();
                        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        String time = timeFormat.format(calendar.getTimeInMillis());
                        TextView currentTime = findViewById(R.id.current_time);
                        currentTime.setText("Current time: " + time);
                    });
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Log.i("custom_log", "Thread has been interrupted");
            }
        }
    }

    private class DataUpdater extends Thread {
        public void run() {
            try {
                while (!dataUpdater.isInterrupted()) {
                    Thread.sleep(1000 * 60 * refreshTime);
                    runOnUiThread(() -> {
                        sunFragment.getSunInfo();
                        moonFragment.getMoonInfo();
                        if(isConnected()) {
                            ArrayList<String> cities = new ArrayList<>(settingsManager.getCitiesList());
                            Thread thread = new Thread(() -> {
                                try {
                                    for (String city : cities)
                                        settingsManager.updateWeatherData(city, cities);
                                } catch (IOException | JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                            thread.start();
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        basicInfoFragment.getBasicInfo();
                        extendedInfoFragment.getExtendedInfo();
                        forecastFragment.getForecast();
                    });
                }
            } catch (InterruptedException e) {
                Log.i("custom_log", "Thread has been interrupted");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isConnected())
            Toast.makeText(getApplicationContext(),"Weather information may not be accurate. To ensure that information are actual you should connect to Internet.", Toast.LENGTH_LONG).show();

        settingsManager = new SettingsManager(this);

        Button settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        double latitude = settingsManager.getCurrentLatitude();
        double longitude = settingsManager.getCurrentLongitude();
        refreshTime = settingsManager.getRefreshTime();

        TextView latitudeValue = findViewById(R.id.latitude);
        latitudeValue.setText("lat: " + latitude);
        TextView longitudeValue = findViewById(R.id.longitude);
        longitudeValue.setText("lon: " + longitude);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        if(viewPager != null) {
            ArrayList<Fragment> fragments = new ArrayList<>();
            fragments.add(new SunFragment());
            fragments.add(new MoonFragment());
            fragments.add(new BasicInfoFragment());
            fragments.add(new ExtendedInfoFragment());
            fragments.add(new ForecastFragment());
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragments);
            viewPager.setOffscreenPageLimit(5);
            viewPager.setAdapter(viewPagerAdapter);
            sunFragment = (SunFragment)viewPagerAdapter.createFragment(0);
            moonFragment = (MoonFragment)viewPagerAdapter.createFragment(1);
            basicInfoFragment = (BasicInfoFragment)viewPagerAdapter.createFragment(2);
            extendedInfoFragment = (ExtendedInfoFragment)viewPagerAdapter.createFragment(3);
            forecastFragment = (ForecastFragment)viewPagerAdapter.createFragment(4);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        timeUpdater = new TimeUpdater();
        timeUpdater.start();
        dataUpdater = new DataUpdater();
        dataUpdater.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        timeUpdater.interrupt();
        dataUpdater.interrupt();
    }
}


