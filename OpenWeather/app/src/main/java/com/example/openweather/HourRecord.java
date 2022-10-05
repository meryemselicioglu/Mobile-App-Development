package com.example.openweather;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HourRecord implements Serializable {

    private final String dt;
    private final String temp;
    private final String description;
    private final String iconCode;
    private String day;

    HourRecord(String dt, String temp, String description, String icon, String day)
    {
        this.iconCode = icon;
        this.description = description;
        this.temp = temp;
        this.day = day;
        this.dt = dt;
    }

    public String getDt() {
        return dt;
    }

    public String getTemp() {
        return temp;
    }

    public String getDescription() {
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

    public String getIconCode() {
        return "_"  + iconCode;
    }

    public String getDay() {
        return dt;
    }
}
