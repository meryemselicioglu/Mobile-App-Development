package com.example.openweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DailyForecast extends AppCompatActivity {

    private static final String TAG = "DailyForecastActivity";
    private List<DayRecord> dayRecordList;
    private DailyAdapter dAdapter;
    public boolean fahrenheit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        RecyclerView recyclerView = findViewById(R.id.dailyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        dayRecordList =  (ArrayList<DayRecord>) intent.getSerializableExtra("dayList");
        if (dayRecordList == null) {
            return;
        }else{
            if (dAdapter == null) {
                DailyAdapter dAdapter = new DailyAdapter(dayRecordList, this);
                recyclerView.setAdapter(dAdapter);
            } else{
                dAdapter.notifyDataSetChanged();
            }
        }

        if(intent.hasExtra("LOCATION")) {
            String location = intent.getStringExtra("LOCATION");
            setTitle(location);
            fahrenheit = intent.getBooleanExtra("FAHRENHEIT",true);
        }
    }
}