package com.example.mykotlinsocialmediaapp

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.mykotlinsocialmediaapp.databinding.FragmentPostBinding
import com.example.mykotlinsocialmediaapp.model.Comment
import com.example.mykotlinsocialmediaapp.model.Post
import com.example.mykotlinsocialmediaapp.model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import id.zelory.compressor.Compressor.compress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class PostFragment : Fragment() {


    private lateinit var binding: FragmentPostBinding

    private val db = FirebaseFirestore.getInstance()
    val PICK_IMAGE_REQUEST = 1

    var storageRef = Firebase.storage.reference

    var selectedImageUri: Uri? = null
    var path: String? = ""
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var userUid: String
    var userImage: String = ""
    var userName: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        userUid = sharedPreferencesHelper.getToken() ?: FirebaseAuth.getInstance().currentUser!!.uid

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("Students").document(userUid).get().addOnSuccessListener { querySnapshot ->

            val student = querySnapshot.toObject(Student::class.java)

            userImage = student?.studentImg.toString()
            userName = student?.studentName.toString()
            Log.d(TAG, "Student Information ==> : ${student}")
            Log.d(TAG, "Student img ==> : ${userImage}")
            Log.d(TAG, "Student name ==> : ${userName}")


        }

        id
        binding.btnAddImage.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        val post = Post(
            name = userName,
            postId = "",
            postImg = path,
            postFile = "file-url",
            stdId = userId,
            createdDate = "2023-06-16",
            userImg = userImage,
            postBody = "",
            isLiked = false,
            postComments = ArrayList<Comment>()
        )
        binding.btnPost.setOnClickListener {


            if (selectedImageUri != null || binding.editPostContent.text.isNotEmpty()) {


                val documentRef = db.collection("posts").document()
                CreatePost(documentRef, post)

                binding.editPostContent.setText("")
                binding.imgSelected.setImageResource(R.drawable.add_img)
                binding.progress.isVisible = true

            } else {

                Toast.makeText(
                    activity,
                    "Please Add image or Text to add post",
                    Toast.LENGTH_LONG
                )
                    .show()

            }


        }

    }

    private fun CreatePost(
        documentRef: DocumentReference,
        post: Post
    ) {

        documentRef.set(
            post.copy(
                postId = documentRef.id,
                postBody = binding.editPostContent.text.toString(),
                postImg = path,
                userImg = userImage,
                name = userName
            )
        )
            .addOnSuccessListener { documentReference ->

                uploadImage(selectedImageUri)

                val postId = documentRef.id
                val postBody = binding.editPostContent.text.toString()
                val postImage = path
                post.postId = postId
                post.postBody = postBody
                post.postImg = postImage

                Toast.makeText(
                    activity,
                    "Post Created Successfully",
                    Toast.LENGTH_LONG
                )
                    .show()

                binding.progress.isVisible = false

            }.addOnFailureListener {

            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data



            binding.imgSelected.setImageURI(selectedImageUri)

//            compressImage(selectedImageUri!!)


        }
    }

    private fun uploadImage(imagePath: Uri?) {


        imagePath?.let {
            storageRef.child("images/${imagePath.lastPathSegment}")
                .putFile(

                    it
                ).addOnSuccessListener {

                    val imgLinkPath = it.storage.downloadUrl.addOnSuccessListener {
                        path = it.toString()
                        Log.d(TAG, "path: ${path}")

                    }
                    Toast.makeText(
                        activity,
                        "Image Uploaded Successfully ==> ${imgLinkPath}",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    Log.d(TAG, "uploadImage: ${imgLinkPath}")
                }.addOnFailureListener {
                    Toast.makeText(
                        activity,
                        "Image Uploaded failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
        }

    }


    private fun compressImage(img: Uri) {

        try {
            GlobalScope.launch {
                val compressedImageFile = compress(
                    context = requireContext(),
                    imageFile = File(img?.path)

                )
                binding.imgSelected.setImageBitmap(BitmapFactory.decodeFile(compressedImageFile.absolutePath))

            }


        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

}