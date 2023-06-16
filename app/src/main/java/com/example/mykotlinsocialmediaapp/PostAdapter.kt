package com.example.mykotlinsocialmediaapp;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mykotlinsocialmediaapp.model.Post
import com.google.android.material.imageview.ShapeableImageView

class PostAdapter(private val myPosts: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.myViewHolder>() {

//    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

//        context=parent.context
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.post_custom_item, parent, false)
        return myViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return myPosts.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val post = myPosts[position]

//        val currentItem= myPosts[position]
//        holder.title.setText(currentItem.title)

        holder.name.text = post.name
        holder.title.text = post.postBody

        Glide
            .with(holder.postImage.context)
            .load(post.postImg).fitCenter()
            .centerCrop()
            .placeholder(R.drawable.photo)
            .into(holder.postImage);


        Glide
            .with(holder.userImage.context)
            .load(post.userImg).fitCenter()
            .centerCrop().circleCrop()
            .placeholder(R.drawable.user)
            .into(holder.userImage);


    }


    class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.text_user_name)
        val title: TextView = itemView.findViewById(R.id.text_title)
        val postImage: ImageView = itemView.findViewById(R.id.image_post)
        val userImage: ImageView = itemView.findViewById(R.id.userImg)

    }

}