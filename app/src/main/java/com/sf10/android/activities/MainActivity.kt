package com.sf10.android.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.sf10.android.R
import com.sf10.android.databinding.ActivityMainBinding
import com.sf10.android.databinding.ContentMainBinding
import com.sf10.android.firebase.Realtime
import com.sf10.android.models.Card
import com.sf10.android.models.User
import com.sf10.android.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentWindowBinding: ContentMainBinding
    private val mRealtime = Realtime()
    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        contentWindowBinding = binding.mainWindow.mainContent

        setContentView(binding.root)

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener(this)

        updateUser()

        contentWindowBinding.btnJoinGame.setOnClickListener {
            hideKeyboard(this)
            if (contentWindowBinding.etGameId.text.toString().isNotEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "Your provided game code = ${contentWindowBinding.etGameId.text}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                showErrorSnackBar("Game code cannot be empty!")
            }
        }

        contentWindowBinding.btnCreateGame.setOnClickListener {
//            startActivity(Intent(this, SessionActivity::class.java))
//            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.MY_PROFILE_REQUEST && resultCode == RESULT_OK) {
            mUser = data!!.getParcelableExtra(Constants.USER_CODE)
            updateNavigationUserDetails()
        }
    }

    private fun updateUser() {
        fetchUser { user ->
            mUser = user
            Log.d("User", "User updation, data = $mUser")
            updateNavigationUserDetails()
        }
    }

    private fun logout() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    private fun updateNavigationUserDetails() {
        Glide.with(this).load(mUser!!.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById<CircleImageView>(R.id.iv_user_image))

        findViewById<TextView>(R.id.tv_username).text = mUser!!.username
        binding.navView.menu.findItem(R.id.nav_score).title = mUser!!.score.toString()
    }


    private fun setupActionBar() {
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_drawer_24)
        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                val myProfileIntent = Intent(this, MyProfileActivity::class.java)
                myProfileIntent.putExtra(Constants.USER_CODE, mUser)
                startActivityForResult(myProfileIntent, Constants.MY_PROFILE_REQUEST)
            }
            R.id.nav_sign_out -> {
                logout()
            }
            R.id.nav_score -> return false
            R.id.nav_report_bug -> {
                startActivity(Intent(this, ReportActivity::class.java))
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}