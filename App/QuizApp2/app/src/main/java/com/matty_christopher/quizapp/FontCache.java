package com.matty_christopher.quizapp;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/*got from
*http://stackoverflow.com/questions/16901930/memory-leaks-with-custom-font-for-set-custom-font
* user post: britzl
* Used to cache font to prevent memory leaks which can cause out of memory exception
* this solution caches it so no need to search for multiple instances
 */

public class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}