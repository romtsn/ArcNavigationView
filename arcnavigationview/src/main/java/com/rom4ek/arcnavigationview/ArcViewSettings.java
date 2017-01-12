package com.rom4ek.arcnavigationview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

public class ArcViewSettings {
    public final static int CROP_INSIDE = 0;
    public final static int CROP_OUTSIDE = 1;
    private boolean cropInside = true;
    private float arcWidth;
    private float elevation;
    private Drawable backgroundDrawable = new ColorDrawable(Color.WHITE); //default background color of navigation view

    public static float dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public ArcViewSettings(Context context, AttributeSet attrs) {
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ArcDrawer, 0, 0);
        arcWidth = styledAttributes.getDimension(R.styleable.ArcDrawer_arc_width, dpToPx(context, 10));

        final int cropDirection = styledAttributes.getInt(R.styleable.ArcDrawer_arc_cropDirection, CROP_INSIDE);
        cropInside = (cropDirection == CROP_INSIDE);

        int[] attrsArray = new int[]{
                android.R.attr.background,
                android.R.attr.layout_gravity,
        };

        TypedArray androidAttrs = context.obtainStyledAttributes(attrs, attrsArray);
        backgroundDrawable = androidAttrs.getDrawable(0);

        androidAttrs.recycle();
        styledAttributes.recycle();
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public boolean isCropInside() {
        return cropInside;
    }

    public float getArcWidth() {
        return arcWidth;
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }
}