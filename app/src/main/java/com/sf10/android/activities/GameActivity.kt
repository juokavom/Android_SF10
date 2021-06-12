package com.sf10.android.activities

import android.icu.util.Measure
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ktx.getValue
import com.sf10.android.R
import com.sf10.android.databinding.ActivityGameBinding
import com.sf10.android.firebase.Realtime
import com.sf10.android.models.GameState
import com.sf10.android.models.PublicPlayer
import com.sf10.android.models.Session
import com.sf10.android.models.User
import com.sf10.android.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView

class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var realtimeDB: Realtime
    private lateinit var publicStateListener: ChildEventListener
    private lateinit var mUser: User
    private lateinit var gameCode: String
    private lateinit var publicGameState: GameState
    private var isCreator: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realtimeDB = Realtime()

        mUser = intent.getParcelableExtra(Constants.USER_CODE)!!
        isCreator = intent.extras!!.getBoolean(Constants.IS_CREATOR)
        gameCode = intent.extras!!.getString(Constants.GAME_CODE)!!

        realtimeDB.initGameStateDatabaseReferences(gameCode)
        realtimeDB.getGameStateReference().get().addOnSuccessListener {
            this.publicGameState = it.getValue<GameState>()!!
            createCircularPlayerListView()
        }
//        subscribe()
    }

    private fun createCircularPlayerListView() {
        val n = publicGameState.players.count()
        val angleIncrement: Float = 360f / n

        for (i in 0 until n) {
            val playerView: View = LayoutInflater.from(this).inflate(R.layout.item_public_player_game, null)
            playerView.id = View.generateViewId()

            playerView.findViewById<TextView>(R.id.tvNameGame).text = publicGameState.players[i].username
            Glide.with(this).load(publicGameState.players[i].image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(playerView.findViewById<CircleImageView>(R.id.iv_place_image_game))

            val circularView = createCircularView(playerView, 120, angleIncrement, i)

            binding.gameLayout.addView(circularView)
        }
    }

    private fun createCircularView(
        view: View,
        radius: Int,
        angleIncrement: Float,
        place: Int
    ): View {
        val layout = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layout.circleRadius = radius.toPx()
        layout.circleConstraint = R.id.gameRoomCenter
        layout.circleAngle = (place * angleIncrement)
        layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layout.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layout.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        view.layoutParams = layout
        return view
    }


//    private fun subscribe() {
//        publicStateListener = realtimeDB.createChildListener { snapshot, code ->
//            Log.d(
//                "Subscribe",
//                "Code = $code, Key: ${snapshot.key}, Value: ${snapshot.value}"
//            )
//            when (code) {
//                Constants.UPDATE, Constants.ADD -> {
//                    when (snapshot.key.toString()) {
//                        "players" -> {
//                            Log.d("Players", snapshot.children.toString())
//                            val playersList = ArrayList<PublicPlayer>()
//                            for (pl in snapshot.children) {
//                                val player = pl.getValue(PublicPlayer::class.java)!!
//                                playersList.add(player)
//                            }
////                            setupPlayersRecyclerView(playersList)
////                            if (!imStillPresent) goBackToMainMenu("You have been kicked!")
//                        }
//                    }
//                }
//
////                Constants.REMOVE -> {
////                    goBackToMainMenu("Creator has ended session!")
////                }
//            }
//        }
//        realtimeDB.subscribeOnChanges(realtimeDB.getGameStateReference(), publicStateListener)
//
////        privateMyCardsListener = realtimeDB.createEventListener {
////
////        }
////        realtimeDB.subscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
//    }

    private fun Int.toPx(): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(), resources.displayMetrics
        ).toInt()

    override fun onBackPressed() {}

    fun unsubscribe() {
        realtimeDB.unSubscribeOnChanges(realtimeDB.getGameStateReference(), publicStateListener)
//        realtimeDB.unSubscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
    }
}