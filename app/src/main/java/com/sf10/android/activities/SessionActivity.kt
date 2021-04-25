package com.sf10.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.sf10.android.databinding.ActivityReportBinding
import com.sf10.android.databinding.ActivitySessionBinding
import com.sf10.android.firebase.Realtime
import com.sf10.android.models.Session
import com.sf10.android.utils.SecretConstants

class SessionActivity : BaseActivity() {
    private lateinit var binding: ActivitySessionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}