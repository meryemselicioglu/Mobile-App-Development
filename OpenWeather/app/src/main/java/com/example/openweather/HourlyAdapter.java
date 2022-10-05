package com.example.openweather;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyViewHolder> {

    private static final String TAG = "HourlyAdapter";
    private final List<HourRecord> hourRecordList;
    private final HomeScreen homeScreenAct;
    public boolean hfahrenheit;

    public HourlyAdapter(List<HourRecord> hourRecordList, HomeScreen hs){
        this.hourRecordList = hourRecordList;
        homeScreenAct = hs;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "OnCreateViewHolder: MAKING NEW My ViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_recycler_view, parent, false);

        return new HourlyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER HourlyRecords " + position);
        HourRecord hourRecord = hourRecordList.get(position);
        hfahrenheit = homeScreenAct.fahrenheit;
        String unit = hfahrenheit ? "F" : "C";

        holder.temp.setText(String.format("%.0f°" + unit, Double.parseDouble(hourRecord.getTemp())));
        holder.dt.setText(getTime(hourRecord.getDt()));
        holder.day.setText(getDay(hourRecord.getDay()));
        holder.weatherDescr.setText(getCapDesc(hourRecord.getDescription()));

        int iconResId = homeScreenAct.getResources().getIdentifier(hourRecord.getIconCode(),
                "drawable", homeScreenAct.getPackageName());
        holder.weatherIcon.setImageResource(iconResId);
    }

    private String getTime(String dt) {
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
        return tf.format(Long.parseLong(dt)*1000);
    }

    private String getDay(String dt) {
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        return df.format(Long.parseLong(dt)*1000);
    }

    private String getCapDesc(String description){
        char[] charArray = description.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {
            if(Character.isLetter(charArray[i])) {
                if(foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            } else {
                foundSpace = true;
            }
        }
        return String.valueOf(charArray);
    }

    @Override
    public int getItemCount() {
        return hourRecordList.size();
    }

}
