package com.rom4ek.arcnavigationview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.internal.NavigationMenuView
import android.support.design.internal.ScrimInsetsFrameLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.TextView


/**
 * Created by rom4ek on 10.01.2017.
 */

class ArcNavigationView : NavigationView {

    private var settings: ArcViewSettings? = null
    private var mHeight = 0
    private var mWidth = 0
    private var clipPath: Path? = null
    private var arcPath: Path? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    fun init(context: Context, attrs: AttributeSet?) {
        settings = ArcViewSettings(context, attrs)
        settings!!.elevation = ViewCompat.getElevation(this)

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
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        setBackgroundColor(Color.TRANSPARENT)
        setInsetsColor(Color.TRANSPARENT)
        THRESHOLD = Math.round(ArcViewSettings.dpToPx(getContext(), 15)) //some threshold for child views while remeasuring them
    }

    private fun setInsetsColor(color: Int) {
        try {
            val insetForegroundField = ScrimInsetsFrameLayout::class.java.getDeclaredField("mInsetForeground")
            insetForegroundField.isAccessible = true
            val colorDrawable = ColorDrawable(color)
            insetForegroundField.set(this, colorDrawable)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("RtlHardcoded")
    private fun createClipPath(): Path {
        val path = Path()
        arcPath = Path()

        val arcWidth = settings!!.arcWidth
        val layoutParams = layoutParams as DrawerLayout.LayoutParams
        if (settings!!.isCropInside) {
            if (layoutParams.gravity == Gravity.START || layoutParams.gravity == Gravity.LEFT) {
                arcPath!!.moveTo(mWidth.toFloat(), 0f)
                arcPath!!.quadTo(mWidth - arcWidth, (mHeight / 2).toFloat(),
                        mWidth.toFloat(), mHeight.toFloat())
                arcPath!!.close()

                path.moveTo(0f, 0f)
                path.lineTo(mWidth.toFloat(), 0f)
                path.quadTo(mWidth - arcWidth, (mHeight / 2).toFloat(),
                        mWidth.toFloat(), mHeight.toFloat())
                path.lineTo(0f, mHeight.toFloat())
                path.close()
            } else if (layoutParams.gravity == Gravity.END || layoutParams.gravity == Gravity.RIGHT) {
                arcPath!!.moveTo(0f, 0f)
                arcPath!!.quadTo(arcWidth, (mHeight / 2).toFloat(),
                        0f, mHeight.toFloat())
                arcPath!!.close()

                path.moveTo(mWidth.toFloat(), 0f)
                path.lineTo(0f, 0f)
                path.quadTo(arcWidth, (mHeight / 2).toFloat(),
                        0f, mHeight.toFloat())
                path.lineTo(mWidth.toFloat(), mHeight.toFloat())
                path.close()
            }
        } else {
            if (layoutParams.gravity == Gravity.START || layoutParams.gravity == Gravity.LEFT) {
                arcPath!!.moveTo(mWidth - arcWidth / 2, 0f)
                arcPath!!.quadTo(mWidth + arcWidth / 2, (mHeight / 2).toFloat(),
                        mWidth - arcWidth / 2, mHeight.toFloat())
                arcPath!!.close()

                path.moveTo(0f, 0f)
                path.lineTo(mWidth - arcWidth / 2, 0f)
                path.quadTo(mWidth + arcWidth / 2, (mHeight / 2).toFloat(),
                        mWidth - arcWidth / 2, mHeight.toFloat())
                path.lineTo(0f, mHeight.toFloat())
                path.close()
            } else if (layoutParams.gravity == Gravity.END || layoutParams.gravity == Gravity.RIGHT) {
                arcPath!!.moveTo(arcWidth / 2, 0f)
                arcPath!!.quadTo(-arcWidth / 2, (mHeight / 2).toFloat(),
                        arcWidth / 2, mHeight.toFloat())
                arcPath!!.close()

                path.moveTo(mWidth.toFloat(), 0f)
                path.lineTo(arcWidth / 2, 0f)
                path.quadTo(-arcWidth / 2, (mHeight / 2).toFloat(),
                        arcWidth / 2, mHeight.toFloat())
                path.lineTo(mWidth.toFloat(), mHeight.toFloat())
                path.close()
            }
        }
        return path
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            calculateLayoutAndChildren()
        }
        setFont(settings?.fontAssetSrc)
    }

    fun setFont(fontSrc: String?) {
        settings?.fontAssetSrc = fontSrc
        if (fontSrc == null)
            return
        try {
            overrideFonts(context,this,fontSrc)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun overrideFonts(context: Context, v: View, fontSrc: String) {
        val typeface = Typeface.createFromAsset(context.assets, fontSrc)
        try {
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    val child = v.getChildAt(i)
                    overrideFonts(context, child,fontSrc)
                }
            } else if (v is TextView) {
                v.typeface = typeface
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        if (child is NavigationMenuView) {
            child.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth,
                    View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                    measuredHeight, View.MeasureSpec.EXACTLY))
        } else {
            super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec)
        }
    }

