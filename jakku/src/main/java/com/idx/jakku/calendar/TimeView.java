package com.idx.jakku.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextClock;


public class TimeView extends TextClock {

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);



        setFormat12Hour("HH:mm");
        setFormat24Hour("HH:mm");




    }

}