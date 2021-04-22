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

    fun getUser(callback: (User) -> Unit, error: () -> Unit) {
        Log.d("User", "getUser called with user id = ${getCurrentUserId()}")
        if (getCurrentUserId() == "" ||
            (!FirebaseAuth.getInstance().currentUser!!.isEmailVerified && FirebaseAuth.getInstance().currentUser!!.providerData[1].providerId == "password")
        ) error() // google.com facebook.com password
        else {
            mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get()
                .addOnSuccessListener { document ->
                    Log.d("User", "getUser success listener = ${document}}")
                    if (document.data != null) {
                        callback(document.toObject(User::class.java)!!)
                    } else error()
                }
        }
    }

    fun registerUser(activity: BaseActivity, userInfo: User, callback: () -> Unit) {
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
                callback()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Fetching from Firestore failed")
            }

    }

    fun loginUser(activity: BaseActivity, callback: () -> Unit) {
        activity.showProgressDialog("Signing in...")
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    activity.hideProgressDialog()
                    callback()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Fetching from Firestore failed")
                activity.hideProgressDialog()
            }

    }

    fun logoutIfNotExist() {
        getUser({}, {
            FirebaseAuth.getInstance().signOut()
        })
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