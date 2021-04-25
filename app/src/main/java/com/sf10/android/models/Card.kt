package com.sf10.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    var rank: Rank = Rank.NONE,
    var suit: Suit = Suit.NONE
) : Parcelable

enum class Rank(val id: Int) {
    NONE(0), NINE(1), TEN(2), J(3), Q(4), K(5), A(6)
}

//clubs (♣), diamonds (♦), hearts (♥), spades (♠)
enum class Suit(val id: Int) {
    NONE(0), CLUBS(1), DIAMONDS(2), HEARTS(3), SPADES(4)
}