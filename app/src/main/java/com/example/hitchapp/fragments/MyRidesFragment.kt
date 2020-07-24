package com.example.hitchapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hitchapp.EndlessRecyclerViewScrollListener
import com.example.hitchapp.R
import com.example.hitchapp.adapters.MyRidesAdapter
import com.example.hitchapp.models.Ride
import com.example.hitchapp.viewmodels.MyRidesFragmentViewModel
import java.util.*

class MyRidesFragment : Fragment() {
    protected var pbLoading: ProgressBar? = null
    private var rvRides: RecyclerView? = null
    protected var layoutManager: LinearLayoutManager? = null
    private var myRidesFragmentViewModel: MyRidesFragmentViewModel? = null
    protected var swipeContainer: SwipeRefreshLayout? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvRides = view.findViewById(R.id.rvRides)
        pbLoading = view.findViewById(R.id.pbLoading)
        val itemDecoration: ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvRides?.addItemDecoration(itemDecoration)

        // gets list of rides from livedata
        allRides = ArrayList()
        adapter = context?.let { MyRidesAdapter(it, allRides as ArrayList<Ride>)}

        // Set the adapter on the recycler view
        rvRides?.adapter = adapter

        // Set the layout manager on the recycler view
        rvRides?.layoutManager = LinearLayoutManager(context)
        layoutManager = rvRides?.layoutManager as LinearLayoutManager?
        startViewModel()

        // Show progress bar loading
        pbLoading?.visibility = View.VISIBLE

        // Lookup the swipe container view
        swipeContainer = view.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout

        // Listener for refreshing timeline
        refreshListener()

        // Listens for when you need to load more data
        createScrollListener()
    }

    protected fun startViewModel() {
        myRidesFragmentViewModel = ViewModelProviders.of(this).get(MyRidesFragmentViewModel::class.java)
        myRidesFragmentViewModel?.init()

        // Create the observer which updates the UI.
        val ridesObserver: Observer<List<Ride?>?> = Observer { rides -> // Update the UI
            if (rides != null) {
                for (ride in rides) {
                    Log.i(TAG, "Ride: " + ride?.driver + ", username: " + ride?.messages)
                }
            }
            adapter?.setAll(rides)
            adapter?.notifyDataSetChanged()
            pbLoading?.visibility = View.GONE
            swipeContainer?.isRefreshing = false
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        myRidesFragmentViewModel?.rides?.observe(viewLifecycleOwner, ridesObserver)
    }

    protected fun refreshListener() {
        // Setup refresh listener which triggers new data loading
        swipeContainer?.setOnRefreshListener { // Your code to refresh the list here.
            myRidesFragmentViewModel?.queryMyRides()
        }
        // Configure the refreshing colors
        swipeContainer?.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    // Listens for when you need to load more data
    protected fun createScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore: $page")
                myRidesFragmentViewModel?.loadMoreData()
            }
        }
        // Adds the scroll listener to the RV
        rvRides?.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }

    companion object {
        private const val TAG = "MyRidesFragment"
        var adapter: MyRidesAdapter? = null
        var allRides: List<Ride>? = null
    }
}