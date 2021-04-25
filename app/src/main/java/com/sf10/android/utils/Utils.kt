package com.sf10.android.utils

import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    fun getCurrentDateTime(): String{
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date())
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