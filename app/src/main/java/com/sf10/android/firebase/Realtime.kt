package com.sf10.android.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sf10.android.utils.SecretConstants

class Realtime {
    private val mRealTime = FirebaseDatabase.getInstance(SecretConstants.REALTIME_DATABASE_URL)
    private val mMyGameRef = mRealTime.getReference("myGame")
    private var valueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("Realtime", "Data changed = ${snapshot.value}")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }

    fun generateNewGameRoom(){
        mMyGameRef.setValue("This is my first game")
    }

    fun subscribeOnChanges(){
        unSubscribeOnChanges()
        mMyGameRef.addValueEventListener(valueEventListener)
        Log.d("Realtime", "Subscribed on realtime!")
    }

    fun unSubscribeOnChanges(){
        mMyGameRef.removeEventListener(valueEventListener)
        Log.d("Realtime", "Unsubscribed from realtime!")
    }

}