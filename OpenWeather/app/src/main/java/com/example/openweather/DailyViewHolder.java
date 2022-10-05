package com.example.openweather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class DailyViewHolder extends RecyclerView.ViewHolder {

    TextView dt;
    TextView tempDaylight;
    TextView tempMorning;
    TextView tempEvening;
    TextView tempNight;
    TextView tempMax;
    TextView tempMin;
    TextView weatherDescr;
    ImageView weatherIcon;
    TextView pop;
    TextView uvi;

    public DailyViewHolder(View view) {
        super(view);
        dt = view.findViewById(R.id.date);
        tempMorning = view.findViewById(R.id.dailyMorningTemp);
        tempDaylight = view.findViewById(R.id.dailyDaytimeTemp);
        tempEvening = view.findViewById(R.id.dailyEveningTemp);
        tempNight = view.findViewById(R.id.dailyNightTemp);
        tempMax = view.findViewById(R.id.dailyMaxTemp);
        tempMin = view.findViewById(R.id.dailyMinTemp);
        weatherDescr = view.findViewById(R.id.dailyWeatherDesc);
        weatherIcon = view.findViewById(R.id.dailyWeatherIcon);
        pop = view.findViewById(R.id.dailyPrecipitation);
        uvi = view.findViewById(R.id.dailyUvIndex);
    }
}
