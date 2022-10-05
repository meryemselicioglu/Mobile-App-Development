package com.example.openweather;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;


public class DayRecord implements Serializable {

    private String dt;
    private final String tempDaylight;
    private final String tempMorning;
    private final String tempEvening;
    private final String tempNight;
    private final String tempMax;
    private final String tempMin;
    private String weatherDescr;
    private String weatherIconCode;
    private final String pop;
    private final String uvi;

    public DayRecord(String dt, String tempDaylight, String tempMorning, String tempEvening,
                     String tempNight, String tempMax, String tempMin, String weatherDescr,
                     String weatherIcon, String pop, String uvi) {
        this.dt = dt;
        this.tempDaylight = tempDaylight;
        this.tempMorning = tempMorning;
        this.tempEvening = tempEvening;
        this.tempNight = tempNight;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.weatherDescr = weatherDescr;
        this.weatherIconCode = weatherIcon;
        this.pop = pop;
        this.uvi = uvi;
    }

    public String getDt() {
        return dt;
    }

    public String getTempDaylight() {
        return tempDaylight;
    }

    public String getTempMorning() {
        return tempMorning;
    }

    public String getTempEvening() {
        return tempEvening;
    }

    public String getTempNight() {
        return tempNight;
    }

    public String getTempMax() {
        return tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public String getWeatherDescr() {
        char[] charArray = weatherDescr.toCharArray();
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
        weatherDescr = String.valueOf(charArray);
        return weatherDescr;
    }

    public String getWeatherIcon() { return "_"+ weatherIconCode; }

    public String getPop() {
        return pop;
    }

    public String getUvi() {
        return uvi;
    }
}
