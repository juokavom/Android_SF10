package com.sf10.android.models

import android.os.Parcelable
import com.google.firebase.auth.FirebaseAuth
import com.sf10.android.utils.Utils
import kotlinx.parcelize.Parcelize

data class Session(
    var publicGameState: GameState = GameState(),
    var privatePlayerCards: HashMap<String, Any> = hashMapOf(Utils().getCurrentUserId() to "")
)

enum class Visibility {
    PUBLIC, PRIVATE
}

enum class GameStatus {
    ROOM, GAME, END
}

enum class MoveStatus {
    INIT, DECISION, CHECKING, END
}

@Parcelize
data class Date(
    var created: String = Utils().getCurrentDateTime(),
    var started: String = "",
    var ended: String = ""
): Parcelable

@Parcelize
data class PublicPlayer(
    var uid: String = Utils().getCurrentUserId(),
    var username: String = "",
    var image: String = "",
    var cardCount: Int = 0,
    var combination: Combination = Combination()
): Parcelable

@Parcelize
data class GameState(
    var id: String = "",
    var creator_uid: String = Utils().getCurrentUserId(),
    var visibility: Visibility = Visibility.PRIVATE,
    var gameStatus: GameStatus = GameStatus.ROOM,
    var moveStatus: MoveStatus = MoveStatus.INIT,
    var currentCombination: Combination = Combination(),
    var countDownTimer: Int = 0,
    var timePerMove: Int = 30,
    var currentPlayer: Int = 0,
    var date: Date = Date(),
    var winner: String = "",
    var players: List<PublicPlayer> = mutableListOf(PublicPlayer()),
    var spectators: List<String> = mutableListOf()
) : Parcelable