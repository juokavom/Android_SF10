package com.sf10.android.firebase

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sf10.android.R
import com.sf10.android.activities.BaseActivity
import java.lang.Exception
import java.util.*

class Storage {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun uploadUserImage(
        activity: BaseActivity, selectedImageFileUri: Uri?,
        onSuccess: (Uri) -> Unit, onError: (Exception) -> Unit
    ) {
        Log.d("Image", "Upload image called, uri = $selectedImageFileUri")

        if (selectedImageFileUri != null) {
            activity.showProgressDialog(activity.resources.getString(R.string.please_wait))
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "profiles/USER_IMAGE${UUID.randomUUID()}.jpeg"
                )

            sRef.putFile(selectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i(
                        "Image",
                        "Firebase image url = ${taskSnapshot.metadata!!.reference!!.downloadUrl}"
                    )

                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri)
                    }.addOnFailureListener { exception ->
                        onError(exception)
                    }
                }.addOnFailureListener { taskSnapshot ->
                    Log.i(
                        "Image",
                        "Firebase upload failed, code = ${taskSnapshot.message.toString()}"
                    )
                }
        }
    }
}