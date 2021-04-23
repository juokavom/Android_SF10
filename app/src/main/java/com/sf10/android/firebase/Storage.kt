package com.sf10.android.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sf10.android.R
import com.sf10.android.activities.BaseActivity
import com.sf10.android.utils.Constants
import java.util.*


class Storage {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun deleteUserImage(url: String){
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.getReferenceFromUrl(url)
        storageReference.delete().addOnSuccessListener {
            Log.e("Picture","#deleted")
        }
    }

    fun uploadUserImage(
        activity: BaseActivity, selectedImageFileUri: Uri?,
        onSuccess: (Uri) -> Unit, onError: () -> Unit
    ) {
        Log.d("Image", "Upload image called, uri = $selectedImageFileUri")

        if (selectedImageFileUri != null) {
            activity.showProgressDialog(activity.resources.getString(R.string.please_wait))
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "profiles/${Constants.USER_IMAGE}${UUID.randomUUID()}.jpeg"
                )

            sRef.putFile(selectedImageFileUri)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i(
                        "Image",
                        "Firebase image url = ${taskSnapshot.metadata!!.reference!!.downloadUrl}"
                    )

                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri)
                    }
                }.addOnFailureListener {
                    onError()
                }
        }
    }
}