    private fun calculateLayoutAndChildren() {
        if (settings == null) {
            return
        }
        mHeight = measuredHeight
        mWidth = measuredWidth
        if (mWidth > 0 && mHeight > 0) {
            clipPath = createClipPath()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ViewCompat.setElevation(this, settings!!.elevation)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    outlineProvider = object : ViewOutlineProvider() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        override fun getOutline(view: View, outline: Outline) {
                            if (clipPath!!.isConvex) {
                                outline.setConvexPath(clipPath!!)
                            }
                        }
                    }
                }
            }

            val count = childCount
            for (i in 0 until count) {
                val v = getChildAt(i)

                if (v is NavigationMenuView) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        v.setBackground(settings!!.backgroundDrawable)
                    } else {
                        v.setBackgroundDrawable(settings!!.backgroundDrawable)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        ViewCompat.setElevation(v, settings!!.elevation)
                    }
                    //TODO: adjusting child views to new mWidth in their rightmost/leftmost points related to path
                    //                    adjustChildViews((ViewGroup) v);
                }
            }
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun adjustChildViews(container: ViewGroup) {
        val containerChildCount = container.childCount
        val pathMeasure = PathMeasure(arcPath, false)
        val layoutParams = layoutParams as DrawerLayout.LayoutParams

        for (i in 0 until containerChildCount) {
            val child = container.getChildAt(i)
            if (child is ViewGroup) {
                adjustChildViews(child)
            } else {
                val pathCenterPointForItem = floatArrayOf(0f, 0f)
                val location = locateView(child)
                val halfHeight = location.height() / 2

                pathMeasure.getPosTan((location.top + halfHeight).toFloat(), pathCenterPointForItem, null)
                if (layoutParams.gravity == Gravity.END || layoutParams.gravity == Gravity.RIGHT) {
                    val centerPathPoint = measuredWidth - Math.round(pathCenterPointForItem[0])
                    if (child.measuredWidth > centerPathPoint) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(centerPathPoint - THRESHOLD,
                                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                                child.measuredHeight, View.MeasureSpec.EXACTLY))
                        child.layout(centerPathPoint + THRESHOLD, child.top, child.right, child.bottom)
                    }
                } else if (layoutParams.gravity == Gravity.START || layoutParams.gravity == Gravity.LEFT) {
                    if (child.measuredWidth > pathCenterPointForItem[0]) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(Math.round(pathCenterPointForItem[0]) - THRESHOLD,
                                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                                child.measuredHeight, View.MeasureSpec.EXACTLY))
                        child.layout(child.left, child.top, Math.round(pathCenterPointForItem[0]) - THRESHOLD, child.bottom)
                    }
                }
                //set text ellipsize to end to prevent it from overlapping edge
                if (child is TextView) {
                    child.ellipsize = TextUtils.TruncateAt.END
                }
            }
        }
    }

    private fun locateView(view: View?): Rect {
        val loc = Rect()
        val location = IntArray(2)
        if (view == null) {
            return loc
        }
        view.getLocationOnScreen(location)

        loc.left = location[0]
        loc.top = location[1]
        loc.right = loc.left + view.width
        loc.bottom = loc.top + view.height
        return loc
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()

        canvas.clipPath(clipPath!!)
        super.dispatchDraw(canvas)

        canvas.restore()
    }

    companion object {

        private var THRESHOLD: Int = 0
    }
}
