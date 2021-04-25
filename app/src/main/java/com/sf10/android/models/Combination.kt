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
        val handsCompareResult: Int = hand.id.compareTo(other.hand.id)
        return if (handsCompareResult != 0) handsCompareResult else when (hand) {
            Hand.NONE -> 0
            Hand.HIGH_CARD,
            Hand.ONE_PAIR,
            Hand.THREE_OF_A_KIND,
            Hand.STRAIGHT,
            Hand.FOUR_OF_A_KIND -> firstCard.rank.id.compareTo(other.firstCard.rank.id)
            Hand.TWO_PAIR, Hand.FULL_HOUSE -> {
                val firsCardsCompareResult = firstCard.rank.id.compareTo(other.firstCard.rank.id)
                if (firsCardsCompareResult != 0) firsCardsCompareResult else secondCard.rank.id.compareTo(
                    other.secondCard.rank.id
                )
            }
            Hand.FLUSH, Hand.STRAIGHT_FLUSH, Hand.ROYAL_FLUSH -> 0
        }
    }
}