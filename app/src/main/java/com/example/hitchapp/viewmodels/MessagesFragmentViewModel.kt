package com.example.hitchapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Ride
import com.example.hitchapp.repositories.MessageRepository

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
        Log.i(TAG, "here")

        var messages: List<Message>? = this.ride?.let { mRepo?.messagesQuery(it) }
        Log.i(TAG, messages.toString())
        mMessages?.postValue(messages)
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
    }
}