package com.example.mykotlinsocialmediaapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mykotlinsocialmediaapp.databinding.FragmentProfileBinding
import com.example.mykotlinsocialmediaapp.model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext
import java.io.File


class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding

    private val db = FirebaseFirestore.getInstance()

    private val personCollectionRef = Firebase.firestore.collection("Students")
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var token: String

    val PICK_IMAGE_REQUEST = 2

    private var storageRef = Firebase.storage.reference

    private var selectedImageUri: Uri? = null
    private var path: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        token = sharedPreferencesHelper.getToken() ?: FirebaseAuth.getInstance().currentUser!!.uid
        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        if (token.isNotEmpty()) {

            db.collection("Students").document(token).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val student = document.toObject(Student::class.java)
                        if (student != null) {

                            val studentName = student.studentName
                            val studentEmail = student.studentEmail
                            val studentPhone = student.studentPhone
                            val studentId = student.studentId
                            val studentImg = student.studentImg
                            val studentBio = student.studentBio
                            val likedPostIds = student.likedPostIds


                            _binding.textName.setText(studentName)
                            _binding.textEmail.setText(studentEmail)
                            _binding.textBio.setText(studentBio)
                            _binding.textPhone.setText(studentPhone)


                            Glide.with(this)
                                .load(studentImg)
                                .centerInside()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.user)
                                .into(_binding.imageProfile)

                            Toast.makeText(
                                requireContext(),
                                "data is --> ${student}}",
                                Toast.LENGTH_SHORT
                            ).show()


                            Log.d("StudentData", "Student Information: ${student}")


                        }

                    } else {
                        // Document does not exist
                        println("Student document does not exist.")
                    }


                }

            Toast.makeText(requireContext(), "Token is --> ${token}", Toast.LENGTH_SHORT).show()


        } else {
            Toast.makeText(requireContext(), "Token Is null ", Toast.LENGTH_SHORT).show()
            // Token is not available
        }


        _binding.btnEdit.setOnClickListener {

            _binding.textName.isEnabled = true
            _binding.textEmail.isEnabled = true
            _binding.textBio.isEnabled = true
            _binding.textPhone.isEnabled = true
            _binding.textPhone.isEnabled = true
            _binding.btnEditImage.isVisible = true
            _binding.btnSaveEdit.isVisible = true
            _binding.btnEdit.isVisible = false

        }


        _binding.btnEditImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
            }


            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        _binding.btnSaveEdit.setOnClickListener {
            val newPersonMap = getNewStudentMap()
            updateStudent(newPersonMap)

        }

    }

    private fun updateStudent(newPersonMap: Map<String, Any>) =
        db.collection("Students").document(token).set(
            newPersonMap,
            SetOptions.mergeFields(
                listOf(
                    "studentName",
                    "studentEmail",
                    "studentPhone",
                    "studentBio",
                    "studentImg",
                )
            )

        ).addOnSuccessListener {

            _binding.textName.isEnabled = false
            _binding.textEmail.isEnabled = false
            _binding.textBio.isEnabled = false
            _binding.textPhone.isEnabled = false
            _binding.textPhone.isEnabled = false
            _binding.btnEditImage.isVisible = false
            _binding.btnSaveEdit.isVisible = false
            _binding.btnEdit.isVisible = true

            Toast.makeText(
                activity,
                " Update Successfully",
                Toast.LENGTH_LONG
            )
                .show()


        }.addOnFailureListener {

            Toast.makeText(
                activity,
                "update Failure",
                Toast.LENGTH_LONG
            )
                .show()

        }


    private fun uploadImage(imagePath: Uri?) {


        storageRef.child("UsersProfileImage/${imagePath?.path}")
            .putFile(

                imagePath!!
            ).addOnSuccessListener {

                val imgLinkPath = it.storage.downloadUrl.addOnSuccessListener {
                    path = it.toString()
                    Log.d(ContentValues.TAG, "path: ${path}")


                }

                Log.d(ContentValues.TAG, "uploadImage: ${imgLinkPath}")
            }.addOnFailureListener {
                Toast.makeText(
                    activity,
                    "Image Uploaded failed.",
                    Toast.LENGTH_SHORT,
                ).show()

            }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            _binding.imageProfile.setImageURI(selectedImageUri)

//            GlobalScope.launch {
//                val compressedImageFile = Compressor.compress(
//                    requireContext(),
//                    File(selectedImageUri?.lastPathSegment)
//                )
//            }

            uploadImage(selectedImageUri)


        }
    }

    private fun getNewStudentMap(): Map<String, Any> {
        val name = _binding.textName.text.toString()
        val email = _binding.textEmail.text.toString()
        val phone = _binding.textPhone.text.toString()
        val bio = _binding.textBio.text.toString()
        val img = path
        val likeList = emptyList<String>()

        val map = mutableMapOf<String, Any>()
        if (name.isNotEmpty()) {
            map["studentName"] = name
        }
        if (email.isNotEmpty()) {
            map["studentEmail"] = email
        }
        if (phone.isNotEmpty()) {
            map["studentPhone"] = phone
        }
        if (bio.isNotEmpty()) {
            map["studentBio"] = bio
        } else {
            map["studentBio"] = "bio bio"
        }
        if (img != null) {
            map["studentImg"] = img
        }
        if (likeList.isNotEmpty()) {
            map["likedPostIds"] = likeList
        }

        return map
    }


}





