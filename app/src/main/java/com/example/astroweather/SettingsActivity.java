package com.example.astroweather;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private Spinner refreshTimeSpinner, savedCitiesSpinner;
    private ArrayAdapter<String> dataAdapter;
    private ArrayList<String> cities;
    private SettingsManager settingsManager;

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void initRefreshTimeSpinner() {
        refreshTimeSpinner = findViewById(R.id.refresh_time_spinner);
        List<String> categories = new ArrayList<>();
        categories.add("1");
        categories.add("5");
        categories.add("10");
        categories.add("15");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refreshTimeSpinner.setAdapter(dataAdapter);
    }

    private void initSavedCitiesSpinner() {
        savedCitiesSpinner = findViewById(R.id.saved_cities_spinner);

        cities = new ArrayList<>(settingsManager.getCitiesList());

        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        savedCitiesSpinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsManager = new SettingsManager(this);
        initRefreshTimeSpinner();
        initSavedCitiesSpinner();

        Button addButton = findViewById(R.id.add_button);
        Button submitButton = findViewById(R.id.submit_button);
        Button removeButton = findViewById(R.id.remove_button);

        RadioGroup radioGroup = findViewById(R.id.radio);

        addButton.setOnClickListener(v -> {
            EditText cityName = findViewById(R.id.city_name);
            String city = cityName.getText().toString();

            if (city.equals("")) {
                Toast.makeText(getApplicationContext(), "Fill in the required fields", Toast.LENGTH_SHORT).show();
                return;
            } else if(!isConnected()) {
                Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

            Thread thread = new Thread(() -> {
                try {
                    settingsManager.updateWeatherData(city, cities);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataAdapter.notifyDataSetChanged();
        });

        submitButton.setOnClickListener(v -> {
            Object selectedCity = savedCitiesSpinner.getSelectedItem();
            if(selectedCity != null) {
                int refreshTime = Integer.parseInt(refreshTimeSpinner.getSelectedItem().toString());
                String currentCity = selectedCity.toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedId);
                String units = String.valueOf(radioButton.getText());

                settingsManager.setCurrentSettings(refreshTime, currentCity, units);
                try {
                    settingsManager.setCurrentCoord(currentCity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        });

        removeButton.setOnClickListener(v -> {
            if(savedCitiesSpinner.getSelectedItem() == null)
                return;

            String currentCity = savedCitiesSpinner.getSelectedItem().toString();
            settingsManager.deleteWeatherData(currentCity, cities);
            dataAdapter.notifyDataSetChanged();
        });
    }
}