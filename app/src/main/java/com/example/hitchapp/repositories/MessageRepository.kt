package com.example.hitchapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

class MessageRepository {

    // Gets messages
    fun messagesQuery(ride: Ride, messages: Int, findCallback: FindCallback<Message>?) {
        val query = ParseQuery.getQuery(Message::class.java)

        // Get only rides that are scheduled
        query.whereEqualTo(Message.KEY_RIDE_ID, ride.objectId)

        // Set a limit
        query.limit = messages

        // Sort by departure date
        query.orderByDescending("createdAt")

        // Finds the posts asynchronously
        query.findInBackground(findCallback)
    }

    companion object {
        const val TAG = "MessageRepository"
        @JvmStatic
        var instance: MessageRepository? = null
            get() {
                if (field == null) {
                    field = MessageRepository()
                }
                return field
            }
            private set
    }
}