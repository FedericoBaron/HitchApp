package com.example.hitchapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hitchapp.EndlessRecyclerViewScrollListener;
import com.example.hitchapp.R;
import com.example.hitchapp.adapters.ConversationsAdapter;
import com.example.hitchapp.adapters.MessagesAdapter;
import com.example.hitchapp.models.Conversation;
import com.example.hitchapp.models.Message;
import com.example.hitchapp.models.User;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessagesFragment extends Fragment {

    private static final String TAG = "MessagesFragment";

    private RecyclerView rvMessages;
    public static MessagesAdapter adapter;
    public static List<Message> allMessages;
    public JSONArray messages;
    private User currentUser;
    private Conversation conversation;
    private LinearLayoutManager layoutManager;
    private JSONArray participants;
    private int num_messages = 20;
    private View view;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    // Views
    private EditText etMessage;
    private Button btnSend;


    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        Log.i(TAG, "You are in here now");

        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);

        rvMessages = view.findViewById(R.id.rvMessages);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvMessages.addItemDecoration(itemDecoration);

        // Create layout for one row in the list
        // Create the adapter
        allMessages = new ArrayList<>();
        currentUser = (User) ParseUser.getCurrentUser();
        adapter = new MessagesAdapter(getContext(), currentUser.getObjectId(), allMessages);

        // Set the adapter on the recycler view
        rvMessages.setAdapter(adapter);

        // Set the layout manager on the recycler view
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutManager = (LinearLayoutManager) rvMessages.getLayoutManager();
        layoutManager.setReverseLayout(true);

        // Unwrap the user passed in via bundle, using its simple name as a key
        conversation = Parcels.unwrap(getArguments().getParcelable("conversation"));

        // Gets the array of participants
        participants = conversation.getParticipants();

        messages = conversation.getMessages();

        queryMessages();

        setupMessagePosting();



        //participants.put(currentUser.getObjectId());
        //conversation.setParticipants(participants);
        //save();

    }

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new android.os.Handler();
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            queryMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        btnSend = (Button) view.findViewById(R.id.btnSend);
        // When send button is clicked, create message object on Parse
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etMessage.getText().toString();
                Message message = new Message();
                message.setAuthor(currentUser);
                message.setContent(content);
                message.setAuthorId(currentUser.getObjectId());
                messages.put(message);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Log.i(TAG, "sucess!!!!!!");
                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
                etMessage.setText(null);
                queryMessages();
            }
        });
    }

    // Gets posts and notifies adapter
    protected void queryMessages(){

        //pbLoading.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        //query.whereContainedIn("objectId", (Collection<?>) participants);
        //query.include(Message.KEY_USER);

        // Set a limit
        query.setLimit(20);

        // Sort by created at
        query.addDescendingOrder(Message.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }


                for(Message message: messages){
                    // fetch author data
                    try {
                        message.getAuthor().fetch();
                    } catch(Exception ex){
                        Log.e(TAG, "Couldn't fetch author", ex);
                    }
                    Log.i(TAG, "Content: " + message.getContent() + ", username: " + message.getAuthor().getUsername());
                }

                // Add posts to adapter
                adapter.setAll(messages);
                adapter.notifyDataSetChanged();
                if (mFirstLoad) {
                    rvMessages.scrollToPosition(0);
                    mFirstLoad = false;
                }

            }
        });
    }

    private void save(){
        conversation.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error while saving", e);
                    //Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "update save was successful!");

            }
        });
    }


    //        try {
//            User user = participants.getJSONObject(0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        List<User> users = new ArrayList<>();
//        try {
//            users = User.fromJsonArray(participants);
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//
//        for(int i = 0; i < users.size(); i++){
//            Log.i(TAG, users.get(i).getCollege());
//        }
    // Go through each participant
//                for(int i = 0; i < participants.length(); i++){
//                    Log.i(TAG, "looping through the participants");
//                    try {
//                        // gets the user at array position i
//                        //Gson gson = new Gson();
//                        //Log.i(TAG, participants.getJSONObject(i).toString());
//                        //User participant = (User) gson.fromJson(participants.getJSONObject(i).toString(), User.class);
//                        //User participant = (User) participants.getJSONObject(i);
//                        User participant = new User(fromj.getJSONObject(i);
//                        Log.i(TAG, participant.getObjectId());
//
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                }

    //Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();

}
