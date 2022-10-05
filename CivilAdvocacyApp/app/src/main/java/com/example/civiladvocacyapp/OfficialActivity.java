package com.example.civiladvocacyapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
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

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "OfficialActivity: ";
    private Official o;
    private TextView officialAddressBar;
    private TextView office, officialName, partyName, officialAddress,
            officialPhone, officialEmail, officialWebsite;
    private TextView address, phone, email, website;
    private ImageView officialPhoto, partyImage;
    private ImageView fbButton, twtButton, ytButton;
    private ScrollView main;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        //Action Bar
        setTitle("Civil Advocacy");
        setTitleColor(Color.WHITE);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_color)));

        //Get the components
        officialAddressBar = findViewById(R.id.officialAddressBar);
        office = findViewById(R.id.office);
        officialName = findViewById(R.id.officialName);
        partyName = findViewById(R.id.partyName);
        officialAddress = findViewById(R.id.officialAddress);
        officialPhone = findViewById(R.id.officialPhone);
        officialEmail = findViewById(R.id.officialEmail);
        officialWebsite = findViewById(R.id.officialWebsite);
        officialPhoto = findViewById(R.id.officialImage);
        partyImage = findViewById(R.id.partyImage);
        fbButton = findViewById(R.id.facebook);
        twtButton = findViewById(R.id.twitter);
        ytButton = findViewById(R.id.youtube);

        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);

        main = findViewById(R.id.main);



        //Get Intent
        Intent intent = getIntent();
        if (intent.hasExtra("OFFICIAL")) {
            o = (Official)intent.getSerializableExtra("OFFICIAL");
            if (o != null) {
                office.setText(o.getOfficeName());
                officialName.setText(o.getOfficialName());
                partyName.setText("(" + o.getParty() + ")");
                officialAddress.setText(o.getAddress());
                officialPhone.setText(o.getPhone());
                officialEmail.setText(o.getEmail());
                officialWebsite.setText(o.getWebsite());

                officialAddress.setPaintFlags(officialAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                officialPhone.setPaintFlags(officialPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                officialEmail.setPaintFlags(officialEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                officialWebsite.setPaintFlags(officialWebsite.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


                if (officialAddress.getText().toString().equals("")) {
                    address.setVisibility(View.GONE);
                    officialAddress.setVisibility(View.GONE);
                }
                if (officialPhone.getText().toString().equals("")) {
                    phone.setVisibility(View.GONE);
                    officialPhone.setVisibility(View.GONE);
                }
                if (officialEmail.getText().toString().equals("")) {
                    email.setVisibility(View.GONE);
                    officialEmail.setVisibility(View.GONE);
                }
                if (officialWebsite.getText().toString().equals("")) {
                    website.setVisibility(View.GONE);
                    officialWebsite.setVisibility(View.GONE);
                }
                if (o.getFacebook().equals("")) {
                    fbButton.setVisibility(View.GONE);
                }
                if (o.getTwitter().equals("")) {
                    twtButton.setVisibility(View.GONE);
                }
                if (o.getYoutube().equals("")) {
                    ytButton.setVisibility(View.GONE);
                }

                if(!checkNetwork()) {
                    officialPhoto.setImageResource(R.drawable.brokenimage);
                }
                else if (!o.getPhoto().equals("")) {

                    Picasso picasso = Picasso.get();
                    picasso.setLoggingEnabled(true);
                    picasso.load(o.getPhoto())
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(officialPhoto);
                }
                else {
                    officialPhoto.setImageResource(R.drawable.missing);
                }

                if (o.getParty().equals("Democratic Party")) {
                    main.setBackgroundColor(Color.BLUE);
                    partyImage.setImageResource(R.drawable.dem_logo);
                    partyImage.bringToFront();

                } else if (o.getParty().equals("Republican Party")) {
                    main.setBackgroundColor(Color.RED);
                    partyImage.setImageResource(R.drawable.rep_logo);
                    partyImage.bringToFront();
                }
            }
        }

        if (intent.hasExtra("LOCATION")) {
            String loc = intent.getStringExtra("LOCATION");
            if (loc != null) {
                officialAddressBar.setText(loc);
            }
        }

    }

    public void photoClicked(View v) {
        if(!o.getPhoto().equals("")) {
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            intent.putExtra("OFFICIAL",  o);
            intent.putExtra("LOCATION", officialAddressBar.getText().toString());
            setResult(RESULT_OK, intent);
            startActivity(intent);
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
                makeErrorAlert("No Application found that handles ACTION_VIEW intents");
            }

        } else if (o.getParty().equals("Republican Party")) {
            String url = "https://www.gop.gov"; //.com is not available at the moment
            i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(i);
            } else {
                makeErrorAlert("No Application found that handles ACTION_VIEW intents");
            }
        }
    }

    public void addressClicked(View v ){
        String address = o.getAddress();

        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (geo) intents");
        }

    }

    public void phoneClicked(View v ){
        String number = o.getPhone();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_DIAL (tel) intents");
        }
    }

    public void emailClicked(View v ){
        String[] addresses = new String[]{o.getEmail()};


        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, addresses);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 111);
        } else {
            makeErrorAlert("No Application found that handles SENDTO (mailto) intents");
        }
    }

    public void websiteClicked(View v ){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(o.getWebsite()));
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW intents");
        }
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + o.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }
            else {
                //older versions of fb app
                urlToUse = "fb://page/" + o.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }

        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = o.getTwitter();
        try { // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) { // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void youTubeClicked(View v) {
        String name = o.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
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


}