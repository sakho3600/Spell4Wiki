package com.manimarank.spell4wiki.utils

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.style.MetricAffectingSpan
import android.widget.TextView

fun TextView.removeStyleAfterPaste() {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
            try {
                if (string != null && string.isNotEmpty()) {
                    val toBeRemovedSpans = string.getSpans(0, string.length, MetricAffectingSpan::class.java)
                    for (toBeRemovedSpan in toBeRemovedSpans) string.removeSpan(toBeRemovedSpan)
                }
            } catch (e: Exception) {
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    })
}

fun Context.getAppVersion(): Long {
    val pInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    var versionCode: Long = 0
    try {
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo.longVersionCode
        } else {
            pInfo.versionCode.toLong()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return versionCode
}