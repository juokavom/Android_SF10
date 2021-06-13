package com.sf10.android.utils

import android.content.res.Resources.getSystem
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        fun getCurrentDateTime(): String {
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

        fun createCircularView(
            view: View,
            position: Int,
            radius: Int,
            angleOfIncrement: Float,
            centerViewId: Int
        ): View {
            val layout = ConstraintLayout.LayoutParams(
                120.px,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layout.circleRadius = radius.px
            layout.circleConstraint = centerViewId
            layout.circleAngle = (position * angleOfIncrement)
            layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layout.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layout.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            view.layoutParams = layout
            return view
        }

        val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()
        val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()
    }
}