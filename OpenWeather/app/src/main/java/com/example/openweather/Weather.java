package com.example.openweather;

import java.util.ArrayList;

public class Weather {
    private final Double lat;
    private final Double lon;
    private final String timezone;
    private final Integer timezone_offset;
    private final String currDt;
    private final String currSunrise;
    private final String currSunset;
    private final String currTemp;
    private final String currFeelsLike;
    private final String currPressure;
    private final String currHumidity;
    private final String currUvi;
    private final String currClouds;
    private final String currVisibility;
    private final String currWindSpeed;
    private final Double currWindDirection;
    private final String currWindGust;
    private final String currWeatherDescription;
    private final String currWeatherIcon;
    private final String currRain;
    private final String currSnow;
    private final ArrayList<DayRecord> dayRecords;
    private final ArrayList<HourRecord> hourRecords;


    public Weather(Double lat, Double lon, String timezone, Integer timezone_offset, String currDt,
                   String currSunrise, String currSunset, String currTemp, String currFeelsLike,
                   String currPressure, String currHumidity, String currUvi, String currClouds,
                   String currVisibility, String currWindSpeed, Double currWindDirection, String
                           currWindGust, String currWeatherDescription, String currWeatherIcon,
                   String currRain, String currSnow, ArrayList<DayRecord> dayRecords,
                   ArrayList<HourRecord> hourRecords)
    {
        this.lat = lat;
        this.lon = lon;
        this.timezone = timezone;
        this.timezone_offset = timezone_offset;
        this.currDt = currDt;
        this.currSunrise = currSunrise;
        this.currSunset = currSunset;
        this.currTemp = currTemp;
        this.currFeelsLike = currFeelsLike;
        this.currPressure = currPressure;
        this.currHumidity = currHumidity;
        this.currUvi = currUvi;
        this.currClouds = currClouds;
        this.currVisibility = currVisibility;
        this.currWindSpeed = currWindSpeed;
        this.currWindDirection = currWindDirection;
        this.currWindGust = currWindGust;
        this.currWeatherDescription = currWeatherDescription;
        this.currWeatherIcon = currWeatherIcon;
        this.currRain = currRain;
        this.currSnow = currSnow;
        this.dayRecords = dayRecords;
        this.hourRecords = hourRecords;
    }

    public String getTimezone() { return timezone; }

    public Integer getTimezone_offset() { return timezone_offset; }

    public String getCurrDt() { return currDt; }

    public String getCurrSunrise() { return currSunrise; }

    public String getCurrSunset() { return currSunset; }

    public String getCurrTemp() { return currTemp; }

    public String getCurrFeelsLike() { return currFeelsLike; }

    public String getCurrPressure() { return currPressure; }

    public String getCurrHumidity() { return currHumidity; }

    public String getCurrUvi() { return currUvi; }

    public String getCurrClouds() { return currClouds; }

    public String getCurrVisibility() { return currVisibility; }

    public String getCurrWindSpeed() { return currWindSpeed; }

    public Double getCurrWindDirection() { return currWindDirection; }

    public String getCurrWindGust() { return currWindGust; }

    public String getCurrWeatherDescription() {
        char[] charArray = currWeatherDescription.toCharArray();
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

    public String getCurrWeatherIcon() {

        return "_" + currWeatherIcon; }

    public String getCurrRain() { return currRain; }

    public String getCurrSnow() { return currSnow; }

    public ArrayList<DayRecord> getDayRecords() {
        return dayRecords;
    }

    public ArrayList<HourRecord> getHourRecords() {
        return hourRecords;
    }
}
