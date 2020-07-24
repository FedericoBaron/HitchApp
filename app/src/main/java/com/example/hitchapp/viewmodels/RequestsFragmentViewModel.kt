package com.example.hitchapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.Request
import com.example.hitchapp.models.Ride
import com.example.hitchapp.repositories.RequestRepository
import com.parse.FindCallback

class RequestsFragmentViewModel: ViewModel(){

    protected var mRequests: MutableLiveData<List<Request>>? = null
    protected var mRepo: RequestRepository? = null
    protected var totalRequests = 5

    fun init() {
        if (mRequests != null) {
            return
        }
        mRepo = RequestRepository.instance
        queryRequests()
    }

    val requests: LiveData<List<Request>>
        get() {
            if (mRequests == null) {
                mRequests = MutableLiveData()
            }
            return mRequests as MutableLiveData<List<Request>>
        }


    fun queryRequests() {

        val findCallback = FindCallback<Request>{ requests, e ->
            if(e == null){
                Log.i(HomeFragmentViewModel.TAG, requests.toString())
                mRequests?.postValue(requests)
            }
            else{
                Log.i(HomeFragmentViewModel.TAG, "Error querying for rides")
            }
        }

        mRepo?.requestsQuery(totalRequests, findCallback)
    }

    // Loads more requests when we reach the bottom of TL
    fun loadMoreData() {
        // Adds more rides to the amount of rides queried in the repository
        totalRequests += NEW_REQUESTS

        // Query rides from repo
        queryRequests()
    }

    companion object {
        const val TAG = "RequestsFragmentViewMod"
        protected const val NEW_REQUESTS = 5
    }
}