package com.sf10.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Session(
    var publicGameState: GameState = GameState(),
    var privatePlayerCards: HashMap<String, Any> = hashMapOf()
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
data class GameState(
    var id: String = "",
    var visibility: Visibility = Visibility.PRIVATE,
    var gameStatus: GameStatus = GameStatus.ROOM,
    var moveStatus: MoveStatus = MoveStatus.INIT,

) : Parcelable