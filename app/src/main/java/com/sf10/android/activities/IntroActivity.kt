package com.sf10.android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.sf10.android.R
import com.sf10.android.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//        binding.btnSignInIntro.setOnClickListener{
//            startActivity(Intent(this, SignInActivity::class.java))
//        }
//
//        binding.btnSignUpIntro.setOnClickListener{
//            startActivity(Intent(this, SignUpActivity::class.java))
//        }
    }
}