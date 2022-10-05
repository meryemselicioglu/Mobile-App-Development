package com.example.openweather;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyAdapter extends RecyclerView.Adapter<DailyViewHolder> {
    private static final String TAG = "DailyAdapter";
    private final List<DayRecord> dayRecordList;
    private final DailyForecast dailyAct;
    private String unit;

    public DailyAdapter(List<DayRecord> dayRecordList, DailyForecast df){
        this.dayRecordList = dayRecordList;
        dailyAct = df;
        Log.d(TAG, "OnCreateViewHolder: MAKING NEW My ViewHolder");
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "OnCreateViewHolder: MAKING NEW My ViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_recycler_view, parent, false);
        unit = dailyAct.fahrenheit ? "F" : "C";
        Log.i(TAG,"Unit:" + unit);
        return new DailyViewHolder(itemView);

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER DailyRecords " + position);
        DayRecord dayRecord = dayRecordList.get(position);

        holder.pop.setText(String.format("(%.0f%% precip.)", Double.parseDouble(dayRecord.getPop())));

        holder.dt.setText(getDate(dayRecord.getDt()));
        int iconResId = dailyAct.getResources().getIdentifier(dayRecord.getWeatherIcon(),
                "drawable", dailyAct.getPackageName());
        holder.weatherIcon.setImageResource(iconResId);
        holder.weatherDescr.setText(dayRecord.getWeatherDescr());
        holder.uvi.setText(String.format("UV Index: %.0f", Double.parseDouble(dayRecord.getUvi())));
        holder.tempMin.setText(String.format("%.0f°" + unit, Double.parseDouble(dayRecord.getTempMin())));
        holder.tempMax.setText(String.format("%.0f°" + unit + "/", Double.parseDouble(dayRecord.getTempMax())));
        holder.tempNight.setText(String.format("%.0f°" + unit, Double.parseDouble(dayRecord.getTempNight())));
        holder.tempEvening.setText(String.format("%.0f°" + unit, Double.parseDouble(dayRecord.getTempEvening())));
        holder.tempDaylight.setText(String.format("%.0f°" + unit, Double.parseDouble(dayRecord.getTempDaylight())));
        holder.tempMorning.setText(String.format("%.0f°" + unit, Double.parseDouble(dayRecord.getTempMorning())));
        Log.i (TAG,"dayRecord" + dayRecord.getTempMin() + dayRecord.getTempMax() + dayRecord.getTempNight());
    }

    private String getDate(String dt) {
        SimpleDateFormat tf = new SimpleDateFormat("EEE, MM/dd");
        return tf.format(Long.parseLong(dt)*1000);
    }
    @Override
    public int getItemCount() {
        return dayRecordList.size();
    }

}
