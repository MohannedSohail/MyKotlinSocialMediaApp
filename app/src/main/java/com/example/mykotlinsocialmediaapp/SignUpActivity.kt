package com.example.mykotlinsocialmediaapp

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mykotlinsocialmediaapp.databinding.ActivitySignUpBinding
import com.example.mykotlinsocialmediaapp.model.Student
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var valueAnimator: ValueAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()


        valueAnimator = ValueAnimator.ofInt(0, 35, 0, 0, -35, 0)
        valueAnimator.duration = 500
        valueAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            binding.cardView.translationX = animatedValue.toFloat()
        }


        binding.signUpBtnSignUp.setOnClickListener {


            val name = binding.signUpEtName.text.toString()
            val password = binding.signUpEtPassword.text.toString()
            val phone = binding.signUpEtPhone.text.toString()
            val email = binding.signUpEtEmail.text.toString()
            val bio = binding.signUpEtBio.text.toString()



            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty() && bio.isNotEmpty()) {
                createStudent(email, password, phone, name, bio)
            } else {
                valueAnimator.start()

                Toast.makeText(this, "Please fill up Your Information :|", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun createStudent(
        email: String,
        password: String,
        phone: String,
        name: String,
        bio: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val student = Student().apply {
                        studentEmail = email
                        studentName = name
                        studentPhone = phone
                        studentId = user?.uid
                        studentBio = bio
                    }
                    createStudent(student)
//                    val uid = user!!.uid
                    val intent = Intent()
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    Toast.makeText(
                        this,
                        "Successfully registered : $user)",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Error registering, try again later :(",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun createStudent(student: Student) {

        db.collection("Students").document(student.studentId.toString()).set(student)
            .addOnSuccessListener { document ->


            }.addOnFailureListener {

            }
    }

}