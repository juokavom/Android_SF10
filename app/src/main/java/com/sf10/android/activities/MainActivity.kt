package com.sf10.android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.sf10.android.R
import com.sf10.android.databinding.ActivityMainBinding
import com.sf10.android.firebase.FirestoreClass
import com.sf10.android.models.User
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var binding: ActivityMainBinding
    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener(this)

        fetchUser { user ->
            mUser = user
            updateNavigationUserDetails()
        }
    }

    private fun fetchUser(setUser: (User) -> Unit) {
        FirestoreClass().getUser({ user ->
            Log.d("User", "user object = $user")
            setUser(user)
        }, {
            Log.d("User", "does not exist")
        })
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
//                startActivity(Intent(this, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                logout()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}