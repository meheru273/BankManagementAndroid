package com.example.myapplication;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

/** Tiny view helpers for building cards in code (rounded backgrounds, dp conversion). */
public final class Ui {

    private Ui() {}

    public static int dp(Context c, float dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics()));
    }

    public static GradientDrawable rounded(int color, Context c, float radiusDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(dp(c, radiusDp));
        return gd;
    }

    public static GradientDrawable roundedStroke(int color, Context c, float radiusDp,
                                                 int strokeColor, float strokeDp) {
        GradientDrawable gd = rounded(color, c, radiusDp);
        gd.setStroke(dp(c, strokeDp), strokeColor);
        return gd;
    }
}
