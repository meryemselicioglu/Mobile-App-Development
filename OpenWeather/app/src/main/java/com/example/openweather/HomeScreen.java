package com.example.openweather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class HomeScreen extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public boolean fahrenheit = true;
    double lat = 41.8675766, lon = -87.616232;

    String loc = "Chicago, Illinois";
    TextView location;

    private List<HourRecord> hourList = new ArrayList<>();
    private List<DayRecord> dayList = new ArrayList<>();
    private SharedPreferences.Editor editor;
    private HourlyAdapter hAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.d(TAG, "onCreate:");

        location = findViewById(R.id.location);
        location.bringToFront();

        RecyclerView recyclerView = findViewById(R.id.hourlyRecycler);
        hAdapter = new HourlyAdapter(hourList, this);
        recyclerView.setAdapter(hAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (!sharedPref.contains("FAHRENHEIT")) {
            editor.putBoolean("FAHRENHEIT", true);
            editor.apply();
        }
        fahrenheit = sharedPref.getBoolean("FAHRENHEIT", true);
        doDownload();
    }

    @Override
    public void onResume(){ //checks internet connection when resumed
        Log.d(TAG, " OnResume");
        super.onResume();
        doNetworkCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Options item selected: " + item.getItemId());

        if (item.getItemId() == R.id.unit) {
            if  (item.getIcon().getConstantState().equals(getResources().
                    getDrawable(R.drawable.units_f).getConstantState())) {
                fahrenheit = false;
                editor.putBoolean("FAHRENHEIT", false);
                item.setIcon(R.drawable.units_c);
            } else {
                fahrenheit = true;
                editor.putBoolean("FAHRENHEIT", true);
                item.setIcon(R.drawable.units_f);
            }
            editor.apply();
            doDownload();
            return true;

        } else if (item.getItemId() == R.id.opt_daily_forecast) {
            Intent intent = new Intent(this, DailyForecast.class);
            intent.putExtra("dayList", (ArrayList<DayRecord>) dayList );
            intent.putExtra("LOCATION", location.getText().toString());
            intent.putExtra("FAHRENHEIT", fahrenheit);
            startActivity(intent);
            return true;

        } else if (item.getItemId() == R.id.opt_location) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter a Location");
            builder.setMessage("For US locations, enter as 'City', or 'City, State' \n\n" +
                    "For international locations enter as 'City, Country");
            final EditText locInput = new EditText(this);
            locInput.setInputType(InputType.TYPE_CLASS_TEXT);
            locInput.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(locInput);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(doNetworkCheck()) {
                        if(!locInput.getText().toString().isEmpty()){
                            loc = getPlace(locInput.getText().toString());
                            if (loc != null) {
                                getLatLon(loc);
                                doDownload();
                            } else {
                                Toast.makeText(HomeScreen.this,
                                        "Couldn't find the address.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(HomeScreen.this,
                                    "Address field cannot be empty.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
            }});

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;

        } else {
            Log.d(TAG, "onOptionsItemSelected: Unexpected selection: " + item.getTitle());
            return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void doDownload() {
        if (!doNetworkCheck()){
            location.setText(getString(R.string.no_internet));
            return;
        }
        //OneCall.downloadWeather(this, Objects.requireNonNull(getLatLon(loc)), fahrenheit);
        OneCall.downloadWeather(this, Objects.requireNonNull(getLatLon(loc)), fahrenheit);
    }


    private double[] getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address = geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty()) {
                // Nothing returned!
                return null;
            }
            displayAddress(address);
            lat = address.get(0).getLatitude();
            lon = address.get(0).getLongitude();
            return new double[] {lat, lon};
        } catch (IOException e) {
            return null; }
    }

    private String getPlace(String loc){
        Log.d(TAG, " getPlace with new location");
        StringBuilder sb = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocationName(loc, 10);

            if(addresses.size()== 0) { return null; }

            Address ad = addresses.get(0);
            String a = String.format("%s, %s",
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea()));
            sb.append(a);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void displayAddress(List<Address> addresses) {
        if(addresses.size() == 0){
            ((TextView) findViewById(R.id.location)).setText(R.string.no_data);
            return;
        }
        Address one = addresses.get(0);
        // check if it's null or not

        String city = one.getLocality() == null ? "" : one.getLocality();
        String state = one.getAdminArea() == null ? one.getCountryName() : one.getAdminArea();

        loc = city + ", " + state;
        ((TextView) findViewById(R.id.location)).setText(city + ", " + state);
    }

    // Performing Network Check
    private boolean doNetworkCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Toast.makeText(this, "Cannot access Connectivity Manager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && networkInfo.isConnectedOrConnecting());
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateData(Weather weather) {
        if (weather == null) {
            Toast.makeText(this, "Please Enter a Valid City Name", Toast.LENGTH_SHORT).show();
        }
        else {
            hourList.clear();
            hourList.addAll(weather.getHourRecords());
            hAdapter.notifyDataSetChanged();
            dayList.clear();
            dayList = weather.getDayRecords();

            String unit = fahrenheit ? "F" : "C";

            TextView location = findViewById(R.id.location);
            location.setText(loc);

            LocalDateTime ldt = LocalDateTime.ofEpochSecond(
                    Long.parseLong(weather.getCurrDt()) + (weather.getTimezone_offset()),
                    0, ZoneOffset.UTC);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd h:mm a, yyyy",
                    Locale.getDefault());
            String formattedTimeString = ldt.format(dtf);
            TextView date = findViewById(R.id.dateTime);
            date.setText(formattedTimeString);

            TextView feelsLike = findViewById(R.id.feelsLike);
            feelsLike.setText(String.format("Feels Like %.0f°" + unit, Double.parseDouble(weather.getCurrFeelsLike())));

            TextView currTemp = findViewById(R.id.currTemp);
            currTemp.setText(String.format(Locale.getDefault(), "%.0f°" + unit, Double.parseDouble(weather.getCurrTemp())));
            Log.d(TAG, "currTemp: " + currTemp.getText());

            TextView description = findViewById(R.id.weatherDescr);
            description.setText(String.format(Locale.getDefault(), "%s (%.0f%% clouds)",
                    weather.getCurrWeatherDescription(), Double.parseDouble(weather.getCurrClouds())));

            TextView wind = findViewById(R.id.wind);
            wind.setText(String.format("Wind: %s at %.0f " + (fahrenheit ? "mph" : "mps"),
                    getDirection(weather.getCurrWindDirection()),
                    Double.parseDouble(weather.getCurrWindSpeed())));

            TextView uv = findViewById(R.id.uvIndex);
            uv.setText(String.format(Locale.getDefault(), "UV Index: %.0f ",
                    Double.parseDouble(weather.getCurrUvi())));

            TextView visibility = findViewById(R.id.visibility);
            visibility.setText(String.format(Locale.getDefault(), "Visibility: %.0f " +
                    (fahrenheit ? "mi" : "km"), Double.parseDouble(weather.getCurrVisibility())));

            TextView humid = findViewById(R.id.humidity);
            humid.setText(String.format(Locale.getDefault(), "Humidity: %.0f%%",
                    Double.parseDouble(weather.getCurrHumidity())));

            TextView morn = findViewById(R.id.morningTemp);
            morn.setText(String.format("%.0f°" + unit,
                    Double.parseDouble(weather.getDayRecords().get(0).getTempMorning())));

            TextView dayTime = findViewById(R.id.daytimeTemp);
            dayTime.setText(String.format("%.0f°" + unit,
                    Double.parseDouble(weather.getDayRecords().get(0).getTempDaylight())));

            TextView evening = findViewById(R.id.eveningTemp);
            evening.setText(String.format("%.0f°" + unit,
                    Double.parseDouble(weather.getDayRecords().get(0).getTempEvening())));

            TextView night = findViewById(R.id.nightTemp);
            night.setText(String.format("%.0f°" + unit,
                    Double.parseDouble(weather.getDayRecords().get(0).getTempNight())));

            int iconResId = this.getResources().getIdentifier(weather.getCurrWeatherIcon(),
                    "drawable", this.getPackageName());
            ImageView icon = findViewById(R.id.weatherIcon);
            icon.setImageResource(iconResId);

            LocalDateTime ldtSunrise = LocalDateTime.ofEpochSecond(
                    Long.parseLong(weather.getCurrSunrise()) + (weather.getTimezone_offset()),
                    0, ZoneOffset.UTC);
            DateTimeFormatter dtfSunrise = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());

            TextView sunrise = findViewById(R.id.sunrise);
            sunrise.setText("Sunrise: " + ldtSunrise.format(dtfSunrise));

            LocalDateTime ldtSunset = LocalDateTime.ofEpochSecond(
                    Long.parseLong(weather.getCurrSunset()) + (weather.getTimezone_offset()),
                    0, ZoneOffset.UTC);
            DateTimeFormatter dtfSunset = DateTimeFormatter.ofPattern("h:mm a",
                    Locale.getDefault());

            TextView sunset = findViewById(R.id.sunset);
            sunset.setText("Sunset: " + ldtSunset.format(dtfSunset));
        }
    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }

}
