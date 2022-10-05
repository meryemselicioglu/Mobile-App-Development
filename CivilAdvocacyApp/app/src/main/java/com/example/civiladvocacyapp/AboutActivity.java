package com.example.civiladvocacyapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = "AboutActivity: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Log.d(TAG, "onCreate");

        //Action Bar
        setTitle("Civil Advocacy");
        setTitleColor(Color.WHITE);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_color)));
    }

    public void websiteClicked(View v ){
        String address = "https://developers.google.com/civic-information/";
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        } else {
            makeErrorAlert();
        }
    }

    private void makeErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No Application found that handles ACTION_VIEW intents");
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}