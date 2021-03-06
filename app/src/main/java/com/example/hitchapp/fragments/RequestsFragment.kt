package com.example.hitchapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hitchapp.helpers.EndlessRecyclerViewScrollListener
import com.example.hitchapp.R
import com.example.hitchapp.adapters.RequestsAdapter
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Request
import com.example.hitchapp.viewmodels.RequestsFragmentViewModel
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import java.util.*

class RequestsFragment : Fragment() {
    private var rvRequests: RecyclerView? = null
    protected var swipeContainer: SwipeRefreshLayout? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    protected var layoutManager: LinearLayoutManager? = null
    protected var pbLoading: ProgressBar? = null
    private var mRequestsFragmentViewModel: RequestsFragmentViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Show toolbar menu options
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRequests = view.findViewById(R.id.rvRequests)
        pbLoading = view.findViewById(R.id.pbLoading)
        val itemDecoration: ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvRequests?.addItemDecoration(itemDecoration)

        startViewModel()

        // Show progress bar loading
        pbLoading?.visibility = View.VISIBLE
        initRecyclerView()

        // Lookup the swipe container view
        swipeContainer = view.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout

        // Listener for refreshing timeline
        refreshListener()

        // Listener for infinite scrolling
        createScrollListener()

        // Live query for updating requests
        liveQuery()

    }

    // Hide search from toolbar
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.miSearch)
        item.isVisible = false
    }

    protected fun startViewModel() {
        mRequestsFragmentViewModel = ViewModelProviders.of(this).get(RequestsFragmentViewModel::class.java)
        mRequestsFragmentViewModel?.init()

        // Create the observer which updates the UI.
        val requestsObserver: Observer<List<Request>?> = Observer { requests -> // Update the UI
            if (requests != null) {
                for (request in requests) {
                    Log.i(TAG, "Request: " + request?.driver + ", username: " + request?.requester)
                }
            }
            adapter?.setAll(requests)
            adapter?.notifyDataSetChanged()
            pbLoading?.visibility = View.GONE
            swipeContainer?.isRefreshing = false
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mRequestsFragmentViewModel?.requests?.observe(viewLifecycleOwner, requestsObserver)
    }

    protected fun initRecyclerView() {

        // gets list of rides from livedata
        allRequests = ArrayList()
        adapter = context?.let { RequestsAdapter(it, allRequests as ArrayList<Request>) }

        // Set the adapter on the recycler view
        rvRequests?.adapter = adapter

        // Set the layout manager on the recycler view
        rvRequests?.layoutManager = LinearLayoutManager(context)
        layoutManager = rvRequests?.layoutManager as LinearLayoutManager?
    }

    private fun liveQuery(){

        //Build Live Query Client
        val parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()

        //Build Query
        var parseQuery = ParseQuery.getQuery<Request>("Request")

        //Build Live Query Listener
        var subscriptionHandling: SubscriptionHandling<Request> = parseLiveQueryClient.subscribe(parseQuery)
        subscriptionHandling.handleSubscribe {
            Log.i(TAG, "subs")
        }

        subscriptionHandling.handleError { query, exception ->
            Log.i(TAG,"exception")
        }
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE){ parseQuery: ParseQuery<Request>, request: Request ->
            val handler = Handler(Looper.getMainLooper())
            handler.post(Runnable {
                mRequestsFragmentViewModel?.queryRequests()
                rvRequests?.scrollToPosition(0)
            })
        }
    }

    protected fun createScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore: $page")
                mRequestsFragmentViewModel?.loadMoreData()
            }
        }

        // Adds the scroll listener to the RV
        rvRequests?.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }

    protected fun refreshListener() {
        // Setup refresh listener which triggers new data loading
        swipeContainer?.setOnRefreshListener { // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            mRequestsFragmentViewModel?.queryRequests()
        }
        // Configure the refreshing colors
        swipeContainer?.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    companion object {
        private const val TAG = "RequestsFragment"
        var adapter: RequestsAdapter? = null
        var allRequests: List<Request>? = null
    }
}