package com.sf10.android.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sf10.android.R
import com.sf10.android.databinding.ActivitySignInBinding
import com.sf10.android.databinding.DialogResetPasswordBinding
import com.sf10.android.firebase.FirestoreClass
import com.sf10.android.models.User

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()


        binding.btnSignIn.setOnClickListener {
            hideKeyboard(this@SignInActivity)
            signInRegisteredUser()
        }

        binding.resetPassword.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_reset_password)
            dialog.show()
            val window: Window = dialog.window!!
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            handlePasswordReset(dialog)
        }

        setUpActionBar()
    }

    private fun handlePasswordReset(dialog: Dialog) {
        Log.d("User", "Reset password called")
        val resetButton = dialog.findViewById<Button>(R.id.btn_reset)
        resetButton.setOnClickListener {
            Log.d("User", "Pasword reset handler called")
            hideKeyboard(this@SignInActivity)
            val emailView =
                dialog.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.reset_password_email)
            val email: String = emailView.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                Toast.makeText(
                    baseContext, "Enter email to reset password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(
                            baseContext, "Password reset email was sent to your inbox!",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            baseContext, "Password reset failed: ${e.message.toString()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun signInRegisteredUser() {
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Sign in", "signInWithEmail:success")
                        if (task.result!!.user!!.isEmailVerified) {
                            FirestoreClass().loginUser(this@SignInActivity) {
                                startActivity(
                                    Intent(
                                        this@SignInActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finish()
                            }
                        } else {
                            showErrorSnackBar("Please confirm your email")
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password")
                false
            }
            else -> true
        }
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}