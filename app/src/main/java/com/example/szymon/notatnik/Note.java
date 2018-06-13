package com.example.szymon.notatnik;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

//notatka którą widzę
public class Note implements Serializable { //implementujemy by móc zapisywać jako pliki binarne

    private long mDateTime;
    private String mTitle;
    private String mContent;


    //alt+insert - konstruktory
    public Note(long dateTime, String title, String content) {
        mDateTime = dateTime;
        mTitle = title;
        mContent = content;
    }

    //setters - modyfikatory
    public void setContent(String content) {
        mContent = content;
    }

    public void setDateTime(long dateTime) {
        mDateTime = dateTime;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    //getters-akcesory
    public long getDateTime() {
        return mDateTime;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }


    //odniesienie do context, żeby korzystać ze strefy czasowej użytkownika
    public String getDateTimeFormatted(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , context.getResources().getConfiguration().locale);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(mDateTime)); //alt+enter do Date import
    }

}
