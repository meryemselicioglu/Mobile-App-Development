package com.example.openweather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class HourlyViewHolder extends RecyclerView.ViewHolder {

    TextView dt;
    TextView temp;
    TextView weatherDescr;
    ImageView weatherIcon;
    TextView day;

    HourlyViewHolder(View view) {
        super(view);
        dt = view.findViewById(R.id.time);
        temp = view.findViewById(R.id.hourlyTemp);
        weatherDescr = view.findViewById(R.id.hourlyDescription);
        weatherIcon = view.findViewById(R.id.hourlyWeatherIcon);
        day = view.findViewById(R.id.day);
    }
}
