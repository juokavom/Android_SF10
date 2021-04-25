package com.sf10.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Combination(
    var hand: Hand = Hand.NONE,
    var firstCard: Card = Card(),
    var secondCard: Card = Card(),
    var suit: Suit = Suit.NONE
) : Parcelable, Comparable<Combination> {
    override fun compareTo(other: Combination): Int {
        if (hand.id == other.hand.id) {
            when (hand) {
                Hand.NONE -> return 0
                Hand.HIGH_CARD -> return firstCard.rank.compareTo(other.firstCard.rank)
                Hand.ONE_PAIR -> return firstCard.rank.compareTo(other.firstCard.rank)
                Hand.TWO_PAIR -> {
                    val firsCardsCompareResult = firstCard.rank.compareTo(other.firstCard.rank)
                    return if (firsCardsCompareResult == 0) secondCard.rank.compareTo(other.secondCard.rank) else firsCardsCompareResult
                }
            }
        } else if (hand.id < other.hand.id) return -1
        else return 1
    }
}