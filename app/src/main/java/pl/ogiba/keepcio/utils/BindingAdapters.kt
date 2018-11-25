package pl.ogiba.keepcio.utils

import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("error")
fun bindError(view: EditText, error: CharSequence?) {
    view.error = error
}