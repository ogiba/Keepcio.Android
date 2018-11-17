package pl.ogiba.keepcio.utils

import android.app.Activity
import androidx.annotation.IdRes
import android.view.View

/**
 * Created by robertogiba on 23.03.2018.
 */
fun <T : View> Activity.bind(@IdRes res : Int) : Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy { findViewById<T>(res) }
}