package com.example.civiladvocacyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity: ";

    private RecyclerView recyclerView;
    private OfficialAdapter oAdapter;
    private final List<Official> officialList = new ArrayList<>();
    private Downloader civicDownload;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    private TextView usrLocation;
    private String locationString = "Unspecified location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, " OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Action Bar
        setTitle("Know Your Government");
        setTitleColor(Color.WHITE);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_color)));

        //Recycler View
        recyclerView = findViewById(R.id.recycler);
        oAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(oAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        civicDownload = new Downloader(this);
        usrLocation = findViewById(R.id.addressBar);
        usrLocation.bringToFront();

        //check internet connection -> determine location -> start Civic Info Runnable
        if(checkNetwork()) {
            Log.d("TAG", "Network result:" + checkNetwork());
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            determineLocation();
        }

    }
    @Override
    public void onResume(){ //checks internet connection when resumed
        Log.d(TAG, " OnResume");
        super.onResume();
        checkNetwork();
    }

    @Override
    public void onClick(View v) { //opens Official Activity
        Log.d(TAG, "Official clicked ") ;

        int pos = recyclerView.getChildLayoutPosition(v);
        Official o  = officialList.get(pos);

        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("OFFICIAL",  o);
        intent.putExtra("LOCATION",  usrLocation.getText().toString());

        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Options item selected: " + item.getItemId());

        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.locSearch) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Address");

            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(checkNetwork()) {                            //check internet connection
                        if (!et.getText().toString().isEmpty()) {   //check if input is empty
                            String loc = getPlace(et.getText().toString());
                            if (loc != null) {                      //check if valid address
                                loadCivicInfo(loc);
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Couldn't find the address.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Address field cannot be empty.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void determineLocation() { //finds user location with fusedLocation
        Log.d("TAG", "determineLocation: ");
        if (checkPermission()) {
            Log.d("TAG", "locPerm " + checkPermission());
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        Log.d("TAG", "Fused Succeed");
                        if (location != null) {
                            Log.d(TAG, "determineLocation: " + location.getLatitude() +
                                    ", " + location.getLatitude());
                            locationString = getPlace(location);
                            loadCivicInfo(locationString);
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Log.d("TAG", "Fused Failed");
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private String getPlace(Location loc) { //determines address based on lat and lon
        Log.d(TAG, " getPlace with current location");
        StringBuilder sb = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;

            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            addresses = geocoder.getFromLocation(lat, lon, 10);

            Address ad = addresses.get(0);
            String a = String.format("%s %s %s %s %s ",
                    (ad.getSubThoroughfare() == null ? "" : ad.getSubThoroughfare()),
                    (ad.getThoroughfare() == null ? "" : ad.getThoroughfare()),
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
                    (ad.getPostalCode() == null ? "" : ad.getPostalCode()));
            sb.append(a);

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getPlace(String loc) { //determines location based on user input address
        Log.d(TAG, " getPlace with new location");
        StringBuilder sb = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocationName(loc, 10);

            if(addresses.size()== 0) { return null; }

            Address ad = addresses.get(0);
            String a = String.format("%s %s %s %s %s ",
                    (ad.getSubThoroughfare() == null ? "" : ad.getSubThoroughfare()),
                    (ad.getThoroughfare() == null ? "" : ad.getThoroughfare()),
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
                    (ad.getPostalCode() == null ? "" : ad.getPostalCode()));
            sb.append(a);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void loadCivicInfo(String loc) { //start Civic Info Runnable
        civicDownload.setURL(loc);

        Thread t = new Thread(civicDownload);
        t.start();
        try { t.join(); } //Waits the thread to finish
        catch (Exception e) { System.out.println(e);}

        usrLocation.setText(civicDownload.getLocation());

    }

    @SuppressLint("NotifyDataSetChanged")
    public void returnOfficial(ArrayList<Official> oList) { //load data from Civic Info Runnable
        officialList.clear();
        oAdapter.notifyDataSetChanged();
        officialList.addAll(oList);
        oAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    usrLocation.setText(R.string.unspec_loc);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void downloadFailed() {
        officialList.clear();
        oAdapter.notifyDataSetChanged();
        usrLocation.setText(R.string.no_data);
    }


    public boolean checkNetwork() { //check internet connection
        Log.d("TAG", "Checking network:");
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an Internet connection");
            AlertDialog dialog = builder.create();
            dialog.show();

            downloadFailed();

            return false;
        }
        else
            return true;
    }
    private boolean checkPermission() { //check permission for accessing location
        Log.d(TAG, "checkMyLocPermission: ");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }
}