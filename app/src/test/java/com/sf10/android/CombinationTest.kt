package com.sf10.android

import com.sf10.android.models.Card
import com.sf10.android.models.Combination
import com.sf10.android.models.Hand
import com.sf10.android.models.Rank
import org.junit.Test

import org.junit.Assert.*

class CombinationTest {
    @Test
    fun combination_one_field_less() {
        val combo = Combination(Hand.THREE_OF_A_KIND)
        assertEquals(-1, combo.compareTo(Combination(Hand.FULL_HOUSE)))
    }
    @Test
    fun combination_one_field_more() {
        val combo = Combination(Hand.FLUSH)
        assertEquals(1, combo.compareTo(Combination(Hand.THREE_OF_A_KIND)))
    }
    @Test
    fun combination_one_field_equal() {
        val combo = Combination(Hand.FLUSH)
        assertEquals(0, combo.compareTo(Combination(Hand.FLUSH)))
    }

    @Test
    fun combination_two_field_more() {
        val combo = Combination(Hand.THREE_OF_A_KIND, Card(Rank.K))
        assertEquals(1, combo.compareTo(Combination(Hand.THREE_OF_A_KIND, Card(Rank.J))))
    }
    @Test
    fun combination_two_field_less() {
        val combo = Combination(Hand.FOUR_OF_A_KIND, Card(Rank.Q))
        assertEquals(-1, combo.compareTo(Combination(Hand.FOUR_OF_A_KIND, Card(Rank.K))))
    }
    @Test
    fun combination_two_field_equal() {
        val combo = Combination(Hand.ONE_PAIR, Card(Rank.Q))
        assertEquals(0, combo.compareTo(Combination(Hand.ONE_PAIR, Card(Rank.Q))))
    }

    @Test
    fun combination_three_field_more() {
        val combo = Combination(Hand.TWO_PAIR, Card(Rank.TEN), Card(Rank.Q))
        assertEquals(1, combo.compareTo(Combination(Hand.TWO_PAIR, Card(Rank.TEN), Card(Rank.J))))
    }
    @Test
    fun combination_three_field_less() {
        val combo = Combination(Hand.FULL_HOUSE, Card(Rank.NINE), Card(Rank.TEN))
        assertEquals(-1, combo.compareTo(Combination(Hand.FULL_HOUSE, Card(Rank.NINE), Card(Rank.A))))
    }
    @Test
    fun combination_three_field_equal() {
        val combo = Combination(Hand.FULL_HOUSE, Card(Rank.J), Card(Rank.K))
        assertEquals(0, combo.compareTo(Combination(Hand.FULL_HOUSE, Card(Rank.J), Card(Rank.K))))
    }
}