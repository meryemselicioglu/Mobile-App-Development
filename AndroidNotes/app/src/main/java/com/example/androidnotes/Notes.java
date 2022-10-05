package com.example.androidnotes;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notes implements Serializable {

    private String noteTitle;
    private String noteText;
    private String noteTime;


    Notes(String noteTitle, String noteText) {
        setTitle(noteTitle);
        setText(noteText);
        setTime();
    }

    public String getTitle() {
        return noteTitle;
    }
    public String getText() {
        return noteText;
    }
    public String getTime() {
        return noteTime;
    }

    public void setTitle(String title){ noteTitle= title;}
    public void setText(String text){ noteText = text;}
    public void setTime(String time) { noteTime = time;}

    public void setTime(){
        Date date = new Date();
        SimpleDateFormat form = new SimpleDateFormat("E dd MMM yyyy, hh:mm a", Locale.US);
        noteTime = form.format(date);
    }


    @NonNull
    @Override
    public String toString() {
        try{
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("title").value(getTitle());
            jsonWriter.name("text").value(getText());
            jsonWriter.name("time").value(getTime());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
