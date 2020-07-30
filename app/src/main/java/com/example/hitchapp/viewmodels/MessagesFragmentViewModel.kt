package com.example.hitchapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Ride
import com.example.hitchapp.repositories.MessageRepository
import com.parse.FindCallback
import java.util.*

class MessagesFragmentViewModel: ViewModel(){

    protected var mMessages: MutableLiveData<List<Message>>? = null
    protected var mRepo: MessageRepository? = null
    protected var totalMessages = 5
    protected var ride: Ride? = null


    fun init(ride: Ride?) {
        if (mMessages != null) {
            return
        }
        mRepo = MessageRepository.instance

        if (ride != null) {
            this.ride = ride
        }

        Log.i(TAG,"query  messages")
        queryMessages()

    }

    // Query messages from repo
    fun queryMessages() {
        val findCallback = FindCallback<Message>{ messages, e ->
            if(e == null){
                Log.i(TAG, messages.toString())
                mMessages?.postValue(messages)
            }
            else{
                Log.i(TAG, "Error querying for rides")
            }
        }

        ride?.let { mRepo?.messagesQuery(it, totalMessages, findCallback) }
    }

    // Loads more rides when we reach the bottom of TL
    fun loadMoreData() {
        // Adds more rides to the amount of rides queried in the repository
        totalMessages += OLD_MESSAGES

        // Query rides from repo
        queryMessages()
    }

    val messages: LiveData<List<Message>>
        get() {
            if (mMessages == null) {
                mMessages = MutableLiveData()
            }
            return mMessages as MutableLiveData<List<Message>>
        }

    companion object {
        const val TAG = "MessagesFragmentViewMod"
        protected const val OLD_MESSAGES = 5
    }
}