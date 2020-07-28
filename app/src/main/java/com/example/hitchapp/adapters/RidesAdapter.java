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
import com.example.hitchapp.fragments.ProfileFragment;
import com.example.hitchapp.models.Request;
import com.example.hitchapp.models.Ride;
import com.example.hitchapp.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.List;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.ViewHolder> {

    private static final String TAG = "RidesAdapter";
    private Context context;
    private List<Ride> rides;

    public RidesAdapter(Context context, List<Ride> rides) {
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
            SimpleDateFormat DateFor = new SimpleDateFormat("MM/dd/yyyy");

            // Bind the ride data to the view elements
            User user = (User) ride.getDriver();
            tvFirstName.setText(user.getFirstName());
            tvLastName.setText(user.getLastName());
            tvFrom.setText(ride.getFrom());
            tvTo.setText(ride.getTo());
            tvDepartureTime.setText(ride.getDepartureTime());
            tvDepartureDate.setText(DateFor.format(ride.getDepartureDate()));
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
                        Fragment fragment = new ProfileFragment();
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
                        final Ride ride = rides.get(position);
                        final User currentUser = (User) ParseUser.getCurrentUser();

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

                        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
                        query.whereEqualTo("ride", ride);
                        query.whereEqualTo("requester", currentUser);
                        query.include("ride");

                        // Finds the posts asynchronously
                        query.getFirstInBackground(new GetCallback<Request>() {
                            @Override
                            public void done(Request object, ParseException e) {
                                  if(object == null){
                                      Request request = new Request();
                                      request.setRequester(currentUser);
                                      request.setRide(ride);
                                      request.setDriver(ride.getDriver());
                                      save(request);
                                  }
                                  else{
                                      Toast.makeText(context, "You already requested to join this ride", Toast.LENGTH_SHORT).show();
                                  }
                            }
                        });
                    }
                }
            });
        }

        private void save(Request request) {
            request.saveInBackground(new SaveCallback() {

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
