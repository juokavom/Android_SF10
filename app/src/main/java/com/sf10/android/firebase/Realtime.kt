package com.sf10.android.firebase

import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.sf10.android.models.GameState
import com.sf10.android.models.GameStatus
import com.sf10.android.models.PublicPlayer
import com.sf10.android.models.Session
import com.sf10.android.utils.Constants
import com.sf10.android.utils.SecretConstants
import com.sf10.android.utils.Utils
import java.util.*

class Realtime {
    private val mRealTime = FirebaseDatabase.getInstance(SecretConstants.REALTIME_DATABASE_URL)
    private lateinit var dbReference: DatabaseReference
    lateinit var gameStateReference: DatabaseReference
    lateinit var privateCardsReference: DatabaseReference

    fun createSession(session: Session) {
        dbReference.setValue(session)
    }

    fun initSession(gameCode: String) {
        dbReference = mRealTime.getReference(gameCode)
        gameStateReference = dbReference.child(Constants.PUBLIC_GAME_STATE)
        privateCardsReference =
            dbReference.child(Constants.PRIVATE_PLAYER_CARDS).child(Utils().getCurrentUserId())
    }


    fun destroySession() {
        dbReference.removeValue()
    }

    fun kickPlayer(uid: String, successCalback: () -> Unit) {
        dbReference.child(Constants.PUBLIC_GAME_STATE).get()
            .addOnSuccessListener {
                if (it.value != null) {
                    dbReference.child(Constants.PUBLIC_GAME_STATE).child("players")
                        .setValue(it.getValue<GameState>()!!.players.filter { pp -> pp.uid != uid })
                    successCalback()
                }
            }
    }

    fun checkAndJoinSession(
        user: com.sf10.android.models.User,
        roomId: String,
        joinCallback: (String) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        val preparedCode: String = roomId.toUpperCase(Locale.ROOT)
        mRealTime.getReference(preparedCode).child(Constants.PUBLIC_GAME_STATE).get()
            .addOnSuccessListener {
                if (it.value != null) {
                    val pgs = it.getValue<GameState>()!!
                    if (pgs.gameStatus !== GameStatus.ROOM) {
                        errorCallback("Game has already been started!")
                    } else if (pgs.players.size >= Constants.MAX_PLAYER_COUNT) {
                        errorCallback("Maximum player amount reached!")
                    } else {
                        pgs.players.add(PublicPlayer(username = user.username, image = user.image))
                        mRealTime.getReference(roomId.toUpperCase(Locale.ROOT))
                            .child(Constants.PUBLIC_GAME_STATE).child("players")
                            .setValue(pgs.players)
                        joinCallback(preparedCode)
                    }
                } else {
                    errorCallback("Room doesn't exist!")
                }
            }.addOnFailureListener {
                errorCallback("Room doesn't exist!")
            }
    }

    //Privaciom kortom
    fun createEventListener(callback: (DataSnapshot) -> Unit): ValueEventListener {
        return (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Realtime", "Data changed = ${snapshot.value}")
                callback(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Realtime", "Data cancelled = ${error.message}")
            }
        })
    }

    fun createChildListener(callback: (DataSnapshot, Int) -> Unit): ChildEventListener {
        return (object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                callback(snapshot, Constants.UPDATE)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                callback(snapshot, Constants.ADD)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                callback(snapshot, Constants.REMOVE)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })
    }

    fun subscribeOnChanges(reference: DatabaseReference, valueEventListener: ValueEventListener) {
        unSubscribeOnChanges(reference, valueEventListener)
        reference.addValueEventListener(valueEventListener)
        Log.d("Realtime", "Subscribed to realtime value listener!")
    }

    fun unSubscribeOnChanges(reference: DatabaseReference, valueEventListener: ValueEventListener) {
        reference.removeEventListener(valueEventListener)
        Log.d("Realtime", "Unsubscribed from realtime value listener!")
    }

    fun subscribeOnChanges(reference: DatabaseReference, valueEventListener: ChildEventListener) {
        unSubscribeOnChanges(reference, valueEventListener)
        reference.addChildEventListener(valueEventListener)
        Log.d("Realtime", "Subscribed on realtime child listener!")
    }

    fun unSubscribeOnChanges(reference: DatabaseReference, valueEventListener: ChildEventListener) {
        reference.removeEventListener(valueEventListener)
        Log.d("Realtime", "Unsubscribed from realtime child listener!")
    }

}