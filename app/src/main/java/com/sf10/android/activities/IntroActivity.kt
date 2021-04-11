package com.sf10.android.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.sf10.android.R
import com.sf10.android.databinding.ActivityIntroBinding
import com.sf10.android.firebase.FirestoreClass
import com.sf10.android.models.User
import java.util.*


class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    private lateinit var callbackManager: CallbackManager

    private val RC_GOOGLE = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.loginGoogle.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            startActivityForResult(GoogleSignIn.getClient(this, gso).signInIntent, RC_GOOGLE)
        }

        callbackManager = CallbackManager.Factory.create();

        binding.loginFacebook.setReadPermissions("email", "public_profile")
        binding.loginFacebook.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("TAG", "facebook:onSuccess:$loginResult")
                    signInWithCredential(FacebookAuthProvider.getCredential(loginResult.accessToken.token))
                }

                override fun onCancel() {
                    Log.d("TAG", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("TAG", "facebook:onError", error)
                }
            })

//        binding.btnSignInIntro.setOnClickListener{
//            startActivity(Intent(this, SignInActivity::class.java))
//        }

        binding.btnSignUpIntro.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val user = User(firebaseUser.uid, firebaseUser.displayName!!,
                        firebaseUser.email!!, firebaseUser.photoUrl!!.toString())
                    FirestoreClass().registerUser(this@IntroActivity, user)
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@IntroActivity, "Authentication failed. ${task.exception}",
                    Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE) {
            //Google
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                signInWithCredential(GoogleAuthProvider.getCredential(account.idToken!!, null))
            } catch (e: ApiException) {}
        } else {
            //Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

}