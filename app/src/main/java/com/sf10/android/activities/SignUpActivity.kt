//package com.sf10.android.activities
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.TextUtils
//import android.util.Log
//import android.view.WindowManager
//import android.widget.Toast
//import com.example.projemanag.R
//import com.example.projemanag.databinding.ActivitySignUpBinding
//import com.example.projemanag.firebase.FirestoreClass
//import com.example.projemanag.models.User
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//
//class SignUpActivity : BaseActivity() {
//    private lateinit var binding: ActivitySignUpBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySignUpBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setUpActionBar()
//
//        binding.btnSignUp.setOnClickListener {
//            registerUser(binding)
//        }
//    }
//
//    private fun registerUser(binding: ActivitySignUpBinding){
//        val name: String = binding.etName.text.toString().trim{ it <= ' '}
//        val email: String = binding.etEmail.text.toString().trim{ it <= ' '}
//        val password: String = binding.etPassword.text.toString().trim{ it <= ' '}
//
//        if(validateForm(name, email, password)){
//            showProgressDialog(getString(R.string.please_wait))
//            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val firebaseUser: FirebaseUser = task.result!!.user!!
//                        val user = User(firebaseUser.uid, name, firebaseUser.email!!)
//                        FirestoreClass().registerUser(this@SignUpActivity, user)
//                    } else {
//                        Toast.makeText(
//                            this@SignUpActivity, task.exception!!.message,
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//        }
//    }
//
//    private fun validateForm(name: String, email: String, password: String): Boolean{
//        return when {
//            TextUtils.isEmpty(name) -> {
//                showErrorSnackBar("Please enter name")
//                false
//            }
//            TextUtils.isEmpty(email) -> {
//                showErrorSnackBar("Please enter email")
//                false
//            }
//            TextUtils.isEmpty(password) -> {
//                showErrorSnackBar("Please enter password")
//                false
//            }
//            else -> true
//        }
//    }
//
//    private fun setUpActionBar(){
//        setSupportActionBar(binding.toolbarSignUpActivity)
//
//        val actionBar = supportActionBar
//        if (actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true)
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
//        }
//
//        binding.toolbarSignUpActivity.setNavigationOnClickListener {
//            onBackPressed()
//        }
//
//    }
//
//    fun userRegisteredSuccess() {
//        Toast.makeText(
//            this, "You have successfully registered!",
//            Toast.LENGTH_SHORT
//        ).show()
//        hideProgressDialog()
//        finish()
//    }
//}