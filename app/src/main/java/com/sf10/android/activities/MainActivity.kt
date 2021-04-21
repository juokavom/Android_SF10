package com.sf10.android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.sf10.android.databinding.ActivityMainBinding
import com.sf10.android.firebase.FirestoreClass
import com.sf10.android.models.User

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirestoreClass().getUser({ user ->
            Log.d("User", "object = $user")
            binding.uid.text = user.id
            binding.email.text = user.email
            binding.username.text = user.username
        }, {
            Log.d("User", "does not exist")
        })

        binding.logout.setOnClickListener {
            AuthUI.getInstance().signOut(this)
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }
    }
}