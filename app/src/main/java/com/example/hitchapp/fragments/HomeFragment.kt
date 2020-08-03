package com.example.hitchapp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hitchapp.R
import com.example.hitchapp.adapters.RidesAdapter
import com.example.hitchapp.helpers.EndlessRecyclerViewScrollListener
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.viewmodels.HomeFragmentViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.parse.ParseCloud
import com.parse.ParseInstallation
import com.parse.ParseUser
import org.json.JSONException
import org.json.JSONObject
import java.util.*

open class HomeFragment : Fragment() {
    protected var rvRides: RecyclerView? = null
    protected var swipeContainer: SwipeRefreshLayout? = null
    protected var scrollListener: EndlessRecyclerViewScrollListener? = null
    protected var layoutManager: LinearLayoutManager? = null
    protected var pbLoading: ProgressBar? = null
    private var mHomeFragmentViewModel: HomeFragmentViewModel? = null
    private val map: GoogleMap? = null
    private val permissionFineLocation=android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionCoarseLocation=android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val currentUser: User? = ParseUser.getCurrentUser() as User

    private val REQUEST_CODE_LOCATION=100

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //testNotif()
        //testNotif2()


        rvRides = view.findViewById(R.id.rvRides)
        pbLoading = view.findViewById(R.id.pbLoading)
        val itemDecoration: ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvRides?.addItemDecoration(itemDecoration)
        startViewModel()

        if (validatePermissionsLocation()){
            getMyLocation()
        }
        else{
            requestPermissions()
        }

        // Show progress bar loading
        pbLoading?.visibility = View.VISIBLE
        initRecyclerView()

        // Lookup the swipe container view
        swipeContainer = view.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout

        // Listener for refreshing timeline
        refreshListener()

        // Listens for when you need to load more data
        createScrollListener()
    }

    private fun testNotif(){
        val payload = JSONObject()

        try {
            payload.put("sender", ParseInstallation.getCurrentInstallation().installationId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val data: HashMap<String, String?> = HashMap()
        data["customData"] = payload.toString()

        ParseCloud.callFunctionInBackground<Any>("pingReply", data)
    }

    private fun testNotif2(){
        val payload = JSONObject()

        try {
            payload.put("userId", currentUser?.objectId.toString())
            Log.i(TAG, "ID IS" + currentUser?.objectId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val data: HashMap<String, String> = HashMap()
        data["customData"] = payload.toString()

        ParseCloud.callFunctionInBackground<Any>("sendPushNotification", data)
    }



    protected open fun startViewModel() {
        mHomeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
        mHomeFragmentViewModel?.init()

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
        mHomeFragmentViewModel?.rides?.observe(viewLifecycleOwner, ridesObserver)
    }

    protected fun initRecyclerView() {

        // gets list of rides from livedata
        allRides = ArrayList()
        adapter = context?.let { RidesAdapter(it, allRides as ArrayList<Ride>) }

        // Set the adapter on the recycler view
        rvRides?.adapter = adapter

        // Set the layout manager on the recycler view
        rvRides?.layoutManager = LinearLayoutManager(context)
        layoutManager = rvRides?.layoutManager as LinearLayoutManager?

        //mHomeFragmentViewModel?.queryRides()
    }

    private fun requestPermissions(){
        val contextProvider = activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permissionFineLocation) }

        if(contextProvider!!){
            Toast.makeText(activity?.applicationContext, "Permission is required to obtain location", Toast.LENGTH_SHORT).show()
        }
        permissionRequest()
    }
    private fun permissionRequest(){
        activity?.let { ActivityCompat.requestPermissions(it, arrayOf(permissionFineLocation, permissionCoarseLocation), REQUEST_CODE_LOCATION) }
    }
    private fun validatePermissionsLocation():Boolean{
        val fineLocationAvailable= activity?.applicationContext?.let { ActivityCompat.checkSelfPermission(it, permissionFineLocation) } == PackageManager.PERMISSION_GRANTED
        val coarseLocationAvailable= activity?.applicationContext?.let { ActivityCompat.checkSelfPermission(it, permissionCoarseLocation) } ==PackageManager.PERMISSION_GRANTED

        return fineLocationAvailable && coarseLocationAvailable
    }

    protected open fun refreshListener() {
        // Setup refresh listener which triggers new data loading
        swipeContainer?.setOnRefreshListener { // Your code to refresh the list here.
            mHomeFragmentViewModel?.queryRides()
        }
        // Configure the refreshing colors
        swipeContainer?.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    // Listens for when you need to load more data
    protected open fun createScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore: $page")
                mHomeFragmentViewModel?.loadMoreData()
            }
        }

        // Adds the scroll listener to the RV
        rvRides?.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }

    @SuppressWarnings("MissingPermission")
    //@NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getMyLocation() {
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true
        val locationClient = activity?.applicationContext?.let { LocationServices.getFusedLocationProviderClient(it) }
        mHomeFragmentViewModel?.getMyLocation(locationClient)

    }


    companion object {
        private const val TAG = "HomeFragment"
        var adapter: RidesAdapter? = null
        var allRides: List<Ride>? = null
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
        private const val PERMISSION_REQUEST_ACCESS_COARSE_LOCATION= 100
    }
}