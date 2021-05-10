package com.sf10.android.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener
import com.sf10.android.R
import com.sf10.android.adapters.RoomAdapter
import com.sf10.android.databinding.ActivityRoomBinding
import com.sf10.android.firebase.Realtime
import com.sf10.android.models.PublicPlayer
import com.sf10.android.models.Session
import com.sf10.android.models.User
import com.sf10.android.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class RoomActivity : BaseActivity() {
    private lateinit var binding: ActivityRoomBinding
    private lateinit var realtimeDB: Realtime
    private lateinit var publicStateListener: ChildEventListener
    private lateinit var privateMyCardsListener: ValueEventListener
    private lateinit var mUser: User
    private lateinit var mSession: Session
    private var isCreator: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSession = Session()
        realtimeDB = Realtime()

        mUser = intent.getParcelableExtra(Constants.USER_CODE)!!
        isCreator = intent.extras!!.getBoolean(Constants.IS_CREATOR)
        if (isCreator) {
            createSession()
            binding.btnJoinGame.visibility = View.VISIBLE
        } else {
            binding.btnJoinGame.visibility = View.GONE
            realtimeDB.initSession(intent.extras!!.getString(Constants.GAME_CODE)!!)
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

        subscribe()
    }


    private fun createSession() {
        val session = Session()
        session.publicGameState.id =
            UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase()
        session.publicGameState.players =
            mutableListOf(PublicPlayer(username = mUser.username, image = mUser.image))
        realtimeDB.initSession(session.publicGameState.id)
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
                            mSession.publicGameState.id = snapshot.value.toString()
                            binding.roomId.text = mSession.publicGameState.id
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
                    }
                }

                Constants.REMOVE -> {
                    goBackToMainMenu("Creator has ended session!")
                }
            }
        }
        realtimeDB.subscribeOnChanges(realtimeDB.gameStateReference, publicStateListener)

        privateMyCardsListener = realtimeDB.createEventListener {

        }
        realtimeDB.subscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
    }

    fun unsubscribe() {
        realtimeDB.unSubscribeOnChanges(realtimeDB.gameStateReference, publicStateListener)
        realtimeDB.subscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
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