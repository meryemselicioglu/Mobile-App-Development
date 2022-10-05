package com.example.openweather;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OneCall {

    private static final String weatherUrl = "https://api.openweathermap.org/data/2.5/onecall?" +
             "lang=en&exclude=minutely";
    private static final String myAPIkey = "ddde61d90504439074a2e22adf99556e";
    private static final String TAG = "WeatherLoaderRunnable";

    private static HomeScreen homeScreen;
    private static Weather weatherObj;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void downloadWeather(HomeScreen homeScreenIn,
                                       double[] latLon, boolean fahrenheit) {
        homeScreen = homeScreenIn;

        RequestQueue queue = Volley.newRequestQueue(homeScreen);

        Uri.Builder buildURL = Uri.parse(weatherUrl).buildUpon();
        buildURL.appendQueryParameter("lat", String.valueOf(latLon[0]));
        buildURL.appendQueryParameter("lon", String.valueOf(latLon[1]));
        buildURL.appendQueryParameter("appid", myAPIkey);
        buildURL.appendQueryParameter("units", (fahrenheit ? "imperial" : "metric"));
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "urlToUse: " + urlToUse);

        Response.Listener<JSONObject> listener =
                response -> parseJSON(response.toString());

        Response.ErrorListener error = error1 ->
                homeScreen.updateData(null);
            Log.d(TAG, "getSourceData: ");


        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        queue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void parseJSON(String s) {
        try{
            JSONObject jObjMain = new JSONObject(s);
            ArrayList<DayRecord> daysList = new ArrayList<>();
            ArrayList<HourRecord> hoursList = new ArrayList<>();

            Double lat = jObjMain.getDouble("lat");
            Double lon = jObjMain.getDouble("lon");
            String timezone = jObjMain.getString("timezone");
            Integer timezone_offset = jObjMain.getInt("timezone_offset");
            // current section
            JSONObject jCurrent = jObjMain.getJSONObject("current");
            String currDt = jCurrent.getString("dt");
            String currSunrise = jCurrent.getString("sunrise");
            String currSunset = jCurrent.getString("sunset");
            String currTemp = jCurrent.getString("temp");
            String currFeelsLike = jCurrent.getString("feels_like");
            String currPressure = jCurrent.getString("pressure");
            String currHumidity = jCurrent.getString("humidity");
            String currUvi = jCurrent.getString("uvi");
            String currClouds = jCurrent.getString("clouds");
            String currVisibility = jCurrent.getString("visibility");
            String currWindSpeed = jCurrent.getString("wind_speed");
            Double currWindDirection = jCurrent.getDouble("wind_deg");
            String currWindGust = "";
            if(jCurrent.has("wind_gust")) {
                currWindGust = jCurrent.getString("wind_gust"); }

            JSONArray currWeather = jCurrent.getJSONArray("weather");
            JSONObject jCurrWeather = (JSONObject) currWeather.get(0);
            String currDescription = jCurrWeather.getString("description");
            String currIcon = jCurrWeather.getString("icon");

            String currRain = "";
            if (jCurrent.has("rain")) {
                JSONObject jCurrRain = jCurrent.getJSONObject("rain");
                currRain = jCurrRain.getString("1h"); }

            String currSnow = "";
            if (jCurrent.has("snow")) {
                JSONObject jCurrSnow = jCurrent.getJSONObject("snow");
                currSnow = jCurrSnow.getString("1h"); }

            JSONArray hourly = jObjMain.getJSONArray("hourly");
            JSONArray daily = jObjMain.getJSONArray("daily");
            String hourlyDt = ""; String hourlyTemp = "";
            String hourlyDay = ""; String hourlyIcon = "";
            String hourlyDescription = "";

            for (int i = 0; i < 48; i++) {
                JSONObject jHourly = (JSONObject) hourly.get(i);
                hourlyDt = jHourly.getString("dt");
                hourlyTemp = jHourly.getString("temp");
                hourlyDay = jHourly.getString("dt");

                JSONArray aHourlyWeather = (JSONArray) jHourly.get("weather");
                JSONObject oHourlyWeather = (JSONObject) aHourlyWeather.get(0);
                hourlyDescription = oHourlyWeather.getString("description");
                hourlyIcon = oHourlyWeather.getString("icon");
                hoursList.add(new HourRecord(hourlyDt, hourlyTemp,
                        hourlyDescription, hourlyIcon, hourlyDay));
            }


            String dailyDt = "";  String dailyPop = ""; String dailyUvi = "";
            String dailyDay = ""; String dailyMin = ""; String dailyMax = "";
            String dailyMorning = ""; String dailyNight = ""; String dailyIcon = "";
            String dailyEvening = ""; String dailyDescription = "";

            for (int i = 0; i < 7; i++) {
                JSONObject jDaily = (JSONObject) daily.get(i);
                dailyDt = jDaily.getString("dt");
                dailyPop = jDaily.getString("pop");
                dailyUvi = jDaily.getString("uvi");

                JSONObject jDailyTemp = jDaily.getJSONObject("temp");
                dailyDay = jDailyTemp.getString("day");
                dailyMin = jDailyTemp.getString("min");
                dailyMax = jDailyTemp.getString("max");
                dailyNight = jDailyTemp.getString("night");
                dailyEvening = jDailyTemp.getString("eve");
                dailyMorning = jDailyTemp.getString("morn");

                JSONArray aDailyWeather = jDaily.getJSONArray("weather");
                JSONObject oDailyWeather = aDailyWeather.getJSONObject(0);
                dailyDescription = oDailyWeather.getString("description");
                dailyIcon = oDailyWeather.getString("icon");
                daysList.add(new DayRecord(dailyDt, dailyDay, dailyMorning, dailyEvening, dailyNight,
                        dailyMax, dailyMin, dailyDescription, dailyIcon, dailyPop, dailyUvi));
            }

            weatherObj = new Weather(lat, lon, timezone, timezone_offset, currDt, currSunrise,
                    currSunset, currTemp, currFeelsLike, currPressure, currHumidity, currUvi,
                    currClouds, currVisibility, currWindSpeed, currWindDirection, currWindGust,
                    currDescription, currIcon, currRain, currSnow, daysList, hoursList);

            homeScreen.updateData(weatherObj);
        }
        catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
