package com.hikespot.app.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hikespot.app.R
import com.hikespot.app.databinding.ItemPersonalBinding
import com.hikespot.app.model.Post
import com.squareup.picasso.Picasso

class PersonalPostAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PersonalPostAdapter.PostViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, post: Post)
        fun onItemSettingClick(position: Int, post: Post)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPersonalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, mListener!!)
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    class PostViewHolder(
        private val binding: ItemPersonalBinding,
        private val mListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                Picasso.get().load(post.userProfileImage)
                    .placeholder(R.drawable.placeholder)
                    .resize(200,200)
                    .centerCrop()
                    .into(userProfilePersonalImage)
                textViewUsernameItemPersonal.text = post.username
                textViewLocationItemPersonal.text = post.location
                if (post.photoUrl.isEmpty()) {
                    imageViewPostItemPersonal.visibility = View.GONE
                } else {
                    imageViewPostItemPersonal.visibility = View.VISIBLE
                    Picasso.get().load(post.photoUrl)
                        .placeholder(R.drawable.placeholder)
                        .resize(600,400)
                        .centerCrop()
                        .into(imageViewPostItemPersonal)
                }

                textViewDescriptionItemPersonal.text = post.description

                binding.root.setOnClickListener {
                    mListener.onItemClick(layoutPosition, post)
                }

               binding.buttonSettingPostPersonal.setOnClickListener {
                   mListener.onItemSettingClick(layoutPosition,post)
               }
            }
        }

    }


}
