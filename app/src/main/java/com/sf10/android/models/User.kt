package com.sf10.android.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val image: String = ""
) : Parcelable {
    override fun toString(): String {
        return "User(id='$id', username='$username', email='$email', image='$image')"
    }
}
