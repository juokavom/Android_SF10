package com.sf10.android.firebase

import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sf10.android.models.Session
import com.sf10.android.utils.SecretConstants
import java.util.*

class Realtime {
    private val mRealTime = FirebaseDatabase.getInstance(SecretConstants.REALTIME_DATABASE_URL)
    private lateinit var dbReference: DatabaseReference
    private lateinit var gameStateReference: DatabaseReference
    private lateinit var privateCardsReference: DatabaseReference

    fun createSession(session: Session) {
        val uid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase()
        dbReference = mRealTime.getReference(uid)
        session.publicGameState.id = uid
        session.privatePlayerCards = hashMapOf(getCurrentUserId() to "")
        dbReference.setValue(session)
//        gameStateReference = dbReference.child()
    }

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

//    fun subscribeOnChanges(valueEventListener: ValueEventListener) {
//        unSubscribeOnChanges(reference, valueEventListener)
//        reference.addValueEventListener(valueEventListener)
//        Log.d("Realtime", "Subscribed on realtime!")
//    }
//
//    fun unSubscribeOnChanges(valueEventListener: ValueEventListener) {
//        reference.removeEventListener(valueEventListener)
//        Log.d("Realtime", "Unsubscribed from realtime!")
//    }

    private fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}