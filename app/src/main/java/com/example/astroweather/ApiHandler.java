package com.example.astroweather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHandler {
    private JSONObject sendRequest(String request) throws IOException, JSONException {
        URL url = new URL(request);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

            String jsonString = sb.toString();
            return new JSONObject(jsonString);
        } finally {
            urlConnection.disconnect();
        }
    }

    public JSONObject getWeatherData(String cityName) throws IOException, JSONException {
        return sendRequest("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=5e248eb7a539904c26f983729490715a");
    }

    public JSONObject getForecastData(double latitude, double longitude) throws IOException, JSONException {
        return sendRequest("https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=current,minutely,hourly,alerts&units=metric&appid=5e248eb7a539904c26f983729490715a");
    }
}
