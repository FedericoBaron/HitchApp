package com.example.hitchapp.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.hitchapp.helpers.EndlessRecyclerViewScrollListener
import com.example.hitchapp.models.Ride
import com.example.hitchapp.viewmodels.HomeFragmentViewModel
import com.example.hitchapp.viewmodels.SearchResultsFragmentViewModel
import com.parse.ParseGeoPoint

class SearchResultsFragment: HomeFragment() {

    private var searchResultsFragmentViewModel: SearchResultsFragmentViewModel? = null
    private var from: ParseGeoPoint? = null
    private var distance: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Unwrap the arguments passed in via bundle, using its simple name as a key
        val bundle = this.arguments
        from = bundle?.getParcelable<Parcelable>("from") as ParseGeoPoint
        distance = bundle?.getInt("distance")
        Log.i(TAG, "w here " + from.toString())
        super.onViewCreated(view, savedInstanceState)


    }
    override fun startViewModel() {
        Log.i(TAG, "w here again " + from.toString())

        searchResultsFragmentViewModel = ViewModelProviders.of(this).get(SearchResultsFragmentViewModel::class.java)
        from?.let { distance?.let { it1 -> searchResultsFragmentViewModel?.init(it, it1) } }
        // Create the observer which updates the UI.
        val ridesObserver: Observer<List<Ride>?> = Observer { rides -> // Update the UI
            if (rides != null) {
                for (ride in rides) {
                    Log.i(TAG, "Ride: " + ride?.driver)
                }
            }
            adapter?.setAll(rides)
            adapter?.notifyDataSetChanged()
            pbLoading?.visibility = View.GONE
            swipeContainer?.isRefreshing = false
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        searchResultsFragmentViewModel?.rides?.observe(viewLifecycleOwner, ridesObserver)
    }

    override fun refreshListener() {
        // Setup refresh listener which triggers new data loading
        swipeContainer?.setOnRefreshListener { // Your code to refresh the list here.
            searchResultsFragmentViewModel?.queryRides()
        }
        // Configure the refreshing colors
        swipeContainer?.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    // Listens for when you need to load more data
    override fun createScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore: $page")
                searchResultsFragmentViewModel?.loadMoreData()
            }
        }

        // Adds the scroll listener to the RV
        rvRides?.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }

    companion object {
        private const val TAG = "SearchResultsFragment"
    }
}