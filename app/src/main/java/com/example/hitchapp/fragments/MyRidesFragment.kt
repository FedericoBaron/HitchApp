package com.example.hitchapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hitchapp.helpers.EndlessRecyclerViewScrollListener
import com.example.hitchapp.R
import com.example.hitchapp.adapters.MyRidesAdapter
import com.example.hitchapp.helpers.SwipeHelper
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.viewmodels.MyRidesFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseUser
import java.util.*

class MyRidesFragment : Fragment() {
    protected var pbLoading: ProgressBar? = null
    private var rvRides: RecyclerView? = null
    protected var layoutManager: LinearLayoutManager? = null
    private var myRidesFragmentViewModel: MyRidesFragmentViewModel? = null
    protected var swipeContainer: SwipeRefreshLayout? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private val currentUser: User? = ParseUser.getCurrentUser() as User
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


        startViewModel()

        initRecyclerView()

        swipeHelper()


        // Show progress bar loading
        pbLoading?.visibility = View.VISIBLE

        // Lookup the swipe container view
        swipeContainer = view.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout

        // Listener for refreshing timeline
        refreshListener()

        // Listens for when you need to load more data
        createScrollListener()
    }

    val bundle = null
    var ride: Ride? = null
    private fun swipeHelper() {
        var swipeHelper: SwipeHelper = object : SwipeHelper(context) {
            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder, underlayButtons: MutableList<UnderlayButton>) {
                val position = viewHolder.adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    // Get the ride at the position, this won't work if the class is static
                    ride = adapter?.getItem(position)
                    Log.i(TAG, ride.toString())

                }
                underlayButtons.add(UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        UnderlayButtonClickListener {pos ->
                            Log.i(TAG, "pos: " + pos)
                        }
                ))

                if(currentUser?.objectId?.equals(ride?.driver?.objectId)!!)
                    underlayButtons.add(UnderlayButton(
                            "Edit",
                            0,
                            Color.parseColor("#FF9502"),
                            UnderlayButtonClickListener {
                                val position = viewHolder.adapterPosition

                                if (position != RecyclerView.NO_POSITION) {
                                    // Get the ride at the position, this won't work if the class is static
                                    val ride = adapter?.getItem(position)
                                    val bundle = Bundle()
                                    Log.i(TAG, ride.toString())
                                    bundle.putParcelable("ride", ride)
                                    val fragment: Fragment = EditRideFragment()
                                    fragment.arguments = bundle
                                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                                            .replace(R.id.flContainer, fragment)
                                            .addToBackStack(TAG)
                                            .commit()
                                }
                            }
                    ))
                underlayButtons.add(UnderlayButton(
                        "Chat",
                        0,
                        Color.parseColor("#C7C7CB"),
                        UnderlayButtonClickListener {
                            val position = viewHolder.adapterPosition

                            if (position != RecyclerView.NO_POSITION) {
                                // Get the ride at the position, this won't work if the class is static
                                val ride = adapter?.getItem(position)
                                val bundle = Bundle()
                                Log.i(TAG, ride.toString())
                                bundle.putParcelable("ride", ride)
                                val fragment: Fragment = MessagesFragment()
                                fragment.arguments = bundle
                                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                                        .replace(R.id.flContainer, fragment)
                                        .addToBackStack(TAG)
                                        .commit()
                            }
                        }
                ))
            }
        }
        swipeHelper.attachToRecyclerView(rvRides)
    }

    private fun initRecyclerView() {
        // gets list of rides from livedata
        allRides = ArrayList()
        adapter = context?.let { MyRidesAdapter(it, allRides as ArrayList<Ride>)}

        // Set the adapter on the recycler view
        rvRides?.adapter = adapter

        // Set the layout manager on the recycler view
        rvRides?.layoutManager = LinearLayoutManager(context)
        layoutManager = rvRides?.layoutManager as LinearLayoutManager?
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