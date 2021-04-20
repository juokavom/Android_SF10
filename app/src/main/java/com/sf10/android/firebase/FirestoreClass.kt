package com.sf10.android.firebase

import android.util.Log
import com.facebook.internal.WebDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sf10.android.activities.BaseActivity
import com.sf10.android.models.User
import com.sf10.android.utils.Constants

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun getUser(): User? {
        Log.d("User", "getUser called with user id = ${getCurrentUserId()}")
        var user: User? = null
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    user = document.toObject(User::class.java)!! //Nepakeicia 17 eil reiksmes
                    Log.d("User", "user got! he is = $user")
                }
            }
        Log.d("User", "getUser will return $user")
        return user
    }

    fun registerUser(activity: BaseActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.i("Firestore", "Document exists. Data: ${document.data}")
                } else {
                    Log.i("Firestore", "No such document, creating entity in DB")
                    mFireStore.collection(Constants.USERS)
                        .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            activity.userRegisteredSuccess()
                        }.addOnFailureListener { e ->
                            Log.e(activity.javaClass.simpleName, "Error writing document", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Fetching from Firestore failed")
            }

    }

    fun checkIfExists(){
        if(getCurrentUserId() != "" && getUser() == null) {
            FirebaseAuth.getInstance().signOut()
        }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

}