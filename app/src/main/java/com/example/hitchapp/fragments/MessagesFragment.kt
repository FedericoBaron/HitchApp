package com.example.hitchapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.hitchapp.R
import com.example.hitchapp.activities.MainActivity
import com.example.hitchapp.adapters.MessagesAdapter
import com.example.hitchapp.helpers.EndlessRecyclerViewScrollListener
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.example.hitchapp.viewmodels.MessagesFragmentViewModel
import com.parse.ParseCloud
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import java.util.HashMap

class MessagesFragment : Fragment() {
    private var rvMessages: RecyclerView? = null
    private var currentUser: User? = null
    private var layoutManager: LinearLayoutManager? = null
    private var ride: Ride? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var mMessagesFragmentViewModel: MessagesFragmentViewModel? = null

    // Keep track of initial load to scroll to the bottom of the ListView
    var mFirstLoad = false

    // Views
    private var etMessage: EditText? = null
    private var btnSend: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        (activity as MainActivity?)?.setToolbar(false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMessages = view.findViewById(R.id.rvMessages)
        val itemDecoration: ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvMessages?.addItemDecoration(itemDecoration)

        currentUser = ParseUser.getCurrentUser() as User

        // Unwrap the ride passed in via bundle, using its simple name as a key
        val bundle = this.arguments
        ride = bundle?.getParcelable<Parcelable>("ride") as Ride?

        ride?.let { startViewModel(it) }
        initRecyclerView()

        setupMessagePosting()

        // Listens for when you need to load more data
        createScrollListener()

        liveQuery()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.miSearch)
        item.isVisible = false
    }

    protected fun startViewModel(ride: Ride) {
        mMessagesFragmentViewModel = ViewModelProviders.of(this).get(MessagesFragmentViewModel::class.java)
        mMessagesFragmentViewModel?.init(ride)

        // Create the observer which updates the UI.
        val messagesObserver: Observer<List<Message>?> = Observer { messages -> // Update the UI

            adapter?.setAll(messages)
            adapter?.notifyDataSetChanged()

            if (mFirstLoad) {
                rvMessages?.scrollToPosition(0)
                mFirstLoad = false
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mMessagesFragmentViewModel?.messages?.observe(viewLifecycleOwner, messagesObserver)
    }


    protected fun initRecyclerView() {

        // gets list of rides from livedata
        allMessages = ArrayList()

        adapter = context?.let { currentUser?.objectId?.let { it1 -> MessagesAdapter(it, it1, allMessages as ArrayList<Message>) } }

        // Set the adapter on the recycler view
        rvMessages?.adapter = adapter

        // Set the layout manager on the recycler view
        rvMessages?.layoutManager = LinearLayoutManager(context)
        layoutManager = rvMessages?.layoutManager as LinearLayoutManager?
        layoutManager?.reverseLayout = true

        mMessagesFragmentViewModel?.queryMessages()
    }

    // Setup button event handler which posts the entered message to Parse
    fun setupMessagePosting() {
        // Find the text field and button

        etMessage = view?.findViewById<View>(R.id.etMessage) as EditText
        btnSend = view?.findViewById<View>(R.id.btnSend) as Button
        // When send button is clicked, create message object on Parse
        btnSend?.setOnClickListener {

            val content = etMessage?.text.toString()
            val message = Message()
            message.author = currentUser
            message.content = content
            message.authorId = currentUser?.objectId
            message.rideId = ride?.objectId

            message.saveInBackground()

            notifyChannel(ride?.objectId.toString(), message?.content.toString())

            etMessage?.text = null
            mMessagesFragmentViewModel?.queryMessages()
        }
    }

    private fun notifyChannel(channel: String, message: String) {
        var params: HashMap<String, String> = HashMap()
        params["channel"] = channel
        params["message"] = message

        try {
            ParseCloud.callFunctionInBackground<Any>("sendPushNotificationChannel", params)
        } catch(e: ParseException){
            Log.e(TAG,"Failed to notify!", e)
        }
    }

    private fun save() {
        ride?.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                //Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "update save was successful!")
        }
    }

    private fun liveQuery(){

        //Build Live Query Client
        val parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()

        //Build Query
        var parseQuery = ParseQuery.getQuery<Message>("Message")

        //Build Live Query Listener
        var subscriptionHandling: SubscriptionHandling<Message> = parseLiveQueryClient.subscribe(parseQuery)
        subscriptionHandling.handleSubscribe {
            Log.i(TAG, "subs")
        }

        subscriptionHandling.handleError { query, exception ->
            Log.i(TAG,"exception")
        }
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE){ parseQuery: ParseQuery<Message>, message: Message ->
            val handler = Handler(Looper.getMainLooper())
            handler.post(Runnable {
                mMessagesFragmentViewModel?.queryMessages()
                rvMessages?.scrollToPosition(0)
            })
        }
    }

    // Listens for when you need to load more data
    protected fun createScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore: $page")
                mMessagesFragmentViewModel?.loadMoreData()
            }
        }

        // Adds the scroll listener to the RV
        rvMessages?.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }


    companion object {
        private const val TAG = "MessagesFragment"
        var adapter: MessagesAdapter? = null
        var allMessages: List<Message>? = null
    }
}