package com.sf10.android.firebase

import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
        dbReference = mRealTime.getReference(session.publicGameState.id)
        dbReference.setValue(session)
        gameStateReference = dbReference.child(Constants.PUBLIC_GAME_STATE)
        privateCardsReference = dbReference.child(Constants.PRIVATE_PLAYER_CARDS).child(Utils().getCurrentUserId())
    }

    fun joinSession(roomId: String){
        mRealTime.getReference(roomId).child(roomId).get().addOnSuccessListener {
            Log.d("Realtime", "Room exist")
        }.addOnFailureListener {
            Log.d("Realtime", "Room doesn't exist")
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