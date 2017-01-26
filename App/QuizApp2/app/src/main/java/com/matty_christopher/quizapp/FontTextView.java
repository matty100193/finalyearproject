package com.matty_christopher.quizapp;

/*
* modified from http://www.101apps.co.za/index.php/articles/using-custom-fonts-in-your-android-apps.html
* to use delarge font
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontTextView extends TextView {



    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface tf = FontCache.get("fonts/" + "delarge.ttf", context);
        this.setTypeface(tf);
    }




}