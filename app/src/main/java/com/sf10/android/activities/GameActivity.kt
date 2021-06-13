package com.sf10.android.activities

import android.R.attr.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ktx.getValue
import com.sf10.android.R
import com.sf10.android.databinding.ActivityGameBinding
import com.sf10.android.databinding.ItemPublicPlayerGameBinding
import com.sf10.android.firebase.Realtime
import com.sf10.android.models.GameState
import com.sf10.android.models.User
import com.sf10.android.utils.Constants
import com.sf10.android.utils.Utils
import com.sf10.android.utils.Utils.Companion.dp
import com.sf10.android.utils.Utils.Companion.px
import de.hdodenhof.circleimageview.CircleImageView


class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var realtimeDB: Realtime
    private lateinit var publicStateListener: ChildEventListener
    private lateinit var mUser: User
    private lateinit var gameCode: String
    private lateinit var publicGameState: GameState
    private lateinit var playerViewMap: MutableMap<String, View>
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
        this.playerViewMap = mutableMapOf()

        for (i in 0 until n) {
//            val playerView: View =
//                LayoutInflater.from(this).inflate(R.layout.item_public_player_game, null)
            val playerView = ItemPublicPlayerGameBinding.inflate(layoutInflater)
            playerView.root.id = View.generateViewId()
            val publicPlayer = publicGameState.players[i]

            playerView.tvNameGame.text =
                publicGameState.players[i].username
            Glide.with(this).load(publicGameState.players[i].image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(playerView.ivPlaceImageGame)

            for(card_index in 0 until publicPlayer.cardCount){
                val lp = LinearLayout.LayoutParams(30.px, 40.px)
                lp.setMargins((card_index * 10 + 60).px, 30.px, 0.px, 0.px)
                val cardImageView = ImageView(this)
                cardImageView.setImageResource(R.drawable.ic_card_back)
                cardImageView.layoutParams = lp
                playerView.flPublicPlayerGame.addView(cardImageView)
            }

            playerViewMap[publicPlayer.uid] = Utils.createCircularView(
                playerView.root,
                i,
                120,
                angleIncrement,
                R.id.gameRoomCenter
            )
            binding.gameLayout.addView(playerViewMap[publicPlayer.uid])
        }
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

    override fun onBackPressed() {}

    fun unsubscribe() {
        realtimeDB.unSubscribeOnChanges(realtimeDB.getGameStateReference(), publicStateListener)
//        realtimeDB.unSubscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
    }
}