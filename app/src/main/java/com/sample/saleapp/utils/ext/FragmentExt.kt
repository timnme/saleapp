package com.sample.saleapp.utils.ext

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(text: String?) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}