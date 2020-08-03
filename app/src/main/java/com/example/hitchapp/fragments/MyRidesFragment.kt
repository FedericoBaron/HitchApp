package com.example.hitchapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hitchapp.R
import com.example.hitchapp.adapters.MyRidesAdapter
import com.example.hitchapp.helpers.EndlessRecyclerViewScrollListener
import com.example.hitchapp.helpers.SwipeHelper
import com.example.hitchapp.helpers.SwipeHelper.UnderlayButtonClickListener
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.viewmodels.MyRidesFragmentViewModel
import com.parse.ParseCloud
import com.parse.ParseException
import com.parse.ParseInstallation
import com.parse.ParseUser
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.indices
import kotlin.collections.set

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


        // show toolbar menu
        setHasOptionsMenu(true)

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

    // Hide search from toolbar
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.miSearch)
        item.isVisible = false
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
                        "Leave",
                        0,
                        Color.parseColor("#FF3C30"),
                        UnderlayButtonClickListener {
                            val position = viewHolder.adapterPosition

                            if (position != RecyclerView.NO_POSITION) {
                                // Get the ride at the position, this won't work if the class is static
                                val ride = adapter?.getItem(position)
                                if (ride != null) {
                                    leaveRide(ride)
                                }
                            }
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

    private fun leaveRide(ride: Ride){

        // Gets list of participants
        var participantList = ride.getList<User>(Ride.KEY_PARTICIPANTS)

        // Goes through each participant and finds current user index
        for (i in participantList?.indices!!) {
            try {

                // Gets the participant at position i
                participantList[i].fetch()

                // checks if current user is at index i
                if(participantList[i].objectId == currentUser?.objectId) {
                    // removes current user from the ride
                    var participants = ride.participants
                    participants?.remove(i);
                    ride.participants = participants
                    ride.seatsAvailable++
                    break
                }
            } catch (e: ParseException) {
                Log.e(TAG, "exception fetching participants", e)
            }
        }

        // If the driver is the user that means the ride got cancelled
        if(currentUser?.objectId == ride.driver?.objectId && ride.state != "Finished"){
            ride.state = "Cancelled"
            Log.i(TAG, "Cancelled")
        }

        // If the size is 1 it means the ride is going to be empty and should be deleted
        if(participantList?.size == 1)
            myRidesFragmentViewModel?.delete(ride)
        // Otherwise we save the ride and query for the rides without the deleted one
        else{
            myRidesFragmentViewModel?.save(ride)
        }

        myRidesFragmentViewModel?.queryMyRides()

        Toast.makeText(context, "You left the ride", Toast.LENGTH_SHORT).show()
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
                    Log.i(TAG, "Ride: " + ride?.driver)
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




