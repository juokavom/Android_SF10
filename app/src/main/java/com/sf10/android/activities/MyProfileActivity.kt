package com.sf10.android.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.sf10.android.R
import com.sf10.android.databinding.ActivityMyProfileBinding
import com.sf10.android.firebase.Firestore
import com.sf10.android.firebase.Storage
import com.sf10.android.models.User
import com.sf10.android.utils.Constants
import com.sf10.android.utils.ImageHandler
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding

    private var mSelectedImageFileUri: Uri? = null
    private var mUser: User? = null

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        mUser = intent.getParcelableExtra(Constants.USER_CODE)
        setUserDataInUI()

        binding.ivUserProfileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnUpdate.setOnClickListener {
            handleUpdateAction()
        }
    }

    private fun handleUpdateAction() {
        if (binding.etName.text.toString() != mUser!!.username) {
            mUser!!.username = binding.etName.text.toString()
        }
        if (mSelectedImageFileUri != null) {
            if (validateImage()) {
                mSelectedImageFileUri =
                    ImageHandler().process(mSelectedImageFileUri!!, 500, this)
                Storage().uploadUserImage(this, mSelectedImageFileUri, { uri ->
                    if(mUser!!.image.contains(Constants.USER_IMAGE, ignoreCase = true)) Storage().deleteUserImage(mUser!!.image)
                    mUser!!.image = uri.toString()
                    mSelectedImageFileUri = null
                    updateUser()
                }, {
                    showErrorSnackBar("You can only upload images under 1MB")
                    hideProgressDialog()
                })
            }
        } else {
            updateUser()
        }
    }

    private fun updateUser() {
        Firestore().updateUser(mUser!!) {
            val passBackIntent = Intent()
            passBackIntent.putExtra(Constants.USER_CODE, mUser)
            setResult(RESULT_OK, passBackIntent)
            hideProgressDialog()
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can allow it from the settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide.with(this).load(mSelectedImageFileUri).centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivUserProfileImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showImageChooser() {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_24)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        binding.toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUserDataInUI() {
        Glide.with(this).load(mUser!!.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivUserProfileImage)

        binding.etName.setText(mUser!!.username)
        binding.etEmail.setText(mUser!!.email)
    }

    private fun validateImage(): Boolean {
        return when (getFileExtension(mSelectedImageFileUri!!)) {
            "jpg", "jpeg", "png" -> true
            else -> {
                showErrorSnackBar("Image must be in PNG or JPEG format")
                false
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri))
    }
}