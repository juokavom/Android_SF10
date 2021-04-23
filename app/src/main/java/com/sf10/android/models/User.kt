package com.sf10.android.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class User(
    var id: String = "",
    var username: String = "",
    var email: String = "",
    var image: String = ""
) : Parcelable {
    override fun toString(): String {
        return "User(id='$id', username='$username', email='$email', image='$image')"
    }
}
