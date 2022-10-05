package com.example.civiladvocacyapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Downloader implements Runnable {
    private static final String TAG = "CivicInfoDownloader: ";
    private final MainActivity mainActivity;
    private String DATA_URL;

    private final ArrayList<Official> officialList;
    private String location;

    Downloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        officialList =  new ArrayList<>();
    }

    @Override
    public void run() {
        Log.d(TAG, "Run");

        officialList.clear();
        location = "Unspecified Location";

        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode()+ " url: " + DATA_URL);
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());
    }

    public void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }


        parseJSON(s);

        mainActivity.runOnUiThread(() -> {
            mainActivity.returnOfficial(officialList);
        });
    }

    private void parseJSON(String s) {
        try {
            JSONObject jMain = new JSONObject(s);

            //for address bar
            JSONObject jNormInput = jMain.getJSONObject("normalizedInput");
            String line = jNormInput.getString("line1");
            String city = jNormInput.getString("city");
            String state = jNormInput.getString("state");
            String zip = jNormInput.getString("zip");
            location = String.format("%s %s %s %s",
                    (line.equals("") ? "" : line + ", "),
                    (city.equals("") ? "" : city + ", "),
                    state, zip);

            //for officials
            JSONArray jOfficials = jMain.getJSONArray("officials");
            for (int i = 0; i < jOfficials.length(); i++) {
                JSONObject jOfficial = jOfficials.getJSONObject(i);

                String oName = jOfficial.getString("name");

                String oAddress = "";
                if(jOfficial.has("address")) {
                    JSONArray jAdd = jOfficial.getJSONArray("address");
                    JSONObject jAddress = jAdd.getJSONObject(0);
                    String oLine1 = "", oLine2 = "", oLine3 = "";
                    if(jAddress.has("line1")) {
                        oLine1 = jAddress.getString("line1");
                    }
                    if(jAddress.has("line2")) {
                        oLine2 = jAddress.getString("line2");
                    }
                    if(jAddress.has("line3")) {
                        oLine3 = jAddress.getString("line3");
                    }

                    String oCity = jAddress.getString("city");
                    String oState = jAddress.getString("state");
                    String oZip = jAddress.getString("zip");

                    oAddress = oLine1 + " " + oLine2 + " " + oLine3 +
                            oCity + ", " + oState + " " + oZip;
                }

                String oParty =  "Unknown";
                if(jOfficial.has("party")) {
                    oParty = jOfficial.getString("party");
                }

                String oPhone = "";
                if(jOfficial.has("phones")) {
                    JSONArray jPhone = jOfficial.getJSONArray("phones");
                    oPhone = jPhone.getString(0);
                }

                String oWeb = "";
                if(jOfficial.has("urls")) {
                    JSONArray jURLs = jOfficial.getJSONArray("urls");
                    oWeb = jURLs.getString(0);
                }
                String oEmail = "";
                if(jOfficial.has("emails")) {
                    JSONArray jEmail = jOfficial.getJSONArray("emails");
                    oEmail = jEmail.getString(0);
                }

                String oPhoto = "";
                if(jOfficial.has("photoUrl")) {
                    oPhoto = jOfficial.getString("photoUrl");
                }

                String facebookID = "", twitterID = "", youTubeID = "";
                if(jOfficial.has("channels")) {
                    JSONArray jCh = jOfficial.getJSONArray("channels");

                    for (int j = 0; j < jCh.length(); j++) {
                        JSONObject jChannel = jCh.getJSONObject(j);

                        String oType = jChannel.getString("type");
                        String oID = jChannel.getString("id");

                        switch (oType) {
                            case "Facebook":
                                facebookID = oID;
                                break;
                            case "Twitter":
                                twitterID = oID;
                                break;
                            case "YouTube":
                                youTubeID = oID;
                                break;
                        }
                    }
                }


                officialList.add(new Official( "", oName, oParty, oPhoto, oAddress, oPhone,
                        oEmail, oWeb, facebookID, twitterID, youTubeID ));
            }

            //for offices
            JSONArray jOffices = jMain.getJSONArray("offices");
            for (int i = 0; i < jOffices.length(); i++) {
                JSONObject jOffice = jOffices.getJSONObject(i);
                String officeName = jOffice.getString("name");

                JSONArray jIndices = jOffice.getJSONArray("officialIndices");
                for(int j =0; j< jIndices.length(); j++) {
                    int index = jIndices.getInt(j);
                    officialList.get(index).setOfficeName(officeName);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            Log.d(TAG, "null at " + DATA_URL);
            e.printStackTrace();
        }
    }

    public void setURL(String address) {
        DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyCbmyuAVyiYlgSk6sqMIGmzt-kGjGjs6q4&address="
                + address;
    }
    public String getLocation() { return location;}
}
