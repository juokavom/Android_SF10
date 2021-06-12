package com.sf10.android.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.sf10.android.adapters.RoomAdapter
import com.sf10.android.databinding.ActivityRoomBinding
import com.sf10.android.firebase.Realtime
import com.sf10.android.models.*
import com.sf10.android.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class RoomActivity : BaseActivity() {
    private lateinit var binding: ActivityRoomBinding
    private lateinit var realtimeDB: Realtime
    private lateinit var publicStateListener: ChildEventListener
    private lateinit var mUser: User
    private lateinit var gameCode: String
    private var isCreator: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realtimeDB = Realtime()

        mUser = intent.getParcelableExtra(Constants.USER_CODE)!!
        isCreator = intent.extras!!.getBoolean(Constants.IS_CREATOR)
        if (isCreator) {
            createSession()
            binding.btnStartGame.visibility = View.VISIBLE
        } else {
            binding.btnStartGame.visibility = View.GONE
            gameCode = intent.extras!!.getString(Constants.GAME_CODE)!!
            realtimeDB.initGameStateDatabaseReferences(gameCode)
        }


        binding.btnLeaveGame.setOnClickListener {
            realtimeDB.kickPlayer(mUser.id) {
                if (isCreator) {
                    goBackToMainMenu("You've left and destroyed the room!")
                    realtimeDB.destroySession()
                }
                else goBackToMainMenu("You've left the room!")
            }
        }

        binding.btnStartGame.setOnClickListener {
            realtimeDB.setGameStartData()
        }

        subscribe()
    }


    private fun createSession() {
        val session = Session()
        session.publicGameState.id =
            UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase()
        session.publicGameState.players =
            mutableListOf(PublicPlayer(username = mUser.username, image = mUser.image))
        gameCode = session.publicGameState.id
        realtimeDB.initGameStateDatabaseReferences(gameCode)
        realtimeDB.createSession(session)
    }

    private fun goBackToMainMenu(message: String) {
        Toast.makeText(
            this@RoomActivity, message,
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this, MainActivity::class.java))
        unsubscribe()
        finish()
    }


    private fun subscribe() {
        publicStateListener = realtimeDB.createChildListener { snapshot, code ->
            when (code) {
                Constants.UPDATE, Constants.ADD -> {
                    when (snapshot.key.toString()) {
                        "id" -> {
                            this.gameCode = snapshot.value.toString()
                            binding.roomId.text = gameCode
                        }
                        "players" -> {
                            var imStillPresent = false
                            val playersList = ArrayList<PublicPlayer>()
                            for (pl in snapshot.children) {
                                val player = pl.getValue(PublicPlayer::class.java)!!
                                if (player.uid == mUser.id) imStillPresent = true
                                playersList.add(player)
                            }
                            setupPlayersRecyclerView(playersList)
                            if (!imStillPresent) goBackToMainMenu("You have been kicked!")
                        }
                        "gameStatus" -> {
                            if(snapshot.value == GameStatus.GAME.toString()){
                                val intent = Intent(this, GameActivity::class.java)
                                intent.putExtra(Constants.USER_CODE, mUser)
                                intent.putExtra(Constants.IS_CREATOR, isCreator)
                                intent.putExtra(Constants.GAME_CODE, gameCode)
                                startActivity(intent)
                                unsubscribe()
                                finish()
                            }
                        }
                    }
                }

                Constants.REMOVE -> {
                    goBackToMainMenu("Creator has ended session!")
                }
            }
        }
        realtimeDB.subscribeOnChanges(realtimeDB.getGameStateReference(), publicStateListener)

//        privateMyCardsListener = realtimeDB.createEventListener {
//
//        }
//        realtimeDB.subscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
    }

    fun unsubscribe() {
        realtimeDB.unSubscribeOnChanges(realtimeDB.getGameStateReference(), publicStateListener)
//        realtimeDB.unSubscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
    }

    private fun setupPlayersRecyclerView(playerList: ArrayList<PublicPlayer>) {
        binding.rvPlayers.layoutManager = LinearLayoutManager(this)
        binding.rvPlayers.setHasFixedSize(true)
        val placesAdapter = RoomAdapter(this, playerList, isCreator, mUser.id)
        binding.rvPlayers.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : RoomAdapter.OnClickListener {
            override fun onClickKick(position: Int, model: PublicPlayer) {
                Log.d("Realtime", "Player with id = ${model.uid} wil be kicked!")
                realtimeDB.kickPlayer(model.uid) {}
            }
        })
    }

    override fun onBackPressed() {}
}