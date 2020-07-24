package com.example.hitchapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.Ride
import com.example.hitchapp.repositories.RideRepository
import com.example.hitchapp.repositories.RideRepository.Companion.instance
import com.parse.FindCallback

class MyRidesFragmentViewModel : ViewModel() {
    protected var mRides: MutableLiveData<List<Ride>>? = null
    protected var mRepo: RideRepository? = null
    protected var totalRides = 5
    fun init() {
        if (mRides != null) {
            return
        }
        mRepo = instance
        queryMyRides()
    }

    val rides: LiveData<List<Ride>>
        get() {
            if (mRides == null) {
                mRides = MutableLiveData()
            }
            return mRides as MutableLiveData<List<Ride>>
        }

    // Query rides from repo
    fun queryMyRides() {
        val findCallback = FindCallback<Ride>{ rides, e ->
            if(e == null){
                Log.i(TAG, rides.toString())
                mRides?.postValue(rides)
            }
            else{
                Log.i(HomeFragmentViewModel.TAG, "Error querying for rides")
            }
        }
        mRepo?.myRidesQuery(totalRides, findCallback)
    }

    // Loads more rides when we reach the bottom of TL
    fun loadMoreData() {
        Log.i(TAG, "load more data")
        // Adds more rides to the amount of rides queried in the repository
        totalRides += NEW_RIDES

        // Query rides from repo
        queryMyRides()
    }

    companion object {
        const val TAG = "MyRidesFragmentViewMode"
        protected const val NEW_RIDES = 5
    }
}