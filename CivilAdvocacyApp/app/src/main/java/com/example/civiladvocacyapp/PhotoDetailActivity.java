package com.example.civiladvocacyapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {
    private TextView photoAddressBar;
    private TextView officeDetail, officialNameDetail;
    private ImageView officialImageDetail, partyImageDetail;
    private ScrollView mainDetail;
    private Official o;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        //Action Bar
        setTitle("Civil Advocacy");
        setTitleColor(Color.WHITE);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_color)));

        //Get the components
        photoAddressBar = findViewById(R.id.photoAddressBar);
        officeDetail = findViewById(R.id.officeDetail);
        officialNameDetail = findViewById(R.id.officialNameDetail);
        officialImageDetail = findViewById(R.id.officialImageDetail);
        partyImageDetail = findViewById(R.id.partyImageDetail);
        mainDetail = findViewById(R.id.mainDetail);

        //Get intent
        Intent intent = getIntent();
        if (intent.hasExtra("OFFICIAL")) {
            o = (Official) intent.getSerializableExtra("OFFICIAL");
            if (o != null) {
                officeDetail.setText(o.getOfficeName());
                officialNameDetail.setText(o.getOfficialName());

                if(!checkNetwork()) {
                    officialImageDetail.setImageResource(R.drawable.brokenimage);
                }
                else {
                    Picasso.get().load(o.getPhoto())
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(officialImageDetail);
                }

                if (o.getParty().equals("Democratic Party")) {
                    mainDetail.setBackgroundColor(Color.BLUE);
                    partyImageDetail.setImageResource(R.drawable.dem_logo);
                    partyImageDetail.bringToFront();

                } else if (o.getParty().equals("Republican Party")) {
                    mainDetail.setBackgroundColor(Color.RED);
                    partyImageDetail.setImageResource(R.drawable.rep_logo);
                    partyImageDetail.bringToFront();
                }
            }
        }

        if (intent.hasExtra("LOCATION")) {
            String loc = intent.getStringExtra("LOCATION");
            if (loc != null) {
                photoAddressBar.setText(loc);
            }
        }
    }

    public void partyLogoClicked(View v) {
        Intent i;
        if (o.getParty().equals("Democratic Party")) {
            String url = "https://democrats.org";
            i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(i);
            } else {
                makeErrorAlert();
            }

        } else if (o.getParty().equals("Republican Party")) {
            String url = "https://www.gop.gov"; //.com is not available at the moment
            i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(i);
            } else {
                makeErrorAlert();
            }
        }
    }
    public boolean checkNetwork() {
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
            return false;
        }
        else
            return true;
    }

    private void makeErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No Application found that handles ACTION_VIEW intents");
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}