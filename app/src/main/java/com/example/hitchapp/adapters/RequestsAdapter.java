package com.example.hitchapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hitchapp.R;
import com.example.hitchapp.fragments.DriverProfileFragment;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private static final String TAG = "RidesAdapter";
    private Context context;
    private List<Ride> rides;

    public RequestsAdapter(Context context, List<Ride> rides) {
        this.context = context;
        this.rides = rides;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.bind(ride);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        rides.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Ride> list) {
        rides.addAll(list);
        notifyDataSetChanged();
    }

    public void setAll(List<Ride> list) {
        clear();
        addAll(list);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvFirstName;
        private TextView tvLastName;
        private ImageView ivProfilePicture;
        private TextView tvFrom;
        private TextView tvTo;
        private TextView tvDepartureTime;
        private TextView tvDepartureDate;
        private TextView tvPrice;
        protected Button btnRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFirstName = itemView.findViewById(R.id.tvFirstName);
            tvLastName = itemView.findViewById(R.id.tvLastName);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvDepartureDate = itemView.findViewById(R.id.tvDepartureDate);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicure);
            btnRequest = itemView.findViewById(R.id.btnRequest);

            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);

            // Listens for driver profile pic clicked
            profilePicListener();

            // Listens for when someone requests to join ride
            btnRequestListener();

        }

        public void bind(Ride ride) {
            // Bind the ride data to the view elements
            User user = (User) ride.getDriver();
            tvFirstName.setText(user.getFirstName());
            tvLastName.setText(user.getLastName());
            tvFrom.setText(ride.getFrom());
            tvTo.setText(ride.getTo());
            tvDepartureTime.setText(ride.getDepartureTime());
            tvDepartureDate.setText(ride.getDepartureDate());
            tvPrice.setText(String.valueOf(ride.getPrice()));
            if(ParseUser.getCurrentUser().getObjectId().equals(ride.getDriver().getObjectId()))
                btnRequest.setVisibility(View.GONE);
            else{
                btnRequest.setVisibility(View.VISIBLE);
            }

            ParseFile profile = user.getProfilePicture();
            if (profile != null) {
                Glide.with(context)
                        .load(profile.getUrl())
                        .fitCenter()
                        .circleCrop()
                        .into(ivProfilePicture);
            }
        }

        @Override
        public void onClick(View v) {

        }

        // When someone's profile pic gets clicked you get taken to their profile
        private void profilePicListener() {
            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the ride at the position, this won't work if the class is static
                        Ride ride = rides.get(position);
                        Bundle bundle = new Bundle();
                        User user = (User) ride.getDriver();
                        Log.i(TAG, String.valueOf(user));
                        bundle.putParcelable("user", user);
                        Fragment fragment = new DriverProfileFragment();
                        fragment.setArguments(bundle);
                        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContainer, fragment)
                                .addToBackStack(TAG)
                                .commit();
                    }
                }
            });
        }

        private void btnRequestListener() {
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "clicked on request ride");
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the ride at the position, this won't work if the class is static
                        Ride ride = rides.get(position);
                        User currentUser = (User) ParseUser.getCurrentUser();

                        List<User> participantList = ride.getList("participants");
                        Log.i(TAG, String.valueOf(participantList.size()));
                        for(int i = 0; i < participantList.size(); i++) {
                            Log.i(TAG, "welcome part");
                            try {
                                participantList.get(i).fetch();
                                Log.i(TAG, participantList.get(i).getObjectId());
                                if(currentUser.getObjectId().equals(participantList.get(i).getObjectId())){
                                    Toast.makeText(context, "You are already a participant", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "WELCOME HOME");
                                    return;
                                }
                            } catch (ParseException e) {
                                Log.e(TAG, "exception fetching participants", e);
                            }
                        }

                        // Saves the current user to the request list
                        List<User> requestList = ride.getList("requests");
                        JSONArray requests = (JSONArray) requestList;
                        if(requestList == null){
                            requests = new JSONArray();
                        }
                        else{

                            Log.i(TAG, String.valueOf(requestList.size()));
                            for(int i = 0; i < requestList.size(); i++){
                                try{
                                    requestList.get(i).fetch();
                                    if(currentUser.getObjectId().equals(participantList.get(i).getObjectId())){
                                        Toast.makeText(context, "You already requested to join this ride", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (ParseException e){
                                    Log.e(TAG, "exception fetching requests");
                                }
                            }
                        }
                        requests.put(currentUser);
                        ride.setRequests(requests);
                        save(ride);
                    }
                }
            });
        }

        private void save(Ride ride) {
            ride.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving", e);
                        //Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "update save was successful!");
                    Toast.makeText(context, "You have requested to join this ride", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
}
