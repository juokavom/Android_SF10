package com.sf10.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sf10.android.databinding.ActivityRoomBinding
import com.sf10.android.databinding.ActivitySessionBinding

class RoomActivity : BaseActivity() {
    private lateinit var binding: ActivityRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}