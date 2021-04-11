package com.sf10.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.sf10.android.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uid.text = getCurrentUserID();
        binding.email.text = "TODO";

        Log.d("Image", FirebaseAuth.getInstance().currentUser!!.photoUrl.toString())

        binding.logout.setOnClickListener {
            AuthUI.getInstance().signOut(this);
        }
    }
}