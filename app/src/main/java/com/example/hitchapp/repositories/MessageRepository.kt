package com.example.hitchapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Ride
import com.parse.ParseException

class MessageRepository {

    // Gets messages
    fun messagesQuery(ride: Ride): List<Message>{
        val messagesList = ride?.getList<Message>("messages")
        for (i in messagesList?.indices!!) {
            try {
                messagesList[i]?.fetchIfNeeded<Message>()
            } catch (e: ParseException) {
                Log.e(TAG, "exception fetching messages", e)
            }
        }
        return messagesList
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