package com.example.hitchapp.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hitchapp.R
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Request
import com.example.hitchapp.models.User
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseUser
import java.math.BigInteger
import java.security.MessageDigest

class MessagesAdapter(private val mContext: Context, private val mUserId: String, private val mMessages: MutableList<Message>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_message, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = mMessages[position]
        // Gets the person who's logged in
        val user = ParseUser.getCurrentUser() as User
        val isMe = message.author!!.objectId == user.objectId
        if (isMe) {
            holder.imageMe.visibility = View.VISIBLE
            holder.imageOther.visibility = View.GONE
            holder.body.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        } else {
            holder.imageOther.visibility = View.VISIBLE
            holder.imageMe.visibility = View.GONE
            holder.body.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        }
        val profileView = if (isMe) holder.imageMe else holder.imageOther
        var profile: ParseFile? = null
        try {
            val userPic = message.author!!.fetch() as User
            profile = userPic.profilePicture
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (profile != null) {
            Glide.with(mContext)
                    .load(profile.url)
                    .fitCenter()
                    .circleCrop()
                    .into(profileView)
        }
        //Glide.with(mContext).load(getProfileUrl(message.getAuthor().getObjectId())).into(profileView);
        holder.body.text = message.content
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    // Clean all elements of the recycler
    fun clear() {
        mMessages.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(list: Collection<Message?>) {
        mMessages.addAll(list as Collection<Message>)
        notifyDataSetChanged()
    }

    fun setAll(list: List<Message?>?) {
        clear()
        list?.let { addAll(it) }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageOther: ImageView = itemView.findViewById<View>(R.id.ivProfileOther) as ImageView
        var imageMe: ImageView = itemView.findViewById<View>(R.id.ivProfileMe) as ImageView
        var body: TextView = itemView.findViewById<View>(R.id.tvBody) as TextView

    }

    companion object {
        // Create a gravatar image based on the hash value obtained from userId
        private fun getProfileUrl(userId: String): String {
            var hex = ""
            try {
                val digest = MessageDigest.getInstance("MD5")
                val hash = digest.digest(userId.toByteArray())
                val bigInt = BigInteger(hash)
                hex = bigInt.abs().toString(16)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "https://www.gravatar.com/avatar/$hex?d=identicon"
        }
    }

}