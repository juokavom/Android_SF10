package com.sf10.android.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class User(
    var id: String = "",
    var username: String = "",
    var email: String = "",
    var image: String = "",
    var score: Score = Score()
) : Parcelable {
    override fun toString(): String {
        return "User(id='$id', username='$username', email='$email', image='$image')"
    }
}

@Parcelize
data class Score(
    var won: Int = 0,
    var total: Int = 0
) : Parcelable {
    override fun toString(): String {
        return "Win/Total: $won / $total"
    }
}