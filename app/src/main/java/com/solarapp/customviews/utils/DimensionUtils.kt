package com.solarapp.customviews.utils

import android.content.Context

class DimensionUtils {
    companion object {
        fun dpToPixels(context: Context, dp: Int): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi / 160F)
        }
    }
}