package com.example.hitchapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hitchapp.models.Ride
import com.example.hitchapp.repositories.RideRepository

class HomeFragmentViewModel : ViewModel() {
    protected var mRides: MutableLiveData<List<Ride>>? = null
    protected var mRepo: RideRepository? = null
    protected var totalRides = 5

    fun init() {
        if (mRides != null) {
            return
        }
        mRepo = RideRepository.getInstance()
        queryRides()
    }

    val rides: LiveData<List<Ride>>
        get() {
            if (mRides == null) {
                mRides = MutableLiveData()
            }
            return mRides as MutableLiveData<List<Ride>>
        }

    // Query rides from repo
    fun queryRides() {
        mRepo!!.ridesQuery(totalRides) { objects, e ->
            Log.i(TAG, objects.toString())
            mRides!!.postValue(objects)
        }
    }

    // Loads more rides when we reach the bottom of TL
    fun loadMoreData() {
        // Adds more rides to the amount of rides queried in the repository
        totalRides = totalRides + NEW_RIDES

        // Query rides from repo
        queryRides()
    }

    companion object {
        const val TAG = "HomeFragmentViewModel"
        protected const val NEW_RIDES = 5
    }
}