package com.example.hitchapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private static final String TAG = "MessagesFragment";

    private RecyclerView rvMessages;
    public static MessagesAdapter adapter;
    public static List<Message> allMessages;
    private User currentUser;
    private Conversation conversation;
    private LinearLayoutManager layoutManager;
    private JSONArray participants;
    private int totalMessages = 20;
    private final int REQUEST_CODE = 20;

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

        Log.i(TAG, "You are in here now");

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

        // Unwrap the user passed in via bundle, using its simple name as a key
        conversation = Parcels.unwrap(getArguments().getParcelable("conversation"));

        // Gets the array of likes
        participants = conversation.getParticipants();

        participants.put(currentUser);
        conversation.setParticipants(participants);
        save();




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
                // Go through each participant
                for(int i = 0; i < participants.length(); i++){
                    Log.i(TAG, "looping through the participants");
                    try {
                        // gets the user at array position i
                        Gson gson = new Gson();
                        Log.i(TAG, participants.getJSONObject(i).toString());
                        User participant = (User) gson.fromJson(participants.getJSONObject(i).toString(), User.class);
                        //User participant = (User) participants.getJSONObject(i);
                        Log.i(TAG, participant.getObjectId());

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                //Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
