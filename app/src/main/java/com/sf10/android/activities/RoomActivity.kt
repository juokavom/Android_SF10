package com.sf10.android.activities

import android.os.Bundle
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener
import com.sf10.android.databinding.ActivityRoomBinding
import com.sf10.android.firebase.Realtime
import com.sf10.android.models.PublicPlayer
import com.sf10.android.models.Session
import com.sf10.android.models.User
import com.sf10.android.utils.Constants
import java.util.*

class RoomActivity : BaseActivity() {
    private lateinit var binding: ActivityRoomBinding
    private lateinit var realtimeDB: Realtime
    private lateinit var publicStateListener: ChildEventListener
    private lateinit var privateMyCardsListener: ValueEventListener
    private lateinit var mUser: User
    private var isCreator: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUser = intent.getParcelableExtra(Constants.USER_CODE)!!
        isCreator = intent.extras!!.getBoolean(Constants.IS_CREATOR)
        if(isCreator) createSession()

        subscribe()
    }

    private fun createSession(){
        realtimeDB = Realtime()

        val session = Session()
        session.publicGameState.id = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase()
        session.publicGameState.players = mutableListOf(PublicPlayer(username = mUser.username, image = mUser.image))
        realtimeDB.createSession(session)
    }

    fun subscribe(){
        publicStateListener = realtimeDB.createChildListener {

        }
        realtimeDB.subscribeOnChanges(realtimeDB.gameStateReference, publicStateListener)

        privateMyCardsListener = realtimeDB.createEventListener {

        }
        realtimeDB.subscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
    }
    fun unsubscribe(){
        realtimeDB.unSubscribeOnChanges(realtimeDB.gameStateReference, publicStateListener)
        realtimeDB.subscribeOnChanges(realtimeDB.privateCardsReference, privateMyCardsListener)
    }
}