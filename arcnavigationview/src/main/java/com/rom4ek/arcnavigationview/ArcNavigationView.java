package com.rom4ek.arcnavigationview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

/**
 * Created by rom4ek on 10.01.2017.
 */

public class ArcNavigationView extends NavigationView {

    private static int THRESHOLD;

    private ArcViewSettings settings;
    private int height = 0;
    private int width = 0;
    private Path clipPath, arcPath;

    public ArcNavigationView(Context context) {
        super(context);
        init(context, null);
    }

    public ArcNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        settings = new ArcViewSettings(context, attrs);
        settings.setElevation(ViewCompat.getElevation(this));

        /**
         * If hardware acceleration is on (default from API 14), clipPath worked correctly
         * from API 18.
         *
         * So we will disable hardware Acceleration if API < 18
         *
         * https://developer.android.com/guide/topics/graphics/hardware-accel.html#unsupported
         * Section #Unsupported Drawing Operations
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setBackgroundColor(Color.TRANSPARENT);
        THRESHOLD = Math.round(ArcViewSettings.dpToPx(getContext(), 15)); //some threshold for child views while remeasuring them
    }

    private Path createClipPath() {
        final Path path = new Path();
        arcPath = new Path();

        float arcWidth = settings.getArcWidth();

        if (settings.isCropInside()) {
            arcPath.moveTo(width, 0);
            arcPath.quadTo(width - arcWidth, height / 2,
                    width, height);
            arcPath.close();

            path.moveTo(0, 0);
            path.lineTo(width, 0);
            path.quadTo(width - arcWidth, height / 2,
                    width, height);
            path.lineTo(0, height);
            path.close();
        } else {
            arcPath.moveTo(width - arcWidth, 0);
            arcPath.quadTo(width + arcWidth, height / 2,
                    width - arcWidth, height);
            arcPath.close();

            path.moveTo(0, 0);
            path.lineTo(width - arcWidth, 0);
            path.quadTo(width + arcWidth, height / 2,
                    width - arcWidth, height);
            path.lineTo(0, height);
            path.close();
        }
        return path;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            calculateLayoutAndChildren();
        }
    }


    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (child instanceof NavigationMenuView) {
            child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        } else {
            super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        }
    }

    private void calculateLayoutAndChildren() {
        if (settings == null) {
            return;
        }
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        if (width > 0 && height > 0) {
            clipPath = createClipPath();
            ViewCompat.setElevation(this, settings.getElevation());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setOutlineProvider(new ViewOutlineProvider() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void getOutline(View view, Outline outline) {
                        if (clipPath.isConvex()) {
                            outline.setConvexPath(clipPath);
                        }
                    }
                });
            }

//            if (settings.isCropInside()) {
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View v = getChildAt(i);

                if (v instanceof NavigationMenuView) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        v.setBackground(settings.getBackgroundDrawable());
                    } else {
                        v.setBackgroundDrawable(settings.getBackgroundDrawable());
                    }
                    ViewCompat.setElevation(v, settings.getElevation());

                    // adjusting child views to new width in their points related to path
                    adjustChildViews((ViewGroup) v);
                }
            }
        }
    }

    private void adjustChildViews(ViewGroup container) {
        final int containerChildCount = container.getChildCount();
        PathMeasure pathMeasure = new PathMeasure(arcPath, false);

        for (int i = 0; i < containerChildCount; i++) {
            View child = container.getChildAt(i);
            if (child instanceof ViewGroup) {
                adjustChildViews((ViewGroup) child);
            } else {
                float pathCenterPointForItem[] = {0f, 0f};
                Rect location = locateView(child);
                int halfHeight = location.height() / 2;

                pathMeasure.getPosTan(location.top + halfHeight, pathCenterPointForItem, null);
                if (child.getMeasuredWidth() > pathCenterPointForItem[0]) {
                    child.measure(MeasureSpec.makeMeasureSpec((Math.round(pathCenterPointForItem[0]) - THRESHOLD),
                            MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                            child.getMeasuredHeight(), MeasureSpec.EXACTLY));
                }
                //set text ellipsize to end to prevent it from overlapping edge
                if (child instanceof TextView) {
                    ((TextView) child).setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        }
    }

    private Rect locateView(View view) {
        Rect loc = new Rect();
        int[] location = new int[2];
        if (view == null) {
            return loc;
        }
        view.getLocationOnScreen(location);

        loc.left = location[0];
        loc.top = location[1];
        loc.right = loc.left + view.getWidth();
        loc.bottom = loc.top + view.getHeight();
        return loc;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        canvas.clipPath(clipPath);
        super.dispatchDraw(canvas);

        canvas.restore();
    }
}
