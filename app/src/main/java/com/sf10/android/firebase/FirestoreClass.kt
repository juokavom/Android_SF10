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


    fun registerUser(activity: BaseActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error writing document", e)
            }
    }

//    fun loadUserData(activity: BaseActivity) {
//        mFireStore.collection(Constants.USERS)
//            .document(getCurrentUserId()).get()
//            .addOnSuccessListener {document ->
//                val loggedInUser = document.toObject(User::class.java)!!
//
//                when(activity){
//                    is SignInActivity -> {
//                        activity.signInSuccess(loggedInUser)
//                    }
//                    is MainActivity -> {
//                        activity.updateNavigationUserDetails(loggedInUser)
//                    }
//                    is MyProfileActivity -> {
//                        activity.setUserDataInUI(loggedInUser)
//                    }
//                }
//            }.addOnFailureListener {
//                when(activity){
//                    is SignInActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                    is MainActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                }
//                Log.e("Sign in", "Error while signing in")
//            }
//    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

}