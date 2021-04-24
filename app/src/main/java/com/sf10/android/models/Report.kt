package com.sf10.android.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Report(
    var user: String = "",
    var date: String = "",
    var description: String = ""
) : Parcelable