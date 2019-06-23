package com.rom4ek.arcnavigationview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue

class ArcViewSettings(context: Context, attrs: AttributeSet?) {
    var isCropInside = true
    val arcWidth: Float
    var elevation: Float = 0.toFloat()
    var fontAssetSrc:String? = null
    var backgroundDrawable: Drawable? = ColorDrawable(Color.WHITE) //default background color of navigation view

    init {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ArcDrawer, 0, 0)
        arcWidth = styledAttributes.getDimension(R.styleable.ArcDrawer_arc_width, dpToPx(context, 10))

        val cropDirection = styledAttributes.getInt(R.styleable.ArcDrawer_arc_cropDirection, CROP_INSIDE)
        isCropInside = cropDirection == CROP_INSIDE

        val attrsArray = intArrayOf(android.R.attr.background, android.R.attr.layout_gravity)

        val androidAttrs = context.obtainStyledAttributes(attrs, attrsArray)
        backgroundDrawable = androidAttrs.getDrawable(0)

        fontAssetSrc = styledAttributes.getString(R.styleable.ArcDrawer_fontAssetSrc)

        androidAttrs.recycle()
        styledAttributes.recycle()
    }

    companion object {
        val CROP_INSIDE = 0
        val CROP_OUTSIDE = 1

        fun dpToPx(context: Context, dp: Int): Float {
            val r = context.resources
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics)
        }
    }
}