package com.example.mykotlinsocialmediaapp

import android.animation.ValueAnimator
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mykotlinsocialmediaapp.databinding.ActivityLoginBinding
import com.example.mykotlinsocialmediaapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    lateinit var  sharedPreferencesHelper:SharedPreferencesHelper
    private lateinit var valueAnimator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        auth = Firebase.auth

        valueAnimator = ValueAnimator.ofInt(0, 35, 0, 0, -35, 0)
        valueAnimator.duration = 500
        valueAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            binding.cardView.translationX = animatedValue.toFloat()
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()


            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "${auth.currentUser?.uid}")
                            val user = auth.currentUser
//                updateUI(user)

                            sharedPreferencesHelper.saveToken(auth.currentUser?.uid ?: "")


                            Toast.makeText(
                                this,
                                "Authentication : $user.",
                                Toast.LENGTH_SHORT,
                            ).show()


                            startActivity(Intent(this, MainActivity::class.java))
                            finish()


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                this,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()


                        }

                    }

            } else {

                valueAnimator.start()

                Toast.makeText(
                    this,
                    "Please Add Your Information",
                    Toast.LENGTH_SHORT,
                ).show()

            }

        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)

            startActivityForResult(intent, 1)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val email = data.getStringExtra("email")
                    val password = data.getStringExtra("password")

                    binding.etEmail.setText(email)
                    binding.etPassword.setText(password)

                }
            }
    }}
}