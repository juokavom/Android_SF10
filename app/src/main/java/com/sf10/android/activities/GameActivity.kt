package com.sf10.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sf10.android.databinding.ActivityGameBinding
import com.sf10.android.databinding.ActivitySessionBinding

class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}