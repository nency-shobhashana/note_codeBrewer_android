package com.nency.note.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.nency.note.R;

public class ColorUtils {
    public static int getColor(Context context, int position) {
        switch (position) {
            case 0:
                return ContextCompat.getColor(context, R.color.orange);
            case 1:
                return ContextCompat.getColor(context, R.color.yellow);
            case 3:
                return ContextCompat.getColor(context, R.color.violate);
            case 4:
                return ContextCompat.getColor(context, R.color.blue);
            case 5:
                return ContextCompat.getColor(context, R.color.pink);
            case 2:
            default:
                return ContextCompat.getColor(context, R.color.green);
        }
    }
}
