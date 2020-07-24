package com.example.hitchapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hitchapp.EndlessRecyclerViewScrollListener
import com.example.hitchapp.R
import com.example.hitchapp.adapters.RequestsAdapter
import com.example.hitchapp.models.Request
import com.example.hitchapp.models.User
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.*

class RequestsFragment : Fragment() {
    private var rvRequests: RecyclerView? = null
    protected var swipeContainer: SwipeRefreshLayout? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    protected var layoutManager: LinearLayoutManager? = null
    private var totalRequests = 20
    private var currentUser: User? = null
    protected var pbLoading: ProgressBar? = null
    private val REQUEST_CODE = 20
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRequests = view.findViewById(R.id.rvRequests)
        pbLoading = view.findViewById(R.id.pbLoading)
        val itemDecoration: ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvRequests?.addItemDecoration(itemDecoration)

        // Create layout for one row in the list
        // Create the adapter
        allRequests = ArrayList()
        adapter = RequestsAdapter(context, allRequests)

        // Current user
        currentUser = ParseUser.getCurrentUser() as User

        // Set the adapter on the recycler view
        rvRequests?.adapter = adapter

        // Set the layout manager on the recycler view
        rvRequests?.layoutManager = LinearLayoutManager(context)
        layoutManager = rvRequests?.layoutManager as LinearLayoutManager?

        // Lookup the swipe container view
        swipeContainer = view.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout

        // Listener for refreshing timeline
        refreshListener()
        createScrollListener()

        // Gets all the rides for the timeline
        queryRequests()
    }

    protected fun createScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore: $page")
                loadMoreData()
            }
        }

        // Adds the scroll listener to the RV
        rvRequests?.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }

    // Loads more rides when we reach the bottom of TL
    protected fun loadMoreData() {
        Log.i(TAG, "Loading more data")
        totalRequests += NEW_REQUESTS
        val query = ParseQuery.getQuery(Request::class.java)
        query.include("driver")
        query.whereEqualTo("driver", currentUser)
        Log.i(TAG, "Here are the requests")

        // Set a limit
        query.limit = totalRequests

        // Sort by created at
        query.addDescendingOrder("createdAt")
        query.findInBackground(FindCallback { requests, e ->
            if (e != null) {
                Log.e(TAG, "Issue with getting rides", e)
                return@FindCallback
            }
            for (request in requests) {
                //Log.i(TAG, "OBJECT ID " + request.getRide().getDriver().getObjectId());
                //Log.i(TAG, "Ride: " + ride.getDescription() + ", username: " + ride.getUser().getUsername());
            }

            //Now we call setRefreshing(false) to signal refresh has finished
            swipeContainer?.isRefreshing = false

            // Add rides to adapter
            adapter?.setAll(requests)
            adapter?.notifyItemRangeInserted(requests.size - 5, requests.size)
        })
    }

    protected fun refreshListener() {
        // Setup refresh listener which triggers new data loading
        swipeContainer?.setOnRefreshListener { // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            queryRequests()
        }
        // Configure the refreshing colors
        swipeContainer?.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    // Gets rides and notifies adapter
    protected fun queryRequests() {
        pbLoading?.visibility = ProgressBar.VISIBLE
        Log.i(TAG, "Here are the requests")
        val query = ParseQuery.getQuery(Request::class.java)
        query.include("driver")
        query.whereEqualTo("driver", currentUser)
        //query.whereEqualTo("ride.driver.objectId",  User.createWithoutData(User.class, currentUser.getObjectId()));
        Log.i(TAG, currentUser?.objectId)


        // Set a limit
        query.limit = totalRequests

        // Sort by created at
        query.addDescendingOrder("createdAt")
        query.findInBackground(FindCallback { requests, e ->
            if (e != null) {
                Log.e(TAG, "Issue with getting requests", e)
                return@FindCallback
            }
            for (request in requests) {
                //Log.i(TAG, "Ride: " + ride.getDescription() + ", username: " + ride.getUser().getUsername());
                Log.i(TAG, "request" + request.requester)
            }
            // run a background job and once complete
            pbLoading?.visibility = ProgressBar.INVISIBLE

            //Now we call setRefreshing(false) to signal refresh has finished
            swipeContainer?.isRefreshing = false

            // Add rides to adapter
            adapter?.setAll(requests)
            adapter?.notifyDataSetChanged()
        })
    }

    companion object {
        private const val TAG = "RequestsFragment"
        var adapter: RequestsAdapter? = null
        var allRequests: List<Request>? = null
        protected const val NEW_REQUESTS = 5
    }
}