package com.example.hitchapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.hitchapp.R
import com.example.hitchapp.adapters.MessagesAdapter
import com.example.hitchapp.models.Message
import com.example.hitchapp.models.Ride
import com.example.hitchapp.models.User
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import org.json.JSONArray

class MessagesFragment : Fragment() {
    private var rvMessages: RecyclerView? = null
    var messages: JSONArray? = null
    private var currentUser: User? = null
    private var layoutManager: LinearLayoutManager? = null
    private val participants: JSONArray? = null
    private val num_messages = 20
    private var ride: Ride? = null

    // Keep track of initial load to scroll to the bottom of the ListView
    var mFirstLoad = false

    // Views
    private var etMessage: EditText? = null
    private var btnSend: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "You are in here now")

        //myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        rvMessages = view.findViewById(R.id.rvMessages)
        val itemDecoration: ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvMessages?.addItemDecoration(itemDecoration)

        // Create layout for one row in the list
        // Create the adapter
        allMessages = ArrayList()
        currentUser = ParseUser.getCurrentUser() as User
        adapter = MessagesAdapter(context, currentUser?.objectId, allMessages)

        // Set the adapter on the recycler view
        rvMessages?.setAdapter(adapter)

        // Set the layout manager on the recycler view
        rvMessages?.setLayoutManager(LinearLayoutManager(context))
        layoutManager = rvMessages?.getLayoutManager() as LinearLayoutManager?
        layoutManager?.reverseLayout = false

        // Unwrap the user passed in via bundle, using its simple name as a key
        //driver = Parcels.unwrap(getArguments().getParcelable("user"));
        val bundle = this.arguments
        ride = bundle?.getParcelable<Parcelable>("ride") as Ride?
        if (ride?.messages == null){
            ride?.messages = JSONArray()
            save()
        }
        messages = ride?.messages
        queryMessages()
        setupMessagePosting()

        // Make sure the Parse server is setup to configured for live queries
        val parseLiveQueryClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()

        val parseQuery: ParseQuery<Message> = ParseQuery.getQuery(Message::class.java)

        // Connect to Parse server
        val subscriptionHandling: SubscriptionHandling<Message> = parseLiveQueryClient.subscribe(parseQuery)
        subscriptionHandling.handleSubscribe {
            Log.i(TAG, "Subscribed")
        }
        subscriptionHandling.handleEvents { query, event, `object` ->
            Log.i(TAG, "handle")
        }
        subscriptionHandling.handleError { query, exception ->
            Log.i(TAG, "ERROR")
        }

        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE) { query, `object` ->
            val handler = Handler(Looper.getMainLooper())
            Log.i(TAG, "trying to test")
            handler.post(Runnable {
                Log.i(TAG, "runOnUiThread")
                adapter?.notifyDataSetChanged()
                rvMessages?.scrollToPosition(0)
            })
        }
        Log.i(TAG, "Random test")

        // Listen for CREATE events
        // Listen for CREATE events
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE) { query, `object` ->
//            mMessages.add(0, `object`)
//
//            // RecyclerView updates need to be run on the UI thread
//            runOnUiThread(Runnable {
//                mAdapter.notifyDataSetChanged()
//                rvChat.scrollToPosition(0)
//            })
//        }


        // Listen for CREATE events
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE) { query, `object` ->
//            messages?.put(`object`)
//
//            Log.i(TAG ,"You are here now")
//            // RecyclerView updates need to be run on the UI thread
//            activity?.runOnUiThread(
//                    object : Runnable {
//                        override fun run() {
//                            Log.i(TAG, "runOnUiThread")
//                            adapter?.notifyDataSetChanged()
//                            rvMessages?.scrollToPosition(0)
//                        }
//                    }
//            )
//        }
        // Listen for CREATE events
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, object : SubscriptionHandling.HandleEventCallback<Message?> {
//            override fun onEvent(query: ParseQuery<Message?>?, `object`: Message?) {
//                Log.i(TAG, "MESSAGE RECEIVED")
//
//
//                // RecyclerView updates need to be run on the UI thread
//                // RecyclerView updates need to be run on the UI thread
//
//            }
//        })
    }

    // Create a handler which can run code periodically
    //    static final int POLL_INTERVAL = 1000; // milliseconds
    //    Handler myHandler = new android.os.Handler();
    //    Runnable mRefreshMessagesRunnable = new Runnable() {
    //        @Override
    //        public void run() {
    //            queryMessages();
    //            myHandler.postDelayed(this, POLL_INTERVAL);
    //        }
    //    };
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
            messages?.put(message)
            ride?.messages = messages
            message.saveInBackground();
            save()
            etMessage?.text = null
            queryMessages()
        }
    }

    // Gets posts and notifies adapter
    protected fun queryMessages() {

        val messagesList = ride?.getList<Message>("messages")
        for (i in messagesList?.indices!!) {
            try {
                messagesList[i]?.fetchIfNeeded<Message>()
            } catch (e: ParseException) {
                Log.e(TAG, "exception fetching messages", e)
            }
        }
        adapter?.setAll(messagesList)
        adapter?.notifyDataSetChanged()
        if (mFirstLoad) {
            rvMessages?.scrollToPosition(0)
            mFirstLoad = false
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

    companion object {
        private const val TAG = "MessagesFragment"
        var adapter: MessagesAdapter? = null
        var allMessages: List<Message>? = null
    }
